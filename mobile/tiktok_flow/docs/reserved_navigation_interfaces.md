# 预留跳转接口文档

本文档统一记录当前项目中已经展示、但暂未接入具体落地页的跳转入口。后续新增商品详情页、搜索结果页、作者推荐页或广告详情页时，优先复用这里定义的字段和路由约定。

## 1. 通用约定

所有预留入口都建议包含以下字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `String` | 入口或业务对象的唯一标识。 |
| `title` | `String` | 展示给用户的标题或主文案。 |
| `label` | `String?` | 入口标签，例如 `作者推荐`、`大家都在搜`、`广告`、`查看`。 |
| `routeKey` | `String?` | App 内部路由占位 key，用于以后跳转到 Compose 页面。 |
| `targetUrl` | `String?` | 外部链接或 deep link。没有真实页面时可先使用占位 scheme。 |
| `trackingKey` | `String?` | 埋点或推荐归因 key，当前可选。 |

当前阶段允许 `routeKey` 和 `targetUrl` 只做占位，不要求页面立即存在。点击行为可以先不跳转，或只记录日志。

## 2. 评论区入口

评论区目前有两个预留入口：

1. `大家都在搜`
2. `作者推荐`

代码位置：

| 功能 | 文件 | 当前数据来源 |
| --- | --- | --- |
| 评论区顶部“大家都在搜” | `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt` | `VideoModel.SearchRecommendation.searchText` |
| 评论区推荐评论“作者推荐” | `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt` | `VideoModel.SearchRecommendation.label` |
| mock 配置 | `data/src/main/java/com/puskal/data/source/VideoDataSource.kt` | `searchRecommendationFor(index)` |

### 2.1 字段定义

当前模型：`VideoModel.SearchRecommendation`

| 字段 | 类型 | 当前用途 | 后续用途 |
| --- | --- | --- | --- |
| `searchText` | `String` | “大家都在搜”的搜索词。 | 跳转搜索结果页时作为搜索 query。 |
| `commentText` | `String` | 作者推荐评论内容。 | 推荐卡片主文案。 |
| `label` | `String` | 推荐入口文案，例如 `作者推荐`。 | 点击后进入作者推荐页或商品详情页。 |
| `searchUrlKey` | `String` | “大家都在搜”占位 key。 | 映射到搜索页 route 或远程 URL。 |
| `authorRecommendUrlKey` | `String` | “作者推荐”占位 key。 | 映射到推荐详情页 route 或商品详情页。 |

### 2.2 推荐接口形态

```json
{
  "searchRecommendation": {
    "searchText": "荣耀600",
    "commentText": "点击了解全新荣耀600系列",
    "label": "作者推荐",
    "searchUrlKey": "search:honor_600",
    "authorRecommendUrlKey": "recommend:honor_600_author"
  }
}
```

### 2.3 后续跳转建议

| 入口 | 建议 routeKey | 建议目标页 |
| --- | --- | --- |
| 大家都在搜 | `search:{keyword}` | 搜索结果页 |
| 作者推荐 | `recommend:{recommendId}` | 作者推荐详情页、商品详情页或活动页 |

## 3. 广告视频入口

广告视频与普通视频共用推荐流播放能力。区别只在视频条目上增加广告元数据，并在视频底部展示 `广告` 标签和 `查看详情` 按钮。

代码位置：

| 功能 | 文件 | 当前数据来源 |
| --- | --- | --- |
| 广告视频 mock 生成 | `data/src/main/java/com/puskal/data/source/VideoDataSource.kt` | `resources/ad/*.mp4` |
| 广告字段模型 | `data/src/main/java/com/puskal/data/model/VideoModel.kt` | `VideoModel` |
| 广告 UI | `common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt` | `isAdvertisement/actionLinkText/actionLinkUrl` |

### 3.1 字段定义

当前模型：`VideoModel`

| 字段 | 类型 | 当前用途 | 后续用途 |
| --- | --- | --- | --- |
| `assetDirectory` | `String` | 本地资源目录。广告视频为 `ad`。 | 远程接口接入后可替换为视频 URL 或资源类型。 |
| `isAdvertisement` | `Boolean` | 判断是否显示广告样式。 | 控制广告披露、CTA、埋点等。 |
| `adLabel` | `String` | 显示小灰色 `广告` 标签。 | 可由服务端返回合规文案。 |
| `actionLinkText` | `String?` | 底部按钮文案，当前为 `查看详情`。 | 可配置为 `立即购买`、`了解更多` 等。 |
| `actionLinkUrl` | `String?` | 底部按钮占位跳转地址。 | 后续接广告详情页、商品详情页或外部落地页。 |

