## 文档结构
```bash
live-commerce-app/
├── mobile/      # 移动端
├── backend/     # 后端
├── admin/       # 商家后台
├── docs/        # 项目文档
└── docker/      # Docker 配置
```

## Git branches
```
main
└── dev
    ├── feature/ecommerce
    └── feature/feed
```
详见 [Git 协作约定](docs/conventions/git.md)

## Features
- 移动端核心电商链路：商品浮层卡、商品详情、SKU 选择、购物车、订单确认、模拟支付。
- 支付结果页：支持支付成功、支付失败、重新支付和订单详情查看。
- 本地 Mock 数据闭环：商品、优惠券、购物车、订单和支付状态可在客户端内完整演示。

## Milestones
- 5.26 周二：壳工程
- 5.29 周五：技术方案
- 开发 + 测试
- 6.5 周五：总结优化
- 6.9 周二：提交课题
- 准备答辩
