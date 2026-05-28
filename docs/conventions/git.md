# Git 协作规范（暂定）

## 分支策略

- `main`: 最终稳定版本，用于提交和演示; 受保护分支，仅通过 PR 合入，必须 1 个 approve
- `dev`: 日常集成版本，功能先合到这里
- `featture/`: 功能开发分支
- 如果有其他需求再开新的分支


## Commit 规范

采用 [Conventional Commits](https://www.conventionalcommits.org/)：

```
<type>(<scope>): <subject>

[optional body]
```

**type 取值**：
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档变更
- `style`: 代码格式（不影响逻辑）
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具链

示例：
- `feat(cart): add cart item quantity selector`
- `fix(payment): resolve countdown timer bug`
- `docs(readme): update project structure`


## PR 流程

1. 从 `dev` 拉新分支：`feature/...`
2. 开发并提交，小步提交，commit message 保持清晰
3. Push 后在 GitHub 开 PR，指定对方为 Reviewer
4. PR 描述包含：
   - 改了什么
   - 为什么这么改
   - 怎么测试
   - 截图 / 录屏（移动端功能必须）
5. 对方 review approve 后，由 PR 发起人 merge
6. Merge 后本地删除分支：`git branch -d feature/xxx`
7. 同步最新 `dev`：`git pull origin dev`


## 文件归属（减少冲突）

为了减少 merge conflict，原则上按目录划分主要负责人；如果需要修改对方负责的目录，先私聊简单同步。

| 路径 | 主要负责人 |
|------|------------|
| `README.md` | 共同维护 |

## 冲突解决约定

- 不对 `main` / `dev` 强推 (`git push --force`)
- 遇到 conflict，由 PR 发起人负责拉取最新 `dev` 并解决后再合并