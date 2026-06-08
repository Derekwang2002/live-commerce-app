import assert from "node:assert/strict";
import test from "node:test";
import { CommerceStore } from "../src/store.js";

test("cart rejects an out-of-stock sku", () => {
  const store = new CommerceStore();
  assert.throws(() => store.addCartItem({ skuId: "sku_3", quantity: 1 }), /库存不足/);
});

test("order creation reserves stock and snapshots the price", () => {
  const store = new CommerceStore();
  store.addCartItem({ skuId: "sku_1", quantity: 1 });
  const order = store.createOrder();

  assert.equal(store.getStock("prod_1").skus.find((sku) => sku.skuId === "sku_1").stock, 9);
  assert.equal(order.items[0].price, 99);
  assert.equal(order.totalAmount, 99);
  assert.equal(order.discountAmount, 20);
  assert.equal(order.payAmount, 79);
  assert.equal(store.getCart().items.length, 0);
});

test("cancel releases reserved stock exactly once", () => {
  const store = new CommerceStore();
  const order = store.createOrder({ items: [{ skuId: "sku_1", quantity: 2 }] });
  store.cancelOrder(order.id);

  assert.equal(store.getStock("prod_1").skus.find((sku) => sku.skuId === "sku_1").stock, 10);
  assert.throws(() => store.cancelOrder(order.id), /不可取消/);
  assert.equal(store.getStock("prod_1").skus.find((sku) => sku.skuId === "sku_1").stock, 10);
});

test("order creation merges duplicate sku lines before validating stock", () => {
  const store = new CommerceStore();

  assert.throws(() => store.createOrder({
    items: [
      { skuId: "sku_1", quantity: 6 },
      { skuId: "sku_1", quantity: 6 }
    ]
  }), /库存不足/);

  assert.equal(store.getStock("prod_1").skus.find((sku) => sku.skuId === "sku_1").stock, 10);
});

test("payment success is idempotent and does not allow another payment", () => {
  const store = new CommerceStore();
  const order = store.createOrder({ items: [{ skuId: "sku_1", quantity: 1 }] });
  const payment = store.createPayment({ orderId: order.id });

  assert.equal(store.completePayment(payment.paymentNo, true).status, "SUCCESS");
  assert.equal(store.completePayment(payment.paymentNo, true).status, "SUCCESS");
  assert.equal(store.getOrder(order.id).status, "PAID");
  assert.throws(() => store.createPayment({ orderId: order.id }), /已支付或已关闭/);
});

test("failed payment preserves the order and allows a new attempt", () => {
  const store = new CommerceStore();
  const order = store.createOrder({ items: [{ skuId: "sku_1", quantity: 1 }] });
  const firstPayment = store.createPayment({ orderId: order.id });

  assert.equal(store.completePayment(firstPayment.paymentNo, false).status, "FAILED");
  assert.equal(store.getOrder(order.id).status, "PENDING_PAYMENT");
  assert.notEqual(store.createPayment({ orderId: order.id }).paymentNo, firstPayment.paymentNo);
});

test("expired order releases stock and cannot be paid", () => {
  let now = 1_000;
  const store = new CommerceStore({ now: () => now });
  const order = store.createOrder({ items: [{ skuId: "sku_1", quantity: 1 }] });
  const payment = store.createPayment({ orderId: order.id });
  now += 16 * 60 * 1000;

  assert.equal(store.getOrder(order.id).status, "CANCELLED");
  assert.equal(store.getStock("prod_1").skus.find((sku) => sku.skuId === "sku_1").stock, 10);
  assert.throws(() => store.completePayment(payment.paymentNo, true), /已支付或已关闭/);
});

test("AIGC endpoints provide deterministic fallback copy", () => {
  const store = new CommerceStore();
  assert.match(store.generateTitle({ productId: "prod_1" }).title, /直播间限时福利/);
  assert.equal(store.generateSellingPoints({ productId: "prod_1" }).sellingPoints.length, 3);
  assert.match(store.generateScript({ productId: "prod_1" }).script, /先选规格再下单/);
});
