# TikTok-Compose 页面与组件代码名对照

扫描范围：`D:\tiktok_flow\TikTok-Compose`

统计规则：
- 只统计 `src/main` 下带 `@Composable` 的函数，排除 `build`、`test`、`androidTest` 生成或测试代码。
- “页面/Composable”包含导航壳、真正页面、可独立预览/复用的页面内部组件。
- “直接组件/API”列出该 Composable 函数体内直接调用到的 UI 组件、项目自定义 Composable、常用 Compose 状态/资源 API；不展开子组件内部的二级调用。

## App 外壳与导航

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `RootScreen` | 应用根页面，负责主题、底部弹窗导航、脚手架、底部栏和主导航容器。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt:49` | `rememberBottomSheetNavigator`, `rememberNavController`, `TikTokTheme`, `SetupSystemUi`, `ModalBottomSheetLayout`, `Scaffold`, `BottomBar`, `Column`, `AppNavHost` |
| `SetupSystemUi` | 系统状态栏/导航栏颜色设置组件。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt:107` | 无直接子组件 |
| `rememberBottomSheetNavigator` | 创建底部弹窗导航器的 Compose 状态辅助入口。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/RootScreen.kt:119` | `rememberModalBottomSheetState`, `remember` |
| `BottomBar` | 应用底部导航栏容器。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/component/BottomBar.kt:22` | `NavigationBar`, `BottomItem` |
| `BottomItem` | 底部导航栏单个入口，包括图标、文字和选中态。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/component/BottomBar.kt:41` | `NavigationBarItem`, `Text`, `stringResource`, `Icon`, `painterResource` |
| `AppNavHost` | 应用主导航图承载组件。 | `TikTok-Compose/app/src/main/java/com/puskal/tiktokcompose/navigation/AppNavHost.kt:24` | `NavHost` |

## 通用组件与主题

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `CaptureButton` | 相机拍摄圆形按钮。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/CaptureButton.kt:23` | `Box` |
| `ContentSearchBar` | 内容搜索输入栏，带搜索图标、输入框和装饰边框。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/ContentSearchBar.kt:30` | `Row`, `Icon`, `painterResource`, `BasicTextField`, `TextFieldDecorationBox`, `Divider`, `Text`, `stringResource`, `OutlinedBorderContainerBox` |
| `CustomButton` | 项目通用文字按钮。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/CustomButton.kt:22` | `Button`, `Text` |
| `CustomIconButton` | 项目通用图标加文字按钮。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/CustomIconButton.kt:27` | `Button`, `Row`, `Icon`, `painterResource`, `Text` |
| `TikTokVerticalVideoPager` | 抖音式上下滑视频流页面核心组件，承载视频、直播预览、底部信息和右侧操作区。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:64` | `rememberPagerState`, `rememberCoroutineScope`, `LaunchedEffect`, `VerticalPager`, `Box`, `LivePreviewCard`, `VideoPlayer`, `Column`, `Row`, `FooterUi`, `SideItems`, `AnimatedVisibility`, `Icon`, `painterResource` |
| `LivePreviewCard` | 视频流中的直播预览卡片。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:237` | `Box`, `LivePreviewMedia`, `Column`, `Spacer`, `Row`, `Surface`, `Text`, `Icon`, `painterResource`, `AsyncImage` |
| `LivePreviewMedia` | 直播预览媒体区域，优先播放视频预览，否则显示封面图。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:423` | `remember`, `DisposableEffect`, `AndroidView`, `AsyncImage` |
| `SideItems` | 视频流右侧操作栏，包括头像、点赞、评论、收藏、分享、音乐入口。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:469` | `Column`, `AsyncImage`, `Image`, `painterResource`, `Space`, `LaunchedEffect`, `LikeIconButton`, `Icon`, `Text`, `RotatingAudioView` |
| `LikeIconButton` | 点赞按钮及点赞数量展示。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:577` | `Box`, `Icon`, `painterResource`, `Text`, `Space` |
| `FooterUi` | 视频流底部作者、标题、上传时间、音乐信息区域。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:612` | `Column`, `Row`, `Text`, `Space`, `Icon`, `painterResource` |
| `RotatingAudioView` | 右下角旋转音乐封面组件。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TiktokVerticalVideoPager.kt:678` | `Box`, `AsyncImage` |
| `TopBar` | 项目通用顶部导航栏。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/TopBar.kt:21` | `CenterAlignedTopAppBar`, `Text`, `Icon`, `painterResource` |
| `VideoPlayer` | 视频播放组件，封装 ExoPlayer、缩略图、点击和生命周期处理。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/VideoPlayer.kt:44` | `LaunchedEffect`, `remember`, `DisposableEffect`, `rememberUpdatedState`, `AndroidView`, `AsyncImage` |
| `rememberFeedScene` | 视频流播放器管理场景的 Compose 状态入口。 | `TikTok-Compose/common/composable/src/main/java/com/puskal/composable/feed/FeedScene.kt:73` | `remember` |
| `TikTokTheme` | 应用 Material 主题包装。 | `TikTok-Compose/common/theme/src/main/java/com/puskal/theme/Theme.kt:37` | `MaterialTheme` |
| `Space` | 按 Dp 扩展生成垂直间距。 | `TikTok-Compose/core/src/main/java/com/puskal/core/extension/Space.kt:17` | `Spacer` |
| `SmallSpace` | 小号垂直间距组件。 | `TikTok-Compose/core/src/main/java/com/puskal/core/extension/Space.kt:23` | `Spacer` |
| `MediumSpace` | 中号垂直间距组件。 | `TikTok-Compose/core/src/main/java/com/puskal/core/extension/Space.kt:26` | `Spacer` |
| `LargeSpace` | 大号垂直间距组件。 | `TikTok-Compose/core/src/main/java/com/puskal/core/extension/Space.kt:29` | `Spacer` |

