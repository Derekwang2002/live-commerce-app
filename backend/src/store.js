import { randomUUID } from "node:crypto";
import { coupons, defaultAddress, products as seedProducts } from "./data.js";
import { assert } from "./errors.js";

const clone = (value) => structuredClone(value);
const amount = (cents) => Number((cents / 100).toFixed(2));

function publicSku(productId, sku) {
  return { ...sku, productId, price: amount(sku.priceCents), priceCents: undefined };
}

function publicProduct(product) {
  return {
    ...product,
    price: amount(product.priceCents),
    originalPrice: amount(product.originalPriceCents),
    priceCents: undefined,
    originalPriceCents: undefined,
    skus: product.skus.map((sku) => publicSku(product.id, sku))
  };
}

function publicCoupon(coupon) {
  if (!coupon) return null;
  return {
    id: coupon.id,
    title: coupon.title,
    threshold: amount(coupon.thresholdCents),
    discountAmount: amount(coupon.discountCents),
    expiresInText: coupon.expiresInText,
    productIds: coupon.productIds
  };
}

export class CommerceStore {
  constructor({ now = () => Date.now() } = {}) {
    this.now = now;
    this.reset();
  }

  reset() {
    this.products = clone(seedProducts);
    this.carts = new Map();
    this.orders = [];
    this.payments = [];
  }

  listProducts(ids = []) {
    const selected = ids.length ? this.products.filter((product) => ids.includes(product.id)) : this.products;
    return selected.map(publicProduct);
  }

  getProduct(productId) {
    return publicProduct(this.requireProduct(productId));
  }

  getStock(productId) {
    const product = this.requireProduct(productId);
    return {
      productId,
      skus: product.skus.map(({ id, stock }) => ({ skuId: id, stock }))
    };
  }

  getCart(userId = "user_1") {
    return this.cartSummary(this.getCartItems(userId));
  }

  addCartItem({ userId = "user_1", skuId, quantity = 1 }) {
    const { product, sku } = this.requirePurchasableSku(skuId);
    this.assertQuantity(quantity);
    const items = this.getCartItems(userId);
    const existing = items.find((item) => item.skuId === skuId);
    if (existing) {
      assert(existing.quantity + quantity <= sku.stock, 409, "INSUFFICIENT_STOCK", `${product.title} 库存不足，仅剩 ${sku.stock} 件`);
      existing.quantity += quantity;
      existing.selected = true;
    } else {
      assert(quantity <= sku.stock, 409, "INSUFFICIENT_STOCK", `${product.title} 库存不足，仅剩 ${sku.stock} 件`);
      items.push({ id: `cart_${randomUUID()}`, productId: product.id, skuId, quantity, selected: true });
    }
    return this.cartSummary(items);
  }

  updateCartItem(cartItemId, { userId = "user_1", quantity }) {
    this.assertQuantity(quantity);
    const items = this.getCartItems(userId);
    const item = this.requireCartItem(items, cartItemId);
    const { product, sku } = this.requirePurchasableSku(item.skuId);
    assert(quantity <= sku.stock, 409, "INSUFFICIENT_STOCK", `${product.title} 库存不足，仅剩 ${sku.stock} 件`);
    item.quantity = quantity;
    return this.cartSummary(items);
  }

  removeCartItem(cartItemId, userId = "user_1") {
    const items = this.getCartItems(userId);
    const index = items.findIndex((item) => item.id === cartItemId);
    assert(index >= 0, 404, "CART_ITEM_NOT_FOUND", "购物车商品不存在");
    items.splice(index, 1);
    return this.cartSummary(items);
  }

  updateCartSelection({ userId = "user_1", cartItemIds, selected }) {
    assert(Array.isArray(cartItemIds), 400, "INVALID_CART_ITEM_IDS", "cartItemIds 必须是数组");
    assert(typeof selected === "boolean", 400, "INVALID_SELECTED", "selected 必须是布尔值");
    const items = this.getCartItems(userId);
    for (const item of items) {
      if (cartItemIds.includes(item.id)) item.selected = selected;
    }
    return this.cartSummary(items);
  }

  createOrder({ userId = "user_1", address = defaultAddress, items: requestedItems } = {}) {
    const cartItems = this.getCartItems(userId);
    const rawItems = requestedItems?.length
      ? requestedItems.map(({ skuId, quantity }) => ({ skuId, quantity }))
      : cartItems.filter((item) => item.selected);
    const sourceItems = [...rawItems.reduce((itemsBySku, item) => {
      this.assertQuantity(item.quantity);
      const previous = itemsBySku.get(item.skuId);
      itemsBySku.set(item.skuId, {
        skuId: item.skuId,
        quantity: (previous?.quantity ?? 0) + item.quantity
      });
      return itemsBySku;
    }, new Map()).values()];
    assert(sourceItems.length > 0, 400, "EMPTY_CHECKOUT", "请先选择可结算商品");

    // Validate the full order before mutating stock so a rejected order has no side effects.
    const resolvedItems = sourceItems.map((item) => {
      const { product, sku } = this.requirePurchasableSku(item.skuId);
      assert(item.quantity <= sku.stock, 409, "INSUFFICIENT_STOCK", `${product.title} 库存不足，仅剩 ${sku.stock} 件`);
      return { source: item, product, sku };
    });

    for (const { source, sku } of resolvedItems) sku.stock -= source.quantity;

    const totalCents = resolvedItems.reduce((sum, { source, sku }) => sum + sku.priceCents * source.quantity, 0);
    const coupon = this.bestCoupon(totalCents, resolvedItems.map(({ product }) => product.id));
    const discountCents = coupon?.discountCents ?? 0;
    const shippingCents = totalCents >= 9900 ? 0 : 800;
    const order = {
      id: `order_${randomUUID()}`,
      orderNo: `LC${this.now()}${Math.floor(Math.random() * 1000).toString().padStart(3, "0")}`,
      userId,
      address: clone(address),
      items: resolvedItems.map(({ source, product, sku }) => ({
        id: `order_item_${randomUUID()}`,
        productId: product.id,
        skuId: sku.id,
        productTitle: product.title,
        coverUrl: product.coverUrl,
        skuSpecs: clone(sku.specs),
        priceCents: sku.priceCents,
        quantity: source.quantity,
        subtotalCents: sku.priceCents * source.quantity
      })),
      totalCents,
      discountCents,
      shippingCents,
      payCents: Math.max(0, totalCents - discountCents + shippingCents),
      status: "PENDING_PAYMENT",
      payExpireAt: this.now() + 15 * 60 * 1000,
      coupon,
      createdAt: this.now(),
      stockReleased: false
    };
    this.orders.unshift(order);

    if (!requestedItems?.length) {
      const selectedIds = new Set(cartItems.filter((item) => item.selected).map((item) => item.id));
      this.carts.set(userId, cartItems.filter((item) => !selectedIds.has(item.id)));
    }
    return this.publicOrder(order);
  }

  listOrders(userId = "user_1") {
    this.expireOrders();
    return this.orders.filter((order) => order.userId === userId).map((order) => this.publicOrder(order));
  }

  getOrder(orderId) {
    this.expireOrders();
    return this.publicOrder(this.requireOrder(orderId));
  }

  cancelOrder(orderId) {
    const order = this.requireOrder(orderId);
    assert(order.status === "PENDING_PAYMENT", 409, "ORDER_CANNOT_CANCEL", "当前订单状态不可取消");
    order.status = "CANCELLED";
    this.releaseStock(order);
    return this.publicOrder(order);
  }

  createPayment({ orderId }) {
    this.expireOrders();
    const order = this.requireOrder(orderId);
    assert(order.status === "PENDING_PAYMENT", 409, "ORDER_CANNOT_PAY", "订单已支付或已关闭");
    const existing = this.payments.find((payment) => payment.orderId === order.id && payment.status === "PENDING");
    if (existing) return this.publicPayment(existing);
    const payment = {
      id: `pay_${randomUUID()}`,
      orderId,
      paymentNo: `PAY${this.now()}${Math.floor(Math.random() * 1000).toString().padStart(3, "0")}`,
      amountCents: order.payCents,
      status: "PENDING",
      createdAt: this.now()
    };
    this.payments.unshift(payment);
    return this.publicPayment(payment);
  }

  getPayment(paymentNo) {
    return this.publicPayment(this.requirePayment(paymentNo));
  }

  completePayment(paymentNo, success) {
    this.expireOrders();
    const payment = this.requirePayment(paymentNo);
    const order = this.requireOrder(payment.orderId);
    if (payment.status === "SUCCESS") return this.publicPayment(payment);
    assert(order.status === "PENDING_PAYMENT", 409, "ORDER_CANNOT_PAY", "订单已支付或已关闭");
    payment.status = success ? "SUCCESS" : "FAILED";
    payment.failedReason = success ? null : "模拟支付失败：余额不足或网络中断";
    if (success) {
      payment.paidAt = this.now();
      order.status = "PAID";
      order.paidAt = payment.paidAt;
    }
    return this.publicPayment(payment);
  }

  generateTitle({ productId }) {
    const product = this.requireProduct(productId);
    return { productId, title: `${product.title}｜直播间限时福利` };
  }

  generateSellingPoints({ productId }) {
    const product = this.requireProduct(productId);
    return {
      productId,
      sellingPoints: [
        `${product.title} 本场直播专享价`,
        product.guaranteeTags.join("、"),
        product.skus.some((sku) => sku.stock <= product.lowStockThreshold && sku.stock > 0) ? "部分规格库存紧张" : "热门规格库存充足"
      ]
    };
  }

