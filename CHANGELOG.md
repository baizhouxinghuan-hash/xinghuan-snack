# 更新记录

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.1.0] - 2026-10-20

更名与稳定版：从「白昼星环浏览器」正式更名为 **星幻爬虫 / StarMirage Crawler**，并修复数据链路上的三个 P0 问题。

### 修复

- **搜索结果丢失 id**：`PageDao.search` 未读取 `id` 列，导致从搜索结果点「编辑 / 删除」时静默失效。现在 `id`、`status`、`crawl_time` 一并返回。
- **抓取时间恒为空**：入库时未写入 `crawl_time`，表格时间列永远空白。现在抓取完成即写入 `TimeUtils.now()`。
- **重复抓取不更新**：`INSERT OR IGNORE` 使同一 URL 二次抓取时正文与时间都不刷新。改为 `INSERT ... ON CONFLICT(url) DO UPDATE`。
- 队列统计失真：`PriorityManager` 与 `UrlRepository` 各存一份队列，界面上的「待抓取」与实际不符。已删除 `PriorityManager`，统一为单队列。
- 调度器竞态：原来「队列为空即退出」会让工作线程提前结束。现在线程常驻，只有空闲才休眠。
- 编辑 / 删除 / 批量删除在记录不存在时无反馈。现在按受影响行数提示成功或失败。

### 新增

- `storage/Database`：集中管理连接，启用 WAL、`busy_timeout=5000`、`synchronous=NORMAL`；支持 `-Dcrawler.db` 与 `CRAWLER_DB` 指定数据库路径。
- `pages.source` 列，区分本地入库（LOCAL）与外部搜索（EXTERNAL）。
- `app/SmokeTest`：URL 规范化、队列去重、数据库增删改查与重复抓取刷新共 24 项断言，`--net` 追加 5 项真实抓取断言。
- 主窗口每 2 秒自动刷新统计，页面数量变化时同步刷新表格。
- 数据库结构升级：用 `PRAGMA table_info` 检测并补齐缺失列，旧库可直接沿用。

### 改进

- `UrlNormalizer`：保留查询串、去 fragment、去默认端口、根路径去尾斜杠、非法或非 http(s) 返回 null。
- `UrlRepository`：`PriorityBlockingQueue` + `putIfAbsent` 去重 + `discovered` 计数，`addUrl` 返回是否真正入队。
- `PageFetcher` 复用 `HttpUtils`：状态码校验、`Content-Type` 过滤、gzip/deflate 解压、编码识别与 UTF-8 回退、正文 2MB 截断。
- `HttpUtils` User-Agent 改为 `StarMirageCrawler/1.0`；charset 不被 JVM 支持时回退 UTF-8。
- 外部搜索抓取失败时不再用摘要冒充正文入库。
- 界面标题、关于对话框、启动横幅、帮助文案统一为星幻爬虫。
- `CrawlConfig` 默认 UA 与 `MainWindow` 注释中的旧名同步为 StarMirage Crawler；文档中的仓库地址改为实际仓库 `xinghuan-snack`。

### 移除

- `scheduler/PriorityManager`（能力已并入 `UrlRepository`）。
- `app/test.java`（依赖已变更的构造器，功能由 `SmokeTest` 取代）。

## [1.0.0] - 2026-10-01

- 首个可运行版本：多线程抓取、深度控制、正文清洗、SQLite 存储、Swing 界面、本地搜索、URL 表格增删改查。
