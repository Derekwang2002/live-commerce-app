# Code Map

## 1) Project Structure (scan result)

- `app/`: App entry layer. Contains `MainActivity`, root UI shell, and top-level navigation wiring across feature modules.
- `feature/`: Business feature layer. Each subfolder is one feature module (home, comment, profile, inbox, etc.).
- `data/`: Data layer. Contains data models, repositories, data sources, and assets (mock videos/images).
- `domain/`: Use case layer. Decouples `feature` from `data`.
- `common/composable/`: Shared UI components (for example: video player, vertical video pager).
- `common/theme/`: Theme, typography, colors, and drawable resources.
- `core/`: Cross-module core utilities (route constants, base ViewModel, helpers, extensions).
- `buildSrc/`: Centralized Gradle dependency and build configuration.
- `gradle/`: Gradle Wrapper files.
- `screenshots/`: Project screenshots and GIF assets.

## 2) Content Feed Module (key area)

> In this project, the Content Feed Module is mainly `feature:home + common:composable + data/domain feed pipeline`.

- Home entry: `feature/home/src/main/java/com/puskal/home/HomeScreen.kt`
- Home nav graph registration: `feature/home/src/main/java/com/puskal/home/HomeNavigation.kt`
- For You feed screen: `feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt`
- Following screen: `feature/home/src/main/java/com/puskal/home/tab/following/FollowingTabScreen.kt`
- Vertical feed UI component: `common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt`
- Feed repository: `data/src/main/java/com/puskal/data/repository/home/ForYouRepository.kt`
- Feed data source: `data/src/main/java/com/puskal/data/source/VideoDataSource.kt`
- Feed use case: `domain/src/main/java/com/puskal/domain/foryou/GetForYouPageFeedUseCase.kt`

## 3) Shopping Flow (teammate responsibility)

- teammate responsibility: no shopping/product/cart/productId implementation was found in current source.
- Conclusion: `Not implemented yet`

## 4) Feature -> Code Location Mapping

- Short video feed:
  - `feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt`
  - `common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt`
  - `data/src/main/java/com/puskal/data/source/VideoDataSource.kt`
- Live feed: `Not implemented yet`
- Video playback:
  - `common/composable/src/main/java/com/puskal/composable/VideoPlayer.kt`
- Comment list:
  - `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt`
  - `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListViewModel.kt`
  - `data/src/main/java/com/puskal/data/repository/comment/CommentRepository.kt`
  - `data/src/main/java/com/puskal/data/source/CommentDataSource.kt`
- Live barrage (danmu): `Not implemented yet`
- Product card entry: `Not implemented yet`
- `productId` passing: `Not implemented yet`
- AI recommendation page: `Not implemented yet`
- User behavior analytics: `Not implemented yet`
- C++ / NDK related code: `Not implemented yet` (no source-level `cpp/jni` implementation found; only build intermediate `jni` folders exist)
- Mock data:
  - `data/src/main/java/com/puskal/data/source/VideoDataSource.kt`
  - `data/src/main/java/com/puskal/data/source/CommentDataSource.kt`
  - `data/src/main/java/com/puskal/data/source/UsersDataSource.kt`
  - `data/src/main/assets/videos/`
  - `data/src/main/assets/templateimages/`

## 5) High-Risk Files (review carefully before editing)

- Routing / navigation:
  - `app/src/main/java/com/puskal/tiktokcompose/navigation/AppNavHost.kt`
  - `app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt`
  - `core/src/main/java/com/puskal/core/DestinationRoute.kt`
- Product detail entry: `Not implemented yet`
- Interfaces that hand off to teammate modules: `Not implemented yet`
- NDK/C++ code: `Not implemented yet`
- Data models:
  - `data/src/main/java/com/puskal/data/model/VideoModel.kt`
  - `data/src/main/java/com/puskal/data/model/ForYouFeedModel.kt`
  - `data/src/main/java/com/puskal/data/model/CommentList.kt`
  - `data/src/main/java/com/puskal/data/model/UserModel.kt`

## 6) When modifying this project, read these files first

- `app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt`
- `app/src/main/java/com/puskal/tiktokcompose/navigation/AppNavHost.kt`
- `core/src/main/java/com/puskal/core/DestinationRoute.kt`
- `feature/home/src/main/java/com/puskal/home/HomeScreen.kt`
- `feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt`
- `common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt`
- `common/composable/src/main/java/com/puskal/composable/VideoPlayer.kt`
- `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListingNavigation.kt`
- `feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListViewModel.kt`
- `data/src/main/java/com/puskal/data/source/VideoDataSource.kt`
- `data/src/main/java/com/puskal/data/source/CommentDataSource.kt`
- `data/src/main/java/com/puskal/data/model/VideoModel.kt`