  generateScript({ productId }) {
    const product = this.requireProduct(productId);
    return {
      productId,
      script: `正在看直播的朋友可以关注一下${product.title}。${product.description} 支持${product.guaranteeTags.join("、")}，需要的朋友可以先选规格再下单。`
    };
  }

  expireOrders() {
    for (const order of this.orders) {
      if (order.status === "PENDING_PAYMENT" && order.payExpireAt <= this.now()) {
        order.status = "CANCELLED";
        this.releaseStock(order);
      }
    }
  }

  releaseStock(order) {
    if (order.stockReleased) return;
    for (const item of order.items) {
      const { sku } = this.requireSku(item.skuId);
      sku.stock += item.quantity;
    }
    order.stockReleased = true;
  }

  getCartItems(userId) {
    if (!this.carts.has(userId)) this.carts.set(userId, []);
    return this.carts.get(userId);
  }

  cartSummary(items) {
    const hydrated = items.map((item) => {
      const { product, sku } = this.requireSku(item.skuId);
      const invalidReason = product.status !== "ON_SALE"
        ? "商品已下架"
        : sku.stock < item.quantity
          ? `库存不足，仅剩 ${sku.stock} 件`
          : null;
      return {
        ...item,
        selected: invalidReason ? false : item.selected,
        invalidReason,
        product: publicProduct(product),
        sku: publicSku(product.id, sku)
      };
    });
    const selectedItems = hydrated.filter((item) => item.selected && !item.invalidReason);
    const selectedTotalCents = selectedItems.reduce((sum, item) => {
      const { sku } = this.requireSku(item.skuId);
      return sum + sku.priceCents * item.quantity;
    }, 0);
    return {
      items: hydrated,
      totalPrice: amount(selectedTotalCents),
      totalCount: hydrated.reduce((sum, item) => sum + item.quantity, 0),
      selectedCount: selectedItems.reduce((sum, item) => sum + item.quantity, 0),
      coupon: publicCoupon(this.bestCoupon(selectedTotalCents, selectedItems.map((item) => item.productId)))
    };
  }

  bestCoupon(totalCents, productIds) {
    return coupons
      .filter((coupon) => totalCents >= coupon.thresholdCents)
      .filter((coupon) => !coupon.productIds.length || coupon.productIds.some((id) => productIds.includes(id)))
      .sort((a, b) => b.discountCents - a.discountCents)[0] ?? null;
  }

  publicOrder(order) {
    return {
      ...order,
      items: order.items.map((item) => ({
        ...item,
        price: amount(item.priceCents),
        subtotal: amount(item.subtotalCents),
        priceCents: undefined,
        subtotalCents: undefined
      })),
      totalAmount: amount(order.totalCents),
      discountAmount: amount(order.discountCents),
      shippingAmount: amount(order.shippingCents),
      payAmount: amount(order.payCents),
      coupon: publicCoupon(order.coupon),
      totalCents: undefined,
      discountCents: undefined,
      shippingCents: undefined,
      payCents: undefined,
      stockReleased: undefined
    };
  }

  publicPayment(payment) {
    return { ...payment, amount: amount(payment.amountCents), amountCents: undefined };
  }

  requireProduct(productId) {
    const product = this.products.find((candidate) => candidate.id === productId);
    assert(product, 404, "PRODUCT_NOT_FOUND", "商品不存在");
    return product;
  }

  requireSku(skuId) {
    for (const product of this.products) {
      const sku = product.skus.find((candidate) => candidate.id === skuId);
      if (sku) return { product, sku };
    }
    assert(false, 404, "SKU_NOT_FOUND", "商品规格不存在");
  }

  requirePurchasableSku(skuId) {
    const { product, sku } = this.requireSku(skuId);
    assert(product.status === "ON_SALE", 409, "PRODUCT_OFF_SALE", `${product.title} 已下架`);
    assert(sku.stock > 0, 409, "INSUFFICIENT_STOCK", `${product.title} 当前规格库存不足`);
    return { product, sku };
  }

  requireCartItem(items, cartItemId) {
    const item = items.find((candidate) => candidate.id === cartItemId);
    assert(item, 404, "CART_ITEM_NOT_FOUND", "购物车商品不存在");
    return item;
  }

  requireOrder(orderId) {
    const order = this.orders.find((candidate) => candidate.id === orderId);
    assert(order, 404, "ORDER_NOT_FOUND", "订单不存在");
    return order;
  }

  requirePayment(paymentNo) {
    const payment = this.payments.find((candidate) => candidate.paymentNo === paymentNo);
    assert(payment, 404, "PAYMENT_NOT_FOUND", "支付单不存在");
    return payment;
  }

  assertQuantity(quantity) {
    assert(Number.isInteger(quantity) && quantity > 0, 400, "INVALID_QUANTITY", "quantity 必须是正整数");
  }
}
