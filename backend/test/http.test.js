import assert from "node:assert/strict";
import test from "node:test";
import { createApp } from "../src/app.js";

async function withServer(run) {
  const server = createApp();
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  const address = server.address();
  try {
    await run(`http://127.0.0.1:${address.port}`);
  } finally {
    await new Promise((resolve) => server.close(resolve));
  }
}

test("HTTP API completes cart to payment success flow", async () => {
  await withServer(async (baseUrl) => {
    const addResponse = await fetch(`${baseUrl}/cart/items`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ skuId: "sku_1", quantity: 1 })
    });
    assert.equal(addResponse.status, 201);

    const orderResponse = await fetch(`${baseUrl}/orders`, { method: "POST" });
    assert.equal(orderResponse.status, 201);
    const order = await orderResponse.json();

    const paymentResponse = await fetch(`${baseUrl}/payments`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ orderId: order.id })
    });
    assert.equal(paymentResponse.status, 201);
    const payment = await paymentResponse.json();

    const successResponse = await fetch(`${baseUrl}/payments/${payment.paymentNo}/mock-success`, { method: "POST" });
    assert.equal(successResponse.status, 200);

    const finalOrder = await fetch(`${baseUrl}/orders/${order.id}`).then((response) => response.json());
    assert.equal(finalOrder.status, "PAID");
  });
});

test("HTTP API returns a stable error envelope", async () => {
  await withServer(async (baseUrl) => {
    const response = await fetch(`${baseUrl}/cart/items`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ skuId: "sku_3", quantity: 1 })
    });
    assert.equal(response.status, 409);
    assert.deepEqual(await response.json(), {
      error: {
        code: "INSUFFICIENT_STOCK",
        message: "重磅埃及短袖 当前规格库存不足"
      }
    });
  });
});

test("HTTP API updates cart selection without treating selection as an item id", async () => {
  await withServer(async (baseUrl) => {
    const cart = await fetch(`${baseUrl}/cart/items`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ skuId: "sku_1", quantity: 1 })
    }).then((response) => response.json());

    const response = await fetch(`${baseUrl}/cart/items/selection`, {
      method: "PATCH",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ cartItemIds: [cart.items[0].id], selected: false })
    });
    const updatedCart = await response.json();

    assert.equal(response.status, 200);
    assert.equal(updatedCart.items[0].selected, false);
    assert.equal(updatedCart.selectedCount, 0);
  });
});
