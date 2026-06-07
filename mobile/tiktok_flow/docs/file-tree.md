# 项目文件树

更新时间：2026-06-07

说明：本文件树按当前仓库整理，省略 `.git/`、`.gradle/`、`build/`、IDE 缓存、重复启动图标、批量字体、批量视频等低价值明细。需要精确定位代码时，优先参考 `TikTok-Compose/code-map.md` 和根目录 `page-component-map.md`。

```text
D:\tiktok_flow
|-- AGENT.md                                  # Codex/开发协作约定
|-- analysis.md                               # 项目分析记录
|-- page-component-map.md                     # 页面与组件映射总表
|-- requirements.md                           # 需求说明
|-- tesk.md                                   # 任务记录
|-- 字节训练营5_25.txt                        # 原始课程/训练营记录
|
|-- docs/                                     # 项目文档
|   |-- file-tree.md                          # 当前文件树
|   |-- project_technical_documentation.md    # 技术文档
|   |-- reserved_navigation_interfaces.md     # 预留导航接口说明
|   |
|   |-- ai/
|   |   `-- feed-scene-preload-plan.md        # Feed 场景预加载方案
|   |
|   `-- livejpg/
|       |-- entry.jpg                         # 直播入口参考图
|       `-- in.jpg                            # 直播间参考图
|
|-- resources/                                # 外部素材资源
|   |-- ad/
|   |   `-- 4.mp4
|   |-- live/
|   |   `-- 1.mp4
|   `-- vidoe/                                # 原目录名如此，存放视频素材
|       |-- 1.mp4
|       |-- 2.mp4
|       `-- 3.mp4
|
|-- TikTok-Compose/                           # 主 Android / Jetpack Compose 工程
|   |-- README.md
|   |-- LICENSE
|   |-- build.gradle.kts
|   |-- settings.gradle.kts
|   |-- gradle.properties
|   |-- gradlew
|   |-- gradlew.bat
|   |-- code-map.md                           # 主工程代码地图
|   |-- live_integration_plan.md              # 直播集成方案
|   |-- mock_live_room_progress.md            # Mock 直播间进度
|   |-- reserved-links.md                     # 预留链接记录
|   |
|   |-- app/                                  # App 壳层模块
|   |   |-- build.gradle.kts
|   |   |-- proguard-rules.pro
|   |   `-- src/
|   |       |-- main/
|   |       |   |-- AndroidManifest.xml
|   |       |   `-- java/com/puskal/tiktokcompose/
|   |       |       |-- MainActivity.kt
|   |       |       |-- MyApp.kt
|   |       |       |-- RootScreen.kt
|   |       |       |-- component/
|   |       |       |   `-- BottomBar.kt
|   |       |       `-- navigation/
|   |       |           |-- AppNavHost.kt
|   |       |           `-- BottomBarDestination.kt
|   |       |-- androidTest/...                # Android 仪器测试样板
|   |       `-- test/...                       # 单元测试样板
|   |
|   |-- buildSrc/                             # Gradle 构建配置复用
|   |   |-- build.gradle.kts
|   |   `-- src/main/java/
|   |       |-- AppConfig.kt
|   |       |-- DependencyHandler.kt
|   |       |-- Libraries.kt
|   |       `-- plugin/
|   |           `-- android-common.gradle.kts
|   |
|   |-- common/                               # 跨功能公共 UI 和主题
|   |   |-- composable/
|   |   |   |-- build.gradle.kts
|   |   |   `-- src/main/java/com/puskal/composable/
|   |   |       |-- CaptureButton.kt
|   |   |       |-- ContentSearchBar.kt
|   |   |       |-- CustomButton.kt
|   |   |       |-- CustomIconButton.kt
|   |   |       |-- TiktokVerticalVideoPager.kt
|   |   |       |-- TopBar.kt
|   |   |       |-- VideoPlayer.kt
|   |   |       |-- feed/
|   |   |       |   |-- FeedPlayerManager.kt
|   |   |       |   |-- FeedPreloadManager.kt
|   |   |       |   `-- FeedScene.kt
|   |   |       `-- live/
|   |   |           `-- SharedLivePlayer.kt
|   |   |
|   |   `-- theme/
|   |       |-- build.gradle.kts
|   |       `-- src/main/
|   |           |-- java/com/puskal/theme/
|   |           |   |-- Color.kt
|   |           |   |-- Font.kt
|   |           |   |-- Theme.kt
|   |           |   `-- Type.kt
|   |           `-- res/
|   |               |-- drawable/              # 图标、占位图、Logo
|   |               |-- font/                  # Proxima Nova 字体族
|   |               |-- mipmap-*/              # 启动图标资源
|   |               `-- values/                # colors/strings/themes
|   |
|   |-- core/                                 # 核心契约、路由、工具
|   |   |-- build.gradle.kts
|   |   `-- src/main/java/com/puskal/core/
|   |       |-- AppContract.kt
|   |       |-- DestinationRoute.kt
|   |       |-- base/
|   |       |   `-- BaseViewModel.kt
|   |       |-- extension/
|   |       |   |-- Extension.kt
|   |       |   `-- Space.kt
|   |       `-- utils/
|   |           |-- DisableRippleInteractionSource.kt
|   |           |-- FileUtils.kt
|   |           `-- IntentUtils.kt
|   |
|   |-- data/                                 # 数据层：模型、数据源、仓库、直播 API
|   |   |-- build.gradle.kts
|   |   `-- src/main/
|   |       |-- AndroidManifest.xml
|   |       |-- assets/
|   |       |   |-- templateimages/            # 模板图片 img1-img7
|   |       |   `-- videos/                    # Feed 示例视频素材
|   |       `-- java/com/puskal/data/
|   |           |-- api/
|   |           |   `-- live/
|   |           |       |-- LiveRoomApi.kt
|   |           |       `-- MockLiveRoomApi.kt
|   |           |-- model/
|   |           |   |-- AudioModel.kt
|   |           |   |-- CommentList.kt
|   |           |   |-- ContentCreatorFollowingModel.kt
|   |           |   |-- ForYouFeedModel.kt
|   |           |   |-- TemplateModel.kt
|   |           |   |-- UserModel.kt
|   |           |   |-- VideoAsset.kt
|   |           |   |-- VideoModel.kt
|   |           |   `-- live/
|   |           |       `-- LiveRoomModels.kt
|   |           |-- repository/
|   |           |   |-- camermedia/
|   |           |   |-- comment/
|   |           |   |-- creatorprofile/
|   |           |   |-- home/
|   |           |   `-- live/
|   |           `-- source/
|   |               |-- CommentDataSource.kt
|   |               |-- ContentCreatorForFollowingDataSource.kt
|   |               |-- TemplateDataSource.kt
|   |               |-- UsersDataSource.kt
|   |               `-- VideoDataSource.kt
|   |
|   |-- domain/                               # 用例层
|   |   |-- build.gradle.kts
|   |   `-- src/main/java/com/puskal/domain/
|   |       |-- cameramedia/
|   |       |-- comment/
|   |       |-- creatorprofile/
|   |       |-- following/
|   |       `-- foryou/
|   |
|   |-- feature/                              # 业务功能模块
|   |   |-- authentication/
|   |   |   |-- AuthenticationContract.kt
|   |   |   |-- AuthenticationNavigation.kt
|   |   |   |-- AuthenticationScreen.kt
|   |   |   `-- AuthenticationViewModel.kt
|   |   |-- cameramedia/
|   |   |   |-- CameraMediaContract.kt
|   |   |   |-- CameraMediaNavigation.kt
|   |   |   |-- CameraMediaScreen.kt
|   |   |   |-- CameraMediaViewModel.kt
|   |   |   `-- tabs/
|   |   |       |-- CameraScreen.kt
|   |   |       `-- TemplateScreen.kt
|   |   |-- commentlisting/
|   |   |   |-- CommentListContract.kt
|   |   |   |-- CommentListingNavigation.kt
|   |   |   |-- CommentListScreen.kt
|   |   |   `-- CommentListViewModel.kt
|   |   |-- creatorprofile/
|   |   |   |-- CreatorNavigation.kt
|   |   |   |-- component/
|   |   |   |   |-- VideoGrid.kt
|   |   |   |   `-- VideoListingPager.kt
|   |   |   `-- screen/
|   |   |       |-- creatorprofile/
|   |   |       `-- creatorvideo/
|   |   |-- friends/
|   |   |   |-- FriendsNavigation.kt
|   |   |   `-- FriendsScreen.kt
|   |   |-- home/
|   |   |   |-- HomeNavigation.kt
|   |   |   |-- HomeScreen.kt
|   |   |   |-- live/
|   |   |   |   |-- LiveRoomScreen.kt
|   |   |   |   |-- LiveRoomState.kt
|   |   |   |   `-- LiveRoomViewModel.kt
|   |   |   `-- tab/
|   |   |       |-- following/
|   |   |       `-- foryou/
|   |   |-- inbox/
|   |   |   |-- InboxNavigation.kt
|   |   |   `-- InboxScreen.kt
|   |   |-- loginwithemailphone/
|   |   |   |-- LoginEmailPhoneNavigation.kt
|   |   |   |-- LoginWithEmailPhoneContract.kt
|   |   |   |-- LoginWithEmailPhoneScreen.kt
|   |   |   |-- LoginWithEmailPhoneViewModel.kt
|   |   |   `-- tabs/
|   |   |       |-- EmailUsernameTabScreen.kt
|   |   |       `-- PhoneTabScreen.kt
|   |   |-- myprofile/
|   |   |   |-- MyProfileNavigation.kt
|   |   |   `-- MyProfileScreen.kt
|   |   `-- setting/
|   |       |-- SettingContract.kt
|   |       |-- SettingNavigation.kt
|   |       |-- SettingScreen.kt
|   |       `-- SettingViewModel.kt
|   |
|   |-- gradle/
|   |   `-- wrapper/
|   |       |-- gradle-wrapper.jar
|   |       `-- gradle-wrapper.properties
|   |
|   `-- screenshots/                          # 项目截图与演示图
|       |-- download_button.png
|       |-- tiktokcompose_demo.gif
|       |-- tiktokcompose_modularization.jpg
|       `-- tiktokcompose_screenshot.jpg
|
`-- live_reference/                           # 直播 SDK / HLS Demo 参考工程
    |-- README.md
    |-- build.gradle
    |-- settings.gradle
    |-- gradle.properties
    |-- gradlew
    |-- gradlew.bat
    |
    |-- .github/
    |   `-- dependabot.yml
    |
    |-- app/
    |   |-- build.gradle
    |   |-- proguard-rules.pro
    |   `-- src/
    |       |-- main/
    |       |   |-- AndroidManifest.xml
    |       |   |-- java/live/videosdk/android/hlsdemo/
    |       |   |   |-- common/
    |       |   |   |   |-- meeting/
    |       |   |   |   |-- reactions/
    |       |   |   |   `-- utils/
    |       |   |   |-- speakerMode/
    |       |   |   |   |-- manageTabs/
    |       |   |   |   |-- participantList/
    |       |   |   |   `-- stage/
    |       |   |   `-- viewerMode/
    |       |   |-- res/
    |       |   |   |-- color/
    |       |   |   |-- drawable/
    |       |   |   |-- layout/
    |       |   |   |-- values/
    |       |   |   `-- xml/
    |       |   |-- androidTest/...
    |       |   `-- test/...
    |       `-- debug/res/...                  # Debug 启动图标资源
    |
    |-- assets/                               # 直播 Demo 截图/GIF 参考素材
    |   |-- add_as_co_host_speaker.gif
    |   |-- add_as_co_host_viewer.gif
    |   |-- add_to_cart.gif
    |   |-- create_meeting.gif
    |   |-- create_or_join.jpg
    |   |-- emoji_speaker.gif
    |   |-- emoji_viewer.gif
    |   |-- HLS.png
    |   |-- join_meeting.gif
    |   |-- participants.gif
    |   |-- quality.gif
    |   |-- settings.gif
    |   |-- stage.gif
    |   |-- Tabs.png
    |   `-- viewer.gif
    |
    `-- gradle/
        `-- wrapper/
            |-- gradle-wrapper.jar
            `-- gradle-wrapper.properties
```

## 快速入口

- 主工程入口：`TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/MainActivity.kt`
- App 根组合：`TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt`
- 导航宿主：`TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/navigation/AppNavHost.kt`
- 首页模块：`TikTok-Compose/feature/home/`
- 直播间页面：`TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/`
- Feed 播放与预加载：`TikTok-Compose/common/composable/src/main/java/com/puskal/composable/feed/`
- 直播数据 Mock：`TikTok-Compose/data/src/main/java/com/puskal/data/api/live/`
- 公共主题与图标：`TikTok-Compose/common/theme/`
- 直播参考实现：`live_reference/app/src/main/java/live/videosdk/android/hlsdemo/`

