export class AppError extends Error {
  constructor(statusCode, code, message) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
  }
}

export function assert(condition, statusCode, code, message) {
  if (!condition) throw new AppError(statusCode, code, message);
}