## 登录与认证

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `AuthenticationScreen` | 注册/登录方式选择页。 | `TikTok-Compose/feature/authentication/src/main/java/com/puskal/authentication/AuthenticationScreen.kt:40` | `Column`, `Row`, `Icon`, `painterResource`, `Space`, `Text`, `stringResource`, `Spacer`, `PrivacyPolicyFooter` |
| `AuthenticationButton` | 认证页的单个登录方式按钮。 | `TikTok-Compose/feature/authentication/src/main/java/com/puskal/authentication/AuthenticationScreen.kt:96` | `CustomIconButton`, `stringResource`, `Space` |
| `PrivacyPolicyFooter` | 认证页底部隐私政策说明文字。 | `TikTok-Compose/feature/authentication/src/main/java/com/puskal/authentication/AuthenticationScreen.kt:112` | `stringResource`, `ClickableText`, `Space` |
| `LoginWithEmailPhoneScreen` | 手机号/邮箱登录页面外壳。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/LoginWithEmailPhoneScreen.kt:37` | `Scaffold`, `TopBar`, `stringResource`, `Icon`, `painterResource`, `Column`, `LoginPager` |
| `LoginPager` | 登录页的手机号和邮箱 Tab 分页容器。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/LoginWithEmailPhoneScreen.kt:67` | `rememberPagerState`, `rememberCoroutineScope`, `LaunchedEffect`, `TabRow`, `Divider`, `Tab`, `Text`, `stringResource`, `HorizontalPager`, `PhoneTabScreen`, `EmailUsernameTabScreen` |
| `EmailUsernameTabScreen` | 邮箱/用户名登录 Tab 内容。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/EmailUsernameTabScreen.kt:38` | `collectAsState`, `Column`, `EmailField`, `Space`, `CustomButton`, `stringResource` |
| `EmailField` | 邮箱输入框组件。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/EmailUsernameTabScreen.kt:78` | `collectAsState`, `TextField`, `Text`, `stringResource`, `IconButton`, `Icon`, `painterResource`, `LaunchedEffect` |
| `PrivacyPolicyText` | 邮箱登录页隐私协议可点击文本。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/EmailUsernameTabScreen.kt:129` | `stringResource`, `ClickableText` |
| `DomainSuggestion` | 邮箱域名建议横向列表。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/EmailUsernameTabScreen.kt:174` | `LazyRow`, `items`, `Box`, `ClickableText` |
| `PhoneTabScreen` | 手机号登录 Tab 内容。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/PhoneTabScreen.kt:39` | `collectAsState`, `Column`, `PhoneNumberField`, `Space`, `CustomButton`, `stringResource` |
| `PhoneNumberField` | 手机号输入框组件。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/PhoneTabScreen.kt:68` | `collectAsState`, `TextField`, `Text`, `stringResource`, `Row`, `Icon`, `painterResource`, `Box`, `IconButton`, `LaunchedEffect` |
| `InfoAnnotatedText` | 手机登录页说明和 Learn More 可点击文本。 | `TikTok-Compose/feature/loginwithemailphone/src/main/java/com/puskal/loginwithemailphone/tabs/PhoneTabScreen.kt:144` | `stringResource`, `ClickableText` |

