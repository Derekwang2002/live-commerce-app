方案建议叫：**以 TikTok-Compose 为主壳，迁入完整 commerce 交易闭环**。

**目标**
把商品、购物车、订单、支付流程（见除tiktok_flow其他文件）完整接进tiktok_flow的内容流/直播流项目里，让这些入口都能进入同一套交易流程：

- 内容流广告 CTA -> 商品详情
- 直播间商品卡 -> 商品详情
- 评论区 `作者推荐` -> 商品详情/推荐商品
- 评论区 `大家都在搜` -> 搜索结果/商品列表
- 商品详情 -> 加购/立即购买
- 购物车 -> 确认订单
- 确认订单 -> 支付
- 支付 -> 订单详情/订单列表

**主项目选择**
用你的项目作为主 App：

```text
D:\tiktok_flow\_incoming\live-commerce-app\mobile\tiktok_flow\TikTok-Compose
```

原因：你的项目已经有内容流、直播流、多模块、播放器、Hilt、assets、导航框架。商品项目相对简单，迁进来成本更低。

不建议反过来把 TikTok-Compose 塞进根目录 `app`，那样要迁播放器、多模块和资源，风险更高。

**目录设计**
推荐新增一个 commerce 功能模块：

```text
tiktok_flow/TikTok-Compose/feature/commerce
```

内部结构：

```text
feature/commerce/src/main/java/com/puskal/commerce/
  data/
    CommerceMockData.kt
    CommerceRepository.kt
  model/
    Product.kt
    Cart.kt
    Order.kt
  navigation/
    CommerceNavigation.kt
  screen/
    product/ProductDetailScreen.kt
    search/ProductSearchResultScreen.kt
    cart/CartScreen.kt
    order/OrderConfirmScreen.kt
    order/OrderDetailScreen.kt
    order/OrderListScreen.kt
    payment/PaymentScreen.kt
```

如果想少改 Gradle，也可以先放在：

```text
feature/home/src/main/java/com/puskal/home/commerce/
```

但长期更推荐独立 `feature:commerce`，边界清楚。

**需要迁入的文件**
从根项目 `app` 迁这些：

```text
app/src/main/java/com/example/tts_like/data/model/Product.kt
app/src/main/java/com/example/tts_like/data/model/Cart.kt
app/src/main/java/com/example/tts_like/data/model/Order.kt
app/src/main/java/com/example/tts_like/data/model/VideoContent.kt
app/src/main/java/com/example/tts_like/data/mock/MockDate.kt
app/src/main/java/com/example/tts_like/feature/cart/CartScreen.kt
app/src/main/java/com/example/tts_like/feature/order/OrderConfirmScreen.kt
app/src/main/java/com/example/tts_like/feature/order/OrderDetailScreen.kt
app/src/main/java/com/example/tts_like/feature/order/OrderListScreen.kt
app/src/main/java/com/example/tts_like/feature/payment/PaymentScreen.kt
```

如果对方后面有真正的商品详情页，也一起迁。当前我看到根项目里还没有完整商品详情页，只有模型和占位页面，所以商品详情页可能需要新建或补齐。

**统一路由设计**
在 `core/DestinationRoute.kt` 里增加 commerce 路由：

```kotlin
const val PRODUCT_DETAIL_ROUTE = "product_detail_route"
const val FORMATTED_PRODUCT_DETAIL_ROUTE = "$PRODUCT_DETAIL_ROUTE/{product_id}"

const val PRODUCT_SEARCH_ROUTE = "product_search_route"
const val FORMATTED_PRODUCT_SEARCH_ROUTE = "$PRODUCT_SEARCH_ROUTE/{query}"

const val CART_ROUTE = "cart_route"

const val ORDER_CONFIRM_ROUTE = "order_confirm_route"
const val FORMATTED_ORDER_CONFIRM_ROUTE = "$ORDER_CONFIRM_ROUTE/{product_id}/{sku_id}"

const val ORDER_LIST_ROUTE = "order_list_route"

const val ORDER_DETAIL_ROUTE = "order_detail_route"
const val FORMATTED_ORDER_DETAIL_ROUTE = "$ORDER_DETAIL_ROUTE/{order_id}"

const val PAYMENT_ROUTE = "payment_route"
const val FORMATTED_PAYMENT_ROUTE = "$PAYMENT_ROUTE/{order_no}"
```

