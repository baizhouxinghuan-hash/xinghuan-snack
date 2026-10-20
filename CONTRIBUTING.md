# 贡献指南

感谢你愿意为 **星幻爬虫 / StarMirage Crawler** 出一份力。提交前请先阅读以下约定。

## 开发环境

- JDK 17+
- 依赖位于 `lib/`：`jsoup-1.22.1.jar`、`sqlite-jdbc-3.51.3.0.jar`
- 不使用 Maven / Gradle，构建方式就是 `javac`，请勿引入新的构建工具

## 本地构建与自测

Windows：

```bat
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*" @sources.txt
java -cp "target\classes;lib\*" xiaobaisnack.crawler.app.SmokeTest
```

macOS / Linux：

```bash
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d target/classes -cp "lib/*" @sources.txt
java -cp "target/classes:lib/*" xiaobaisnack.crawler.app.SmokeTest
```

要求：**全量编译 0 error**，`SmokeTest` 全部 PASS。涉及抓取链路的改动，请再手动执行一次 `--gui` 或 `start <URL>` 做真实验证。

## 代码约定

- 源文件 UTF-8 无 BOM；Java 缩进 4 空格。
- 保持现有分层：`core`（抓取）、`storage`（持久化）、`url`（队列与过滤）、`ui`（界面）。界面层不要直接写 SQL。
- 数据库连接统一通过 `storage/Database` 获取，不要各处 `DriverManager.getConnection`。
- 用户可见文案使用中文，注释简洁说明「为什么」而不是复述代码。
- 新增 URL 处理逻辑时，务必走 `UrlNormalizer.normalize`，避免出现 `/a` 与 `/a/` 两份记录。
- 包名 `xiaobaisnack.crawler` 为历史遗留，**不要**大规模重命名，避免无意义 diff。

## 提交信息

采用 `类型: 简述` 格式：

```
fix: 搜索结果丢失 id 导致编辑删除失效
feat: 队列空闲时才休眠，提升抓取吞吐
docs: 补充数据库路径配置说明
refactor: 合并 PriorityManager 到 UrlRepository
test: 增加 URL 规范化用例
```

## Pull Request 检查清单

- [ ] 已执行全量编译，无 error
- [ ] 已运行 `SmokeTest`，全部 PASS
- [ ] 变更涉及界面或抓取时，附上验证步骤或截图
- [ ] 已更新 `CHANGELOG.md`（如属用户可见变更）
- [ ] 未提交 `crawler.db`、`target/`、日志等本地产物

## 行为准则

保持友善、就事论事。请勿提交用于绕过目标站点访问限制、批量抓取隐私数据或规避法律的代码。