## 首页、推荐流、关注流与直播

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `HomeScreen` | 首页根页面，承载关注/推荐分页和顶部直播导航。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/HomeScreen.kt:44` | `rememberPagerState`, `rememberCoroutineScope`, `TikTokTheme`, `Box`, `HorizontalPager`, `FollowingScreen`, `ForYouTabScreen`, `LiveChineseTopNav` |
| `LiveChineseTopNav` | 首页顶部的直播/关注/推荐导航区域。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/HomeScreen.kt:85` | `Surface`, `Box`, `Row`, `Icon`, `painterResource`, `Column`, `Text`, `Spacer` |
| `ForYouTabScreen` | 推荐视频流 Tab 页面。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/foryou/ForYouTabScreen.kt:30` | `collectAsState`, `Box`, `rememberFeedScene`, `LaunchedEffect`, `DisposableEffect`, `TikTokVerticalVideoPager` |
| `FollowingScreen` | 关注创作者视频流页面。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/following/FollowingTabScreen.kt:34` | `collectAsState`, `Column`, `Space`, `Text`, `stringResource`, `VideoItem` |
| `VideoItem` | 关注页横向创作者卡片分页。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/following/FollowingTabScreen.kt:80` | `rememberPagerState`, `HorizontalPager`, `CreatorCard` |
| `CreatorCard` | 关注页单个创作者视频卡片。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/tab/following/component/ContentCreatorVideoCard.kt:43` | `Card`, `Box`, `VideoPlayer`, `Icon`, `painterResource`, `Column`, `AsyncImage`, `Text`, `Button`, `stringResource`, `Space` |
| `LiveRoomScreen` | 直播间主页面，包括视频背景、顶部信息、商品卡、评论流和底部输入栏。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:75` | `collectAsState`, `remember`, `LaunchedEffect`, `Box`, `CircularProgressIndicator`, `BoxWithConstraints`, `MockLiveVideo`, `Column`, `LiveRoomTopBar`, `Spacer`, `Row`, `LiveCapsuleTag`, `LiveProductFloatingCard`, `LiveCommentStack`, `LiveBottomInputBar` |
| `LiveRoomTopBar` | 直播间顶部主播信息、关注按钮、人数标签和关闭按钮。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:216` | `Row`, `Surface`, `AsyncImage`, `Column`, `Text`, `LiveGradientButton`, `LiveTopPill`, `IconButton`, `Icon`, `painterResource` |
| `LiveGradientButton` | 直播间渐变风格文字按钮。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:295` | `Surface`, `Box`, `Text` |
| `LiveTopPill` | 直播间顶部胶囊标签。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:321` | `Surface`, `Text` |
| `LiveCapsuleTag` | 直播间中部活动/状态胶囊标签。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:337` | `Surface`, `Text` |
| `LiveProductFloatingCard` | 直播间浮动商品卡片。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:353` | `Surface`, `Row`, `Box`, `Column`, `Text` |
| `LiveCommentStack` | 直播间评论列表容器。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:415` | `Surface`, `LazyColumn`, `items`, `LiveCommentBubble` |
| `LiveCommentBubble` | 直播间单条评论气泡。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:443` | `Surface`, `Row`, `Text` |
| `LiveBottomInputBar` | 直播间底部评论输入和快捷按钮栏。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:486` | `Row`, `Surface`, `BasicTextField`, `Box`, `Text`, `Icon`, `painterResource` |
| `MockLiveVideo` | 直播间模拟视频播放器背景。 | `TikTok-Compose/feature/home/src/main/java/com/puskal/home/live/LiveRoomScreen.kt:557` | `remember`, `DisposableEffect`, `AndroidView` |

## 评论列表

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `CommentListScreen` | 评论列表底部页，包含评论头部、推荐评论、评论列表、输入框和发送逻辑入口。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:67` | `collectAsState`, `Column`, `Space`, `CommentHeader`, `LazyColumn`, `SearchRecommendationComment`, `items`, `CommentItem`, `CommentUserField` |
| `CommentHeader` | 评论列表顶部标题和关闭按钮。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:109` | `Box`, `Text`, `Icon`, `painterResource` |
| `CommentLikeButton` | 评论点赞按钮及数量展示。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:144` | `Row`, `Icon`, `painterResource`, `Text` |
| `CommentDislikeButton` | 评论点踩按钮。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:177` | `Row`, `Icon`, `painterResource` |
| `SearchRecommendationComment` | 评论区中的搜索推荐卡片。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:201` | `ConstraintLayout`, `AsyncImage`, `Text`, `CommentLikeButton`, `CommentDislikeButton`, `Space` |
| `CommentItem` | 评论列表单条评论行。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:315` | `ConstraintLayout`, `AsyncImage`, `Text`, `stringResource`, `CommentLikeButton`, `CommentDislikeButton`, `Space` |
| `CommentUserField` | 评论输入框和发送按钮区域。 | `TikTok-Compose/feature/commentlisting/src/main/java/com/puskal/commentlisting/CommentListScreen.kt:414` | `Column`, `Row`, `Text`, `OutlinedTextField`, `stringResource`, `TextButton` |

## 拍摄与模板

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `CameraMediaScreen` | 拍摄/模板模块外壳，横向分页切换相机和模板页。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/CameraMediaScreen.kt:37` | `rememberPagerState`, `rememberCoroutineScope`, `DisposableEffect`, `Column`, `Box`, `HorizontalPager`, `CameraScreen`, `TemplateScreen`, `BottomTabLayout` |
| `BottomTabLayout` | 拍摄模块底部 Camera/Templates Tab。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/CameraMediaScreen.kt:101` | `ScrollableTabRow`, `Tab`, `Column`, `Text`, `stringResource`, `Box` |
| `CameraScreen` | 相机页入口，按权限状态显示相机预览或权限说明页。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:66` | `LaunchedEffect`, `Column`, `CameraPreview`, `CameraMicrophoneAccessPage` |
| `CameraMicrophoneAccessPage` | 相机/麦克风权限说明页。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:120` | `Column`, `Icon`, `painterResource`, `MediumSpace`, `Text`, `stringResource`, `Space`, `Card`, `Row`, `Divider`, `Spacer`, `FooterCameraController` |
| `CameraPreview` | 相机预览页面，包含 Android CameraX 预览、底部控制和侧边控制。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:229` | `Box`, `AndroidView`, `FooterCameraController`, `CameraSideControllerSection`, `LaunchedEffect`, `Icon`, `painterResource`, `Row`, `Text`, `stringResource` |
| `FooterCameraController` | 相机页底部拍摄模式、相册入口和拍摄按钮区域。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:334` | `rememberCoroutineScope`, `rememberLazyListState`, `Column`, `Box`, `LazyRow`, `itemsIndexed`, `Text`, `MediumSpace`, `Row`, `Image`, `painterResource`, `stringResource`, `CaptureButton` |
| `CameraSideControllerSection` | 相机页右侧工具栏容器。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:471` | `Column`, `ControllerItem` |
| `ControllerItem` | 相机页右侧单个工具项。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/CameraScreen.kt:489` | `Column`, `Icon`, `painterResource`, `Text`, `stringResource` |
| `TemplateScreen` | 模板选择页入口，展示模板分页和上传照片按钮。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/TemplateScreen.kt:40` | `collectAsState`, `LaunchedEffect`, `Column`, `IconButton`, `Icon`, `painterResource`, `TemplatePager`, `CustomButton`, `stringResource`, `LargeSpace` |
| `TemplatePager` | 模板页横向卡片分页和当前模板说明。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/TemplateScreen.kt:84` | `rememberPagerState`, `Text`, `Space`, `MediumSpace`, `HorizontalPager`, `SingleTemplateCard`, `SmallSpace` |
| `SingleTemplateCard` | 单个模板图片卡片。 | `TikTok-Compose/feature/cameramedia/src/main/java/com/puskal/cameramedia/tabs/TemplateScreen.kt:121` | `Card`, `Box`, `AsyncImage` |

## 创作者主页与创作者视频

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `CreatorProfileScreen` | 创作者主页，展示顶部栏、用户资料、公开视频/喜欢 Tab。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorprofile/CreatorProfileScreen.kt:41` | `collectAsState`, `Scaffold`, `TopBar`, `Icon`, `painterResource`, `Column`, `ProfileDetails`, `VideoListingPager` |
| `ProfileDetails` | 创作者资料详情区域，包括头像、用户名、粉丝数据、关注按钮、社交入口和简介。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorprofile/CreatorProfileScreen.kt:87` | `AsyncImage`, `Row`, `Text`, `Icon`, `painterResource`, `ConstraintLayout`, `stringResource`, `Divider`, `Button`, `Box` |
| `VideoListingPager` | 创作者主页视频列表 Tab 和分页容器。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/component/VideoListingPager.kt:40` | `rememberPagerState`, `rememberCoroutineScope`, `LazyColumn`, `TabRow`, `Divider`, `Tab`, `Icon`, `painterResource`, `HorizontalPager`, `PublicVideoTab`, `LikeVideoTab` |
| `PublicVideoTab` | 创作者公开视频 Tab。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorprofile/tabs/PublicVideoTab.kt:16` | `collectAsState`, `VideoGrid` |
| `LikeVideoTab` | 创作者喜欢视频 Tab。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorprofile/tabs/LikedVideoTab.kt:27` | `collectAsState`, `PrivateLikedVideoLayout` |
| `PrivateLikedVideoLayout` | 喜欢视频私密提示页。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorprofile/tabs/LikedVideoTab.kt:42` | `Column`, `Space`, `Text`, `stringResource` |
| `VideoGrid` | 三列视频缩略图网格。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/component/VideoGrid.kt:43` | `LazyVerticalGrid`, `itemsIndexed`, `VideoGridItem` |
| `VideoGridItem` | 视频网格单个缩略图项。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/component/VideoGrid.kt:80` | `Box`, `AsyncImage`, `LaunchedEffect`, `Row`, `Icon`, `painterResource`, `Text` |
| `CreatorVideoPagerScreen` | 创作者公开视频全屏滑动播放页，带搜索栏和评论输入区域。 | `TikTok-Compose/feature/creatorprofile/src/main/java/com/puskal/creatorprofile/screen/creatorvideo/CreatorVideoPagerScreen.kt:29` | `collectAsState`, `Scaffold`, `ContentSearchBar`, `stringResource`, `Column`, `TikTokVerticalVideoPager`, `Divider`, `OutlinedTextField`, `Text`, `Row`, `Icon`, `painterResource` |

## 其他功能页

| 页面/Composable | 中文描述 | 代码位置 | 直接组件/API |
|---|---|---|---|
| `FriendsScreen` | 好友页外壳，目前主要是顶部栏和空内容区域。 | `TikTok-Compose/feature/friends/src/main/java/com/puskal/friends/FriendsScreen.kt:20` | `Scaffold`, `TopBar`, `stringResource`, `Column`, `LaunchedEffect` |
| `InboxScreen` | 收件箱页外壳。 | `TikTok-Compose/feature/inbox/src/main/java/com/puskal/inbox/InboxScreen.kt:24` | `Scaffold`, `TopBar`, `stringResource`, `Column` |
| `UnAuthorizedInboxScreen` | 未登录收件箱提示页。 | `TikTok-Compose/feature/inbox/src/main/java/com/puskal/inbox/InboxScreen.kt:45` | `Column`, `Image`, `painterResource`, `Text`, `stringResource`, `CustomButton` |
| `MyProfileScreen` | 我的主页外壳，包含顶部栏和内容容器。 | `TikTok-Compose/feature/myprofile/src/main/java/com/puskal/myprofile/MyProfileScreen.kt:26` | `Scaffold`, `TopBar`, `stringResource`, `IconButton`, `Icon`, `painterResource`, `Column` |
| `UnAuthorizedInboxScreen` | 我的主页中的未登录提示页；函数名与 Inbox 模块同名但包路径不同。 | `TikTok-Compose/feature/myprofile/src/main/java/com/puskal/myprofile/MyProfileScreen.kt:54` | `Column`, `Image`, `painterResource`, `Text`, `stringResource`, `CustomButton` |
| `SettingScreen` | 设置页，展示顶部栏和分组设置项。 | `TikTok-Compose/feature/setting/src/main/java/com/puskal/setting/SettingScreen.kt:33` | `collectAsState`, `Scaffold`, `TopBar`, `stringResource`, `Column`, `Text`, `MediumSpace`, `GroupedUiCardSection`, `Space` |
| `GroupedUiCardSection` | 设置页分组卡片和单行设置项组件。 | `TikTok-Compose/feature/setting/src/main/java/com/puskal/setting/SettingScreen.kt:89` | `Text`, `Space`, `Card`, `Column`, `Row`, `Icon`, `painterResource`, `stringResource`, `MediumSpace` |
