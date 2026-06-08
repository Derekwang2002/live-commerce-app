# 内容流/直播流接入商品页操作记录与回滚说明

日期：2026-06-08

目标：在 `tiktok_flow/TikTok-Compose` 内完成最小融合版接入，让内容流广告 CTA 和直播间商品卡可以跳转到商品详情页。

## 1. 新增商品详情路由常量

文件：

- `tiktok_flow/TikTok-Compose/core/src/main/java/com/puskal/core/DestinationRoute.kt`

操作：

- 新增 `PRODUCT_DETAIL_ROUTE = "product_detail_route"`。
- 新增 `FORMATTED_PRODUCT_DETAIL_ROUTE = "$PRODUCT_DETAIL_ROUTE/{$PRODUCT_ID}"`。
- 在 `PassedKey` 中新增 `PRODUCT_ID = "product_id"`。

用途：

- 给内容流广告和直播商品卡提供统一的 App 内部商品详情路由。

回滚：

- 删除 `PRODUCT_ID` import。
- 删除 `PRODUCT_DETAIL_ROUTE` 和 `FORMATTED_PRODUCT_DETAIL_ROUTE`。
- 删除 `PassedKey.PRODUCT_ID`。

## 2. 新增商品 mock 目录

文件：

- `tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/product/ProductCatalog.kt`

操作：

- 新增 `ProductLanding` 数据模型。
- 新增 `ProductCatalog`，内置 `prod_1` 和 `prod_2` 两个商品。
- `find(productId)` 找不到商品时 fallback 到第一个商品。

用途：

- 用 TikTok-Compose 内部 mock 商品承接商品详情页。
- 当前没有直接复用根项目 `app` 里的商品模型，避免跨 Gradle 项目强耦合。

回滚：

- 删除整个 `ProductCatalog.kt` 文件。

## 3. 新增商品详情页

文件：

- `tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/product/ProductDetailScreen.kt`

操作：

- 新增 `ProductDetailScreen(navController, productId)`。
- 页面展示商品图、价格、原价、销量、标题、描述、卖点、规格。
- 底部预留 `加入购物车` 和 `立即购买` 按钮。
- 返回按钮调用 `navController.popBackStack()`。

用途：

- 作为内容流广告和直播商品卡的统一落地页。

回滚：

- 删除整个 `ProductDetailScreen.kt` 文件。

## 4. 在 home 导航中注册商品详情页

文件：

- `tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/HomeNavigation.kt`

操作：

- import `FORMATTED_PRODUCT_DETAIL_ROUTE`。
- import `PassedKey.PRODUCT_ID`。
- import `ProductDetailScreen`。
- 新增 `composable(FORMATTED_PRODUCT_DETAIL_ROUTE)`，从 back stack 读取 `product_id` 并进入商品详情页。

用途：

- 让 `navController.navigate("product_detail_route/{productId}")` 可以正常打开商品页。

回滚：

- 删除上述 3 个 import。
- 删除商品详情页对应的 `composable(...)` 代码块。

## 5. 扩展广告 CTA 组件，支持内部跳转

文件：

- `tiktok_flow/TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt`

操作：

- 给 `TikTokVerticalVideoPager` 新增可选参数：
  `onClickActionLink: ((url: String) -> Unit)? = null`
- 调用 `AdActionLink(...)` 时传入 `onClickActionLink`。
- 给 `AdActionLink` 新增同名参数。
- 点击广告 CTA 时：
  - 如果外部传入 `onClickActionLink`，走内部回调。
  - 如果没有传入，保留原来的 `Intent.ACTION_VIEW` 打开 URL 行为。

用途：

- 内容流可以把广告链接映射为 App 内商品详情页。
- 其他复用方仍可保持原来的外链行为。

回滚：

- 删除 `TikTokVerticalVideoPager` 的 `onClickActionLink` 参数。
- 删除 `AdActionLink(...)` 调用中的 `onClickActionLink = onClickActionLink`。
- 删除 `AdActionLink` 函数参数中的 `onClickActionLink`。
- 将点击逻辑恢复为直接 `context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))`。

## 6. 内容流广告 CTA 映射到商品详情

文件：

- `tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt`

操作：

- import `PRODUCT_DETAIL_ROUTE`。
- 调用 `TikTokVerticalVideoPager` 时新增：
  `onClickActionLink = { url -> navController.navigate("$PRODUCT_DETAIL_ROUTE/${url.toProductId()}") }`
- 新增 `String.toProductId()`：
  - `tiktokflow://product/{id}` -> `{id}`
  - `product:{id}` -> `{id}`
  - `tiktokflow://ad/{id}` -> `prod_1`
  - `ad:{id}` -> `prod_1`
  - 其他值 -> `prod_1`

用途：

- 兼容旧广告占位链接和新商品 deep link。

回滚：

- 删除 `PRODUCT_DETAIL_ROUTE` import。
- 删除 `onClickActionLink = ...` 参数。
- 删除 `String.toProductId()` 函数。

## 7. 直播商品卡点击跳商品详情

文件：

- `tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt`

操作：

- import `PRODUCT_DETAIL_ROUTE`。
- `LiveProductFloatingCard(...)` 调用处新增 `onClick`，执行：
  `navController.navigate("$PRODUCT_DETAIL_ROUTE/${product.id}")`
- `LiveProductFloatingCard` 函数新增 `onClick: () -> Unit` 参数。
- 商品卡根 `Surface` 的 modifier 增加 `clickable { onClick() }`。

用途：

- 直播间当前商品卡整体可点击进入商品详情页。

回滚：

- 删除 `PRODUCT_DETAIL_ROUTE` import。
- 删除调用处 `onClick = { ... }`。
- 删除 `LiveProductFloatingCard` 的 `onClick` 参数。
- 删除商品卡根 modifier 上的 `clickable { onClick() }`。

## 8. 对齐直播 mock 商品 id

文件：

- `tiktok_flow/TikTok-Compose/data/src/main/java/com/puskal/data/api/live/MockLiveRoomApi.kt`

操作：

- 将 `currentProduct` 的 `id` 从 `p1` 改为 `prod_1`。

用途：

- 让直播商品卡点击后可以命中 `ProductCatalog` 中的 `prod_1` 商品。

回滚：

- 将 `id = "prod_1"` 改回 `id = "p1"`。

## 9. 广告 mock 链接改为商品 deep link

文件：

- `tiktok_flow/TikTok-Compose/data/src/main/java/com/puskal/data/source/VideoDataSource.kt`

操作：

- 将广告视频的 `actionLinkUrl` 从 `tiktokflow://ad/$adId` 改为 `tiktokflow://product/prod_1`。

用途：

- 广告 CTA 直接落到商品详情页。
- 仍保留 `ForYouTabScreen.toProductId()` 对旧 `tiktokflow://ad/{id}` 的兼容。

回滚：

- 将 `actionLinkUrl = "tiktokflow://product/prod_1"` 改回 `actionLinkUrl = "tiktokflow://ad/$adId"`。

## 10. 回滚顺序建议

如果要完整回滚本次融合，建议按以下顺序：

1. 先回滚入口调用：`ForYouTabScreen.kt`、`LiveRoomScreen.kt`。
2. 再回滚广告组件扩展：`TiktokVerticalVideoPager.kt`。
3. 再回滚导航注册：`HomeNavigation.kt`、`DestinationRoute.kt`。
4. 删除新增商品页文件：`ProductDetailScreen.kt`、`ProductCatalog.kt`。
5. 最后按需要回滚 mock 数据：`MockLiveRoomApi.kt`、`VideoDataSource.kt`。

