# Commerce full integration plan and rollback

Date: 2026-06-08

Main project:

```text
D:\tiktok_flow\_incoming\live-commerce-app\mobile\tiktok_flow\TikTok-Compose
```

Goal: use TikTok-Compose as the host app and add a full mock commerce flow for product detail, search results, cart, order confirmation, payment, order detail, and order list.

## Step 1: Register the commerce module

Files changed:

```text
tiktok_flow/TikTok-Compose/settings.gradle.kts
tiktok_flow/TikTok-Compose/buildSrc/src/main/java/DependencyHandler.kt
tiktok_flow/TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/navigation/AppNavHost.kt
```

What changed:

- Added `:feature:commerce`.
- Added `FEATURE_COMMERCE` to the shared Gradle dependency helper.
- Added `commerceNavGraph(navController)` to the app NavHost.

Why:

- Keeps commerce isolated from `feature:home`.
- Lets the app route all commerce screens through one graph.

Rollback:

- Remove `include(":feature:commerce")` from `settings.gradle.kts`.
- Remove `FEATURE_COMMERCE` from `moduleDependencies()` and delete the `FEATURE_COMMERCE` helper.
- Remove the `commerceNavGraph(navController)` import and call from `AppNavHost.kt`.

## Step 2: Add shared commerce routes

Files changed:

```text
tiktok_flow/TikTok-Compose/core/src/main/java/com/puskal/core/DestinationRoute.kt
```

What changed:

- Added routes for product detail, product search, cart, order confirmation, order list, order detail, and payment.
- Added route keys for product id, query, order source, order id, and order number.

Why:

- Home, live, comments, and commerce need one route contract.

Rollback:

- Remove all commerce route constants and commerce `PassedKey` constants from `DestinationRoute.kt`.

## Step 3: Create the commerce module

Files changed:

```text
tiktok_flow/TikTok-Compose/feature/commerce/build.gradle.kts
tiktok_flow/TikTok-Compose/feature/commerce/src/main/AndroidManifest.xml
tiktok_flow/TikTok-Compose/feature/commerce/src/main/java/com/puskal/commerce/model/CommerceModels.kt
tiktok_flow/TikTok-Compose/feature/commerce/src/main/java/com/puskal/commerce/data/CommerceRepository.kt
tiktok_flow/TikTok-Compose/feature/commerce/src/main/java/com/puskal/commerce/navigation/CommerceNavigation.kt
tiktok_flow/TikTok-Compose/feature/commerce/src/main/java/com/puskal/commerce/screen/CommerceScreens.kt
```

What changed:

- Added product, SKU, cart, order, address, and payment models.
- Added an in-memory `CommerceRepository` with mock products `prod_1` and `prod_2`.
- Added route helper functions and entry-key parsing.
- Added product detail, search result, cart, order confirmation, payment, order list, and order detail screens.

Why:

- The root `app` project only had placeholder commerce screens and no shared transaction state.
- This module provides a complete mock transaction loop inside TikTok-Compose.

Rollback:

- Delete `tiktok_flow/TikTok-Compose/feature/commerce`.
- Also complete Step 1 and Step 2 rollback so no module references remain.

## Step 4: Replace the temporary product detail implementation

Files changed:

```text
tiktok_flow/TikTok-Compose/feature/home/build.gradle.kts
tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/HomeNavigation.kt
tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt
tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt
```

Files removed:

```text
tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/product/ProductCatalog.kt
tiktok_flow/TikTok-Compose/feature/home/src/main/java/com/puskal/home/product/ProductDetailScreen.kt
```

What changed:

- Added `FEATURE_COMMERCE` as a dependency of `feature:home`.
- Removed temporary product detail registration from `HomeNavigation.kt`.
- Changed ad CTA navigation to call `navigateToCommerceEntry(url)`.
- Changed live product card navigation to call `navigateToProductDetail(product.id)`.

Why:

- Avoids two different product detail pages.
- Makes content feed and live feed use the same commerce graph.

Rollback:

- Remove `FEATURE_COMMERCE` from `feature/home/build.gradle.kts`.
- Restore the deleted `feature/home/product` files if reverting to the temporary implementation.
- Restore product detail route registration in `HomeNavigation.kt`.
- Restore the old ad and live navigation code.

## Step 5: Add comment entry navigation

Files changed:

```text
tiktok_flow/TikTok-Compose/feature/commentlisting/build.gradle.kts
tiktok_flow/TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListingNavigation.kt
tiktok_flow/TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt
```

What changed:

- Added `FEATURE_COMMERCE` as a dependency of `feature:commentlisting`.
- Added click callbacks for the header search recommendation and the author recommendation label.
- The bottom sheet closes before navigating to commerce.

Why:

- Connects the reserved comment entries: `大家都在搜` and `作者推荐`.

Rollback:

- Remove `FEATURE_COMMERCE` from `feature/commentlisting/build.gradle.kts`.
- Remove the commerce import and callbacks from `CommentListingNavigation.kt`.
- Remove click callbacks from `CommentListScreen.kt`.

## Step 6: Normalize mock entry keys

Files changed:

```text
tiktok_flow/TikTok-Compose/data/src/main/java/com/puskal/data/source/VideoDataSource.kt
tiktok_flow/TikTok-Compose/data/src/main/java/com/puskal/data/api/live/MockLiveRoomApi.kt
```

What changed:

- Comment search key now uses `search:荣耀600`.
- Comment author recommendation key now uses `recommend:prod_1`.
- Live product id is expected to stay aligned with commerce ids such as `prod_1`.
- Ad CTA already points to `tiktokflow://product/prod_1`.

Why:

- All four entry types resolve through one commerce parser.

Rollback:

- Restore the previous mock link keys in `VideoDataSource.kt`.
- Restore the previous live product id if needed.

## Verification

Run from:

```powershell
cd D:\tiktok_flow\_incoming\live-commerce-app\mobile\tiktok_flow\TikTok-Compose
```

Commands:

```powershell
.\gradlew.bat :feature:commerce:compileDebugKotlin
.\gradlew.bat :app:compileDebugKotlin
```

Manual checks:

- Content ad CTA -> product detail -> buy now -> payment -> order detail.
- Live product card -> product detail -> add to cart -> cart -> order confirmation.
- Comment `作者推荐` -> product detail.
- Comment `大家都在搜` -> search results -> product detail.
- Order list -> order detail.

## Full rollback order

1. Disconnect feed, live, and comment entry navigation.
2. Remove `commerceNavGraph(navController)` from the app NavHost.
3. Remove commerce routes from `DestinationRoute.kt`.
4. Remove all `FEATURE_COMMERCE` dependencies.
5. Remove `include(":feature:commerce")`.
6. Delete `feature/commerce`.
7. Restore old mock ids and link keys.