**入口映射**
按你文档里的预留接口统一处理。

广告：

```text
tiktokflow://product/prod_1 -> product_detail_route/prod_1
product:prod_1 -> product_detail_route/prod_1
tiktokflow://ad/4 -> product_detail_route/prod_1 或 ad 映射表
```

直播商品卡：

```text
LiveProduct.id = prod_1
点击 -> product_detail_route/prod_1
```

评论区作者推荐：

```text
authorRecommendUrlKey = recommend:prod_1
点击 -> product_detail_route/prod_1
```

评论区大家都在搜：

```text
searchUrlKey = search:荣耀600
点击 -> product_search_route/荣耀600
```

**数据统一**
商品 id 必须统一。现在你直播 mock 是 `p1`，商品 mock 是 `prod_1`，这个必须改成同一套。

建议全部用商品项目里的 id：

```text
prod_1
prod_2
```

然后你的广告、直播、评论推荐都只引用这些 id。

**交易闭环**
商品详情页按钮行为：

```text
加入购物车 -> 写入 CartRepository -> cart_route
立即购买 -> order_confirm_route/{productId}/{skuId}
```

购物车页：

```text
结算 -> order_confirm_route
```

确认订单页：

```text
提交订单 -> 创建 mock order -> payment_route/{orderNo}
```

支付页：

```text
支付成功 -> order_detail_route/{orderId}
```

订单列表页：

```text
点击订单 -> order_detail_route/{orderId}
```

**实施顺序**
1. 建 `feature:commerce` 模块，接入 Gradle。
2. 迁入商品、购物车、订单、支付模型。
3. 建 `CommerceRepository`，先用 mock 内存数据管理商品、购物车、订单。
4. 迁入并适配购物车、订单、支付页面。
5. 新建或补齐商品详情页、搜索结果页。
6. 在 `DestinationRoute.kt` 统一 commerce 路由。
7. 写 `CommerceNavigation.kt` 注册所有 commerce 页面。
8. 在 `AppNavHost.kt` include `commerceNavGraph(navController)`。
9. 接内容流广告 CTA。
10. 接直播商品卡。
11. 接评论区 `作者推荐` 和 `大家都在搜`。
12. 跑编译：
    ```powershell
    .\gradlew.bat :feature:commerce:compileDebugKotlin
    .\gradlew.bat :app:compileDebugKotlin
    ```
13. 真机验证四条入口：
    - 广告 -> 商品详情 -> 立即购买 -> 支付
    - 直播商品卡 -> 商品详情 -> 加购物车
    - 评论区作者推荐 -> 商品详情
    - 评论区大家都在搜 -> 搜索结果

**回滚策略**
每一步都写进新的 docs 文件，比如：

```text
tiktok_flow/docs/commerce_full_integration_plan_and_rollback.md
```

回滚顺序：

1. 先断开入口：广告、直播、评论区点击。
2. 从 `AppNavHost.kt` 移除 `commerceNavGraph`。
3. 从 `DestinationRoute.kt` 移除 commerce 路由。
4. 从 `settings.gradle.kts` 移除 `:feature:commerce`。
5. 删除 `feature/commerce` 模块。
6. 恢复 mock 数据里的商品 id 和链接。

**风险点**
最大风险不是页面，而是状态。

如果只是页面跳转，很简单；但完整交易流程需要一套共享状态：

- 商品详情知道当前商品
- 购物车知道加购商品
- 确认订单知道商品、sku、数量
- 支付知道订单号
- 订单列表知道支付后的订单状态

所以需要 `CommerceRepository` 或 `CommerceViewModel` 做统一管理。先用内存 mock 就够，后续再换成真实接口。

**最终结果**
完成后就不是“只能跳商品详情页”，而是：

```text
内容流/直播流/评论区
  -> 商品详情
  -> 购物车/确认订单
  -> 支付
  -> 订单详情/订单列表
```

这就是完整融合，而不是继续零散补入口。