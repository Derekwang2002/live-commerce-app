# Live Commerce Backend

Zero-dependency Node.js API for the P4 live-commerce transaction flow.

## Run

```bash
npm test
npm start
```

The server listens on `http://localhost:3000` by default. Use `x-user-id` to switch the mock user; it defaults to `user_1`.

## API

- `GET /products/batch?ids=prod_1,prod_2`
- `GET /products/:productId`
- `GET /products/:productId/stock`
- `GET /cart`
- `POST /cart/items`
- `PATCH /cart/items/:cartItemId`
- `DELETE /cart/items/:cartItemId`
- `PATCH /cart/items/selection`
- `POST /orders`
- `GET /orders`
- `GET /orders/:orderId`
- `POST /orders/:orderId/cancel`
- `POST /payments`
- `GET /payments/:paymentNo`
- `POST /payments/:paymentNo/mock-success`
- `POST /payments/:paymentNo/mock-fail`
- `POST /aigc/title`
- `POST /aigc/selling-points`
- `POST /aigc/script`

## Design boundary

This phase keeps persistence in memory so the transaction behavior can be demonstrated without external services. `CommerceStore` is the replacement boundary for a future database-backed repository. Prices are calculated in cents internally, order items keep snapshots, order creation reserves stock, and cancellation or expiration releases stock exactly once.
