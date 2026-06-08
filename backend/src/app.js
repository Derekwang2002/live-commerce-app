import { createServer } from "node:http";
import { AppError } from "./errors.js";
import { CommerceStore } from "./store.js";

async function readJson(request) {
  const chunks = [];
  for await (const chunk of request) chunks.push(chunk);
  if (!chunks.length) return {};
  try {
    return JSON.parse(Buffer.concat(chunks).toString("utf8"));
  } catch {
    throw new AppError(400, "INVALID_JSON", "请求体必须是合法 JSON");
  }
}

function send(response, statusCode, data) {
  response.writeHead(statusCode, { "content-type": "application/json; charset=utf-8" });
  response.end(JSON.stringify(data));
}

export function createApp(store = new CommerceStore()) {
  return createServer(async (request, response) => {
    try {
      const url = new URL(request.url, "http://localhost");
      const path = url.pathname;
      const method = request.method;
      const userId = request.headers["x-user-id"] || "user_1";
      const body = ["POST", "PATCH"].includes(method) ? await readJson(request) : {};

      if (method === "GET" && path === "/health") return send(response, 200, { ok: true });
      if (method === "GET" && path === "/products/batch") return send(response, 200, { products: store.listProducts(url.searchParams.get("ids")?.split(",").filter(Boolean) ?? []) });

      let match;
      if (method === "GET" && (match = path.match(/^\/products\/([^/]+)$/))) return send(response, 200, store.getProduct(match[1]));
      if (method === "GET" && (match = path.match(/^\/products\/([^/]+)\/stock$/))) return send(response, 200, store.getStock(match[1]));
      if (method === "GET" && path === "/cart") return send(response, 200, store.getCart(userId));
      if (method === "POST" && path === "/cart/items") return send(response, 201, store.addCartItem({ ...body, userId }));
      if (method === "PATCH" && path === "/cart/items/selection") return send(response, 200, store.updateCartSelection({ ...body, userId }));
      if (method === "PATCH" && (match = path.match(/^\/cart\/items\/([^/]+)$/))) return send(response, 200, store.updateCartItem(match[1], { ...body, userId }));
      if (method === "DELETE" && (match = path.match(/^\/cart\/items\/([^/]+)$/))) return send(response, 200, store.removeCartItem(match[1], userId));
      if (method === "POST" && path === "/orders") return send(response, 201, store.createOrder({ ...body, userId }));
      if (method === "GET" && path === "/orders") return send(response, 200, { orders: store.listOrders(userId) });
      if (method === "GET" && (match = path.match(/^\/orders\/([^/]+)$/))) return send(response, 200, store.getOrder(match[1]));
      if (method === "POST" && (match = path.match(/^\/orders\/([^/]+)\/cancel$/))) return send(response, 200, store.cancelOrder(match[1]));
      if (method === "POST" && path === "/payments") return send(response, 201, store.createPayment(body));
      if (method === "GET" && (match = path.match(/^\/payments\/([^/]+)$/))) return send(response, 200, store.getPayment(match[1]));
      if (method === "POST" && (match = path.match(/^\/payments\/([^/]+)\/mock-success$/))) return send(response, 200, store.completePayment(match[1], true));
      if (method === "POST" && (match = path.match(/^\/payments\/([^/]+)\/mock-fail$/))) return send(response, 200, store.completePayment(match[1], false));
      if (method === "POST" && path === "/aigc/title") return send(response, 200, store.generateTitle(body));
      if (method === "POST" && path === "/aigc/selling-points") return send(response, 200, store.generateSellingPoints(body));
      if (method === "POST" && path === "/aigc/script") return send(response, 200, store.generateScript(body));

      throw new AppError(404, "ROUTE_NOT_FOUND", "接口不存在");
    } catch (error) {
      if (error instanceof AppError) {
        return send(response, error.statusCode, { error: { code: error.code, message: error.message } });
      }
      console.error(error);
      return send(response, 500, { error: { code: "INTERNAL_ERROR", message: "服务暂时不可用" } });
    }
  });
}