### 3.2 推荐接口形态

```json
{
  "videoId": "ad_video_4",
  "videoLink": "4.mp4",
  "assetDirectory": "ad",
  "description": "本地广告视频 1 #推荐 #本地素材",
  "isAdvertisement": true,
  "adLabel": "广告",
  "actionLinkText": "查看详情",
  "actionLinkUrl": "tiktokflow://ad/4"
}
```

### 3.3 后续跳转建议

| 入口 | 建议 routeKey 或 URL | 建议目标页 |
| --- | --- | --- |
| 查看详情 | `ad:{adId}` 或 `tiktokflow://ad/{adId}` | 广告详情页 |
| 查看详情 | `product:{productId}` 或 `tiktokflow://product/{productId}` | 商品详情页 |
| 查看详情 | `https://...` | 外部广告落地页 |

## 4. 直播间商品卡入口

直播间商品卡目前展示商品名称、价格和 `查看` 按钮。当前点击暂未接入具体详情页，后续可以通过商品 id 或跳转字段进入商品详情页。

代码位置：

| 功能 | 文件 | 当前数据来源 |
| --- | --- | --- |
| 直播间 UI | `feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt` | `LiveRoomState.currentProduct` |
| 直播商品模型 | `data/src/main/java/com/puskal/data/model/live/LiveRoomModels.kt` | `LiveProduct` |
| 直播间 mock 接口 | `data/src/main/java/com/puskal/data/api/live/MockLiveRoomApi.kt` | `currentProduct` |

### 4.1 当前字段

当前模型：`LiveProduct`

| 字段 | 类型 | 当前用途 | 后续用途 |
| --- | --- | --- | --- |
| `id` | `String` | 商品唯一标识。 | 跳转商品详情页时作为 `productId`。 |
| `name` | `String` | 商品名称。 | 商品卡标题。 |
| `priceText` | `String` | 价格文案。 | 商品卡价格展示。 |

### 4.2 建议新增字段

后续接具体跳转页面时，建议将 `LiveProduct` 扩展为：

```json
{
  "id": "p1",
  "name": "夏季轻薄外套",
  "priceText": "￥39.90",
  "coverUrl": "https://example.com/product/p1.png",
  "buttonText": "查看",
  "routeKey": "product:p1",
  "targetUrl": "tiktokflow://product/p1"
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `coverUrl` | `String?` | 商品图片。当前 UI 用渐变占位图，后续可替换为真实图片。 |
| `buttonText` | `String?` | 商品卡按钮文案，默认 `查看`。 |
| `routeKey` | `String?` | App 内部商品详情路由 key。 |
| `targetUrl` | `String?` | deep link 或外部商品页地址。 |

### 4.3 后续跳转建议

| 入口 | 建议 routeKey | 建议目标页 |
| --- | --- | --- |
| 直播商品卡“查看” | `product:{productId}` | 商品详情页 |
| 直播商品卡主体 | `product:{productId}` | 商品详情页 |

## 5. 页面接入建议

后续新增具体跳转页面时，建议按以下顺序接入：

1. 在 `core/src/main/java/com/puskal/core/DestinationRoute.kt` 增加目标路由常量。
2. 在对应 feature 的 Navigation 文件中注册 route。
3. 将当前占位字段 `routeKey` 或 `targetUrl` 映射到 `NavController.navigate(...)`。
4. 保留 `targetUrl` 对外部链接的兼容能力。
5. 点击入口时补充埋点字段，例如 `trackingKey`、`sourceVideoId`、`roomId`。

## 6. 当前占位 key 汇总

| 场景 | 当前 key 或 URL | 状态 |
| --- | --- | --- |
| 评论区大家都在搜 | `honor_600_search_url` | 已展示，未跳转 |
| 评论区作者推荐 | `author_recommend_url` | 已展示，未跳转 |
| 广告视频查看详情 | `tiktokflow://ad/4` | 已展示，系统打开占位 URL |
| 直播商品卡查看 | `product:{productId}` | 建议预留，当前 UI 未跳转 |
