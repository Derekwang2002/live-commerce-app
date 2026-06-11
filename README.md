# Live Commerce App

直播电商购物链路示例项目：覆盖商品曝光、详情决策、加购结算到支付反馈的完整闭环。

## 目录结构

```
live-commerce-app/
├── mobile/   # Android 客户端（Kotlin + Jetpack Compose）
├── backend/  # 后端 API（Node.js + node:http + 内存 store）
└── docs/     # 协作约定（git 规范等）
```

## 启动

### 后端
```bash
cd backend
npm start          # http://localhost:3000
npm test           # 跑接口和数据层用例
```

### 移动端
用 Android Studio 打开 `mobile/` 目录，或者命令行：
```bash
cd mobile
./gradlew :app:assembleDebug          # 打包 APK
./gradlew :app:testDebugUnitTest      # 跑单元测试
```

模拟器中后端地址默认 `http://10.0.2.2:3000/`（与本机 `localhost:3000` 对应）。

## 技术栈

- **Mobile**：Kotlin 2.0、Jetpack Compose、Navigation Compose、Retrofit + OkHttp、kotlinx.serialization、Coil、Room、androidx.lifecycle ViewModel。
- **Backend**：Node.js（原生 `node:http`）、内存 store，无 ORM、无外部数据库依赖。

## 分层架构

```
UI (Compose)  →  ViewModel  →  CommerceRepository  →  MemoryCache / Room / Retrofit
```

- UI 只组合 Compose 组件和响应事件，不直接读 Repository。
- ViewModel 提供页面所需状态和动作，调用 Repository。
- Repository 统一 mock、Room 快照、远端 API 三个数据源。

## Git 协作

主分支 `main`，开发分支 `dev`，功能分支从 `dev` 切。详见 [docs/conventions/git.md](docs/conventions/git.md)。
