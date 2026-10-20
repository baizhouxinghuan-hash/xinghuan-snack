# 星幻爬虫 · StarMirage Crawler

> 一个用 Java 17 + Swing 写的**桌面网页采集与本地全文检索工具**：输入种子网址，程序自动抓取页面、清洗正文、存入 SQLite，然后在界面里搜索、查看、编辑和删除。

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/UI-Swing-orange)
![SQLite](https://img.shields.io/badge/Storage-SQLite-003B57)
![License](https://img.shields.io/badge/License-MIT-green)

> 项目早期的包名仍为 `xiaobaisnack.crawler`，属于历史遗留，不影响使用。

---

## 它是什么 / 不是什么

| 是 | 不是 |
| --- | --- |
| 一个可运行的桌面采集 + 本地检索小工具 | 一个浏览器（没有 HTML 渲染、没有 JS 引擎、没有标签页） |
| 单机、单库、零服务端依赖 | 分布式爬虫框架 |
| 适合学习爬虫链路与 Swing 桌面开发 | 生产级搜索引擎 |

---

## 功能

### 已实现

- **多线程采集**：固定线程池，队列有任务时连续出队，只有空闲才休眠，不会固定间隔空转。
- **URL 规范化与去重**：统一 scheme/host 大小写、去掉默认端口、去掉 `#fragment`、根路径去尾斜杠，**保留查询串**（`?id=1` 与 `?id=2` 不会被误判为同一页）。
- **优先级队列**：单个 `PriorityBlockingQueue` 同时承担排队、去重与状态记录，`seed` 优先级最高。
- **深度控制**：按层级展开子链接，超过 `maxDepth` 停止扩散；`maxUrls` 基于「已发现 URL 数」限流。
- **URL 过滤**：基础黑名单 / 域名黑名单 / 正则黑名单三级过滤。
- **健壮下载**：2xx 校验、`Content-Type` 过滤（跳过图片/压缩包等非文本）、gzip/deflate 解压、`Content-Type` 与 `<meta charset>` 双重编码识别、编码不支持时回退 UTF-8、正文 2MB 截断。
- **正文清洗**：去脚本/样式/导航等噪声后抽取正文，`jsoup` 解析标题。
- **SQLite 持久化**：WAL 模式、`busy_timeout`、`synchronous=NORMAL`；重复抓取同一 URL 使用 `INSERT ... ON CONFLICT(url) DO UPDATE`，内容与抓取时间会被刷新。
- **本地搜索**：多关键词 AND 匹配（标题 + 正文），`LIKE` 通配符转义，结果上限 200 条，**搜索结果保留 id / 状态 / 抓取时间**。
- **桌面界面**：搜索栏、结果列表、正文查看、URL 表格、统计面板、日志面板；编辑 / 删除 / 多选批量删除 / 清空全部；每 2 秒自动刷新统计。
- **命令行模式**：`search` / `crawl` / `start` / `stats` / `help` / `exit`。
- **冒烟测试**：`SmokeTest` 覆盖 URL 规范化、队列去重、数据库增删改查（可选 `--net` 做真实抓取）。

### 规划中

- 倒排索引与 TF-IDF 相关性排序（当前是按关键词命中加权的简易排序）。
- `robots.txt` 解析与遵守。
- 队列持久化，支持中断后续爬。
- 结果导出 CSV / JSON。
- 界面内配置搜索引擎与抓取参数。

---

## 快速开始

### 环境要求

- JDK 17 或更高（`java -version` 验证）
- 依赖已放在 `lib/`：`jsoup-1.22.1.jar`、`sqlite-jdbc-3.51.3.0.jar`

### Windows（推荐）

```bat
start.bat
```

脚本会自动编译到 `target/classes` 并启动图形界面。

### 手动编译运行

Windows：

```bat
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*" @sources.txt
java -Dfile.encoding=UTF-8 -cp "target\classes;lib\*" xiaobaisnack.crawler.app.MainApplication --gui
```

macOS / Linux：

```bash
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d target/classes -cp "lib/*" @sources.txt
java -Dfile.encoding=UTF-8 -cp "target/classes:lib/*" xiaobaisnack.crawler.app.MainApplication --gui
```

不带 `--gui` 时进入命令行模式。

### 跑测试

```bash
java -cp "target/classes;lib/*" xiaobaisnack.crawler.app.SmokeTest
# 追加 --net 会额外真实抓取 https://example.com
```

---

## 使用步骤

1. 在右上「种子 URL」输入框填写起始地址，例如 `https://example.com`。
2. 点击「开始爬取」，日志区会显示入队、抓取、入库过程。
3. 左侧输入关键词搜索，点击结果查看正文。
4. 在 URL 表格中选中记录，可编辑、删除或多选批量删除。
5. 重复抓取同一 URL 会刷新正文与抓取时间，而不是被忽略。

---

## 数据库

- 默认文件：项目根目录 `crawler.db`（已加入 `.gitignore`）。
- 自定义路径：`-Dcrawler.db=D:/data/mine.db` 或环境变量 `CRAWLER_DB`。
- 数据表 `pages`：`id / url / title / content / status / crawl_time / source`，`url` 唯一。
- 修改字段结构后启动会自动补齐缺失列（`PRAGMA table_info`）。

---

## 项目结构

```
src/xiaobaisnack/crawler/
├── api/            对外接口：CrawlController、StatsController
├── app/            入口 MainApplication、SmokeTest
├── cleaner/        正文清洗
├── config/         抓取与站点配置
├── core/           CrawlerEngine、PageFetcher、PageParser、LinkExtractor
├── scheduler/      TaskScheduler（线程池）、DepthController
├── search/         SearchEngine、外部搜索、排序
├── storage/        Database、DAO、实体
├── ui/             Swing 界面与控制器
├── url/            UrlNormalizer、UrlRepository、过滤器
└── utils/          HttpUtils、TimeUtils
```

---

## 常见问题

**抓取中文页面乱码？**
`HttpUtils` 会依次看 `Content-Type` 与 `<meta charset>`，仍失败时按 UTF-8 解码。若目标站点声明了 JVM 不支持的编码名，也会自动回退。

**搜索结果里编辑/删除没反应？**
已在 v1.1.0 修复：搜索结果现在会带出数据库 `id`，编辑与删除按受影响行数给出提示。

**外部搜索为什么没结果？**
外部搜索默认关闭，需要在 `SiteConfig` 中配置 `searchUrl`（如 `https://www.bing.com/search?q={keyword}`），且部分搜索引擎有反爬限制。

---

## 参与贡献

见 [CONTRIBUTING.md](CONTRIBUTING.md)。欢迎提交 Issue 与 Pull Request。

## 许可证

[MIT](LICENSE) © 2026 StarMirage Crawler contributors

## 更新记录

见 [CHANGELOG.md](CHANGELOG.md)。
