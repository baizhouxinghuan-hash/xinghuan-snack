package xiaobaisnack.crawler.ui;

import xiaobaisnack.crawler.storage.dao.PageDao;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.utils.TimeUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ UI控制器
 *
 * 功能：
 * - 处理UI事件
 * - 协调UI组件和业务逻辑
 * - 管理搜索（本地+外部混合）、爬虫、内容展示
 * - 增删改查操作
 */
public class UIController {

    private final MainWindow mainWindow;
    private final PageDao pageDao;

    public UIController(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.pageDao = new PageDao();
    }

    /**
     * 开始爬取
     */
    public void startCrawl() {
        String seedUrl = mainWindow.getSeedUrl();
        if (seedUrl == null || seedUrl.trim().isEmpty()) {
            mainWindow.appendLog(TimeUtils.logTime() + " [错误] 请输入种子URL");
            return;
        }

        mainWindow.appendLog(TimeUtils.logTime() + " [信息] 开始爬取: " + seedUrl);

        new Thread(() -> {
            try {
                mainWindow.getCrawlController().startCrawl(seedUrl.trim());
                mainWindow.appendLog(TimeUtils.logTime() + " [信息] 爬虫已启动");
            } catch (Exception e) {
                mainWindow.appendLog(TimeUtils.logTime() + " [错误] 启动失败: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 停止爬取
     */
    public void stopCrawl() {
        mainWindow.appendLog(TimeUtils.logTime() + " [信息] 停止爬虫");

        new Thread(() -> {
            try {
                mainWindow.getCrawlController().stopCrawl();
                mainWindow.appendLog(TimeUtils.logTime() + " [信息] 爬虫已停止");
            } catch (Exception e) {
                mainWindow.appendLog(TimeUtils.logTime() + " [错误] 停止失败: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        mainWindow.updateStats();
        mainWindow.updateTable();
        mainWindow.appendLog(TimeUtils.logTime() + " [信息] 界面已刷新");
    }

    /**
     * 添加URL
     */
    public void addUrl(String url) {
        if (url == null || url.trim().isEmpty()) return;

        mainWindow.getCrawlController().addUrl(url.trim());
        mainWindow.appendLog(TimeUtils.logTime() + " [信息] 添加URL: " + url);
    }

    /**
     * ⭐ 混合搜索功能
     * 1. 先查本地数据库
     * 2. 借助外部搜索引擎搜索（如果配置了）
     * 3. 合并结果 + 加权排序
     * 4. 展示结果
     */
    public void search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            mainWindow.appendLog(TimeUtils.logTime() + " [提示] 请输入搜索关键词");
            return;
        }

        mainWindow.appendLog(TimeUtils.logTime() + " [搜索] 搜索关键词: " + keyword);
        mainWindow.appendLog(TimeUtils.logTime() + " [搜索] 正在本地数据库搜索...");

        new Thread(() -> {
            try {
                // 混合搜索（本地 + 外部）
                List<PageEntity> results = mainWindow.getSearchEngine().search(keyword.trim());

                int localCount = 0;
                int externalCount = 0;
                if (results != null) {
                    for (PageEntity p : results) {
                        if ("LOCAL".equals(p.getSource())) {
                            localCount++;
                        } else {
                            externalCount++;
                        }
                    }
                }

                mainWindow.appendLog(TimeUtils.logTime() + " [搜索] 本地: " + localCount + " 条, 外部: " + externalCount + " 条, 共 " + (results != null ? results.size() : 0) + " 条");

                // 在UI线程更新结果列表
                final List<PageEntity> finalResults = results;
                SwingUtilities.invokeLater(() -> {
                    mainWindow.getResultList().setResults(finalResults);
                });
            } catch (Exception e) {
                mainWindow.appendLog(TimeUtils.logTime() + " [错误] 搜索失败: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * ⭐ 显示搜索结果中选中的页面内容
     */
    public void showSelectedContent() {
        PageEntity page = mainWindow.getResultList().getSelectedPage();
        if (page != null) {
            mainWindow.getContentView().showPage(page);
            mainWindow.appendLog(TimeUtils.logTime() + " [查看] 打开页面: " + page.getTitle());
        }
    }

    /**
     * ⭐ 显示URL表格中选中的页面内容
     */
    public void showSelectedUrlContent() {
        PageEntity page = mainWindow.getUrlTableView().getSelectedPage();
        if (page != null) {
            mainWindow.getContentView().showPage(page);
            mainWindow.appendLog(TimeUtils.logTime() + " [查看] 打开页面: " + page.getTitle());
        }
    }

    // =========================
    // ⭐⭐ 增删改查操作
    // =========================

    /**
     * 编辑选中的页面
     */
    public void editSelectedPage() {
        PageEntity page = mainWindow.getUrlTableView().getSelectedPage();
        if (page == null) {
            page = mainWindow.getResultList().getSelectedPage();
        }
        if (page == null) {
            mainWindow.appendLog(TimeUtils.logTime() + " [提示] 请先选择一条记录");
            return;
        }

        // 弹出编辑对话框
        JTextField titleField = new JTextField(page.getTitle(), 40);
        JTextArea contentArea = new JTextArea(page.getContent(), 10, 40);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("URL: " + page.getUrl()));
        panel.add(new JLabel("标题:"));
        panel.add(titleField);
        panel.add(new JLabel("内容:"));
        panel.add(contentScroll);

        int result = JOptionPane.showConfirmDialog(mainWindow, panel, "编辑页面",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            page.setTitle(titleField.getText().trim());
            page.setContent(contentArea.getText());
            int updated = pageDao.update(page);
            if (updated > 0) {
                mainWindow.appendLog(TimeUtils.logTime() + " [编辑] 已更新: " + page.getTitle());
            } else {
                mainWindow.appendLog(TimeUtils.logTime() + " [编辑] 更新失败：该记录可能已被删除");
            }
            refresh();
        }
    }

    /**
     * 删除选中的页面
     */
    public void deleteSelectedPage() {
        PageEntity page = mainWindow.getUrlTableView().getSelectedPage();
        if (page == null) {
            page = mainWindow.getResultList().getSelectedPage();
        }
        if (page == null) {
            mainWindow.appendLog(TimeUtils.logTime() + " [提示] 请先选择一条记录");
            return;
        }

        int result = JOptionPane.showConfirmDialog(mainWindow,
                "确定要删除 \"" + page.getTitle() + "\" 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            int deleted = pageDao.delete(page.getId());
            if (deleted > 0) {
                mainWindow.appendLog(TimeUtils.logTime() + " [删除] 已删除: " + page.getTitle());
            } else {
                mainWindow.appendLog(TimeUtils.logTime() + " [删除] 未删除任何记录（id=" + page.getId() + "，可能已不存在）");
            }
            refresh();
        }
    }

    /**
     * 批量删除选中的页面
     */
    public void deleteBatchPages() {
        List<PageEntity> selectedPages = mainWindow.getUrlTableView().getSelectedPages();
        if (selectedPages.isEmpty()) {
            mainWindow.appendLog(TimeUtils.logTime() + " [提示] 请先选择要删除的记录（可按住Ctrl或Shift多选）");
            return;
        }

        int result = JOptionPane.showConfirmDialog(mainWindow,
                "确定要删除选中的 " + selectedPages.size() + " 条记录吗？",
                "批量删除确认", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            List<Integer> ids = new ArrayList<>();
            for (PageEntity page : selectedPages) {
                ids.add(page.getId());
            }
            int deleted = pageDao.deleteBatch(ids);
            mainWindow.appendLog(TimeUtils.logTime() + " [批量删除] 已删除 " + deleted + " 条记录");
            refresh();
        }
    }

    /**
     * 清空所有数据
     */
    public void deleteAllPages() {
        int count = pageDao.getCount();
        if (count == 0) {
            mainWindow.appendLog(TimeUtils.logTime() + " [提示] 数据库中没有数据");
            return;
        }

        int result = JOptionPane.showConfirmDialog(mainWindow,
                "确定要清空所有 " + count + " 条数据吗？此操作不可恢复！",
                "清空所有数据", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            int deleted = pageDao.deleteAll();
            mainWindow.appendLog(TimeUtils.logTime() + " [清空] 已清空所有数据，共 " + deleted + " 条");
            refresh();
        }
    }

    /**
     * 手动添加新页面
     */
    public void addNewPage() {
        JTextField urlField = new JTextField(40);
        JTextField titleField = new JTextField(40);
        JTextArea contentArea = new JTextArea(5, 40);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("URL:"));
        panel.add(urlField);
        panel.add(new JLabel("标题:"));
        panel.add(titleField);
        panel.add(new JLabel("内容:"));
        panel.add(contentScroll);

        int result = JOptionPane.showConfirmDialog(mainWindow, panel, "添加新页面",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String url = urlField.getText().trim();
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (url.isEmpty()) {
                mainWindow.appendLog(TimeUtils.logTime() + " [提示] URL 不能为空");
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                mainWindow.appendLog(TimeUtils.logTime() + " [提示] 仅支持 http/https 地址");
                return;
            }

            PageEntity page = new PageEntity(url, title, content, "DONE", TimeUtils.now());
            page.setSource(PageEntity.SOURCE_LOCAL);
            pageDao.save(page);
            mainWindow.appendLog(TimeUtils.logTime() + " [添加] 已保存: " + (title.isEmpty() ? url : title));
            refresh();
        }
    }

    /**
     * 获取PageDao（供外部使用）
     */
    public PageDao getPageDao() {
        return pageDao;
    }
}