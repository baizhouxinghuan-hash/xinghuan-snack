package xiaobaisnack.crawler.ui;

import xiaobaisnack.crawler.api.CrawlController;
import xiaobaisnack.crawler.api.StatsController;
import xiaobaisnack.crawler.core.CrawlerEngine;
import xiaobaisnack.crawler.scheduler.TaskScheduler;
import xiaobaisnack.crawler.url.manager.UrlRepository;
import xiaobaisnack.crawler.search.engine.SearchEngine;
import xiaobaisnack.crawler.ui.components.*;
import xiaobaisnack.crawler.ui.model.CrawlTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * ⭐ 主窗口 - 星幻爬虫 StarMirage Crawler
 *
 * 功能：
 * - 爬虫管理主界面
 * - 搜索框 + 搜索结果列表 + 页面内容展示
 * - URL输入 + 爬取表格 + 统计面板
 * - 整合所有UI组件
 */
public class MainWindow extends JFrame {

    private final CrawlController crawlController;
    private final StatsController statsController;
    private final SearchEngine searchEngine;
    private final UIController uiController;

    // UI组件
    private SearchBar searchBar;
    private ResultList resultList;
    private ContentView contentView;
    private ControlPanel controlPanel;
    private StatsPanel statsPanel;
    private UrlInputPanel urlInputPanel;
    private UrlTableView urlTableView;
    private LogPanel logPanel;

    public MainWindow(CrawlController crawlController, StatsController statsController, SearchEngine searchEngine) {
        this.crawlController = crawlController;
        this.statsController = statsController;
        this.searchEngine = searchEngine;
        this.uiController = new UIController(this);

        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        setTitle("星幻爬虫 StarMirage Crawler v1.1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        // 主面板使用分割面板
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setResizeWeight(0.25);

        // ===== 左侧面板：搜索 + 结果列表 =====
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        searchBar = new SearchBar(uiController);
        resultList = new ResultList();

        // 点击搜索结果时显示内容
        resultList.getJList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                uiController.showSelectedContent();
            }
        });

        leftPanel.add(searchBar, BorderLayout.NORTH);
        leftPanel.add(resultList, BorderLayout.CENTER);

        // ===== 右侧面板：爬虫管理 + 内容展示 =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 右上：控制面板
        controlPanel = new ControlPanel(uiController);

        // 右中：URL输入 + 统计 + 表格
        JPanel centerRightPanel = new JPanel(new BorderLayout(5, 5));

        urlInputPanel = new UrlInputPanel(uiController);
        statsPanel = new StatsPanel();
        urlTableView = new UrlTableView();

        // 点击表格行时显示内容
        urlTableView.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                uiController.showSelectedUrlContent();
            }
        });

        JPanel urlStatsPanel = new JPanel(new BorderLayout(5, 5));
        urlStatsPanel.add(urlInputPanel, BorderLayout.NORTH);
        urlStatsPanel.add(statsPanel, BorderLayout.CENTER);

        centerRightPanel.add(urlStatsPanel, BorderLayout.NORTH);
        centerRightPanel.add(urlTableView, BorderLayout.CENTER);

        // 右下：内容展示
        contentView = new ContentView();

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(400);
        rightSplitPane.setResizeWeight(0.5);
        rightSplitPane.setTopComponent(centerRightPanel);
        rightSplitPane.setBottomComponent(contentView);

        JPanel rightTopPanel = new JPanel(new BorderLayout(5, 5));
        rightTopPanel.add(controlPanel, BorderLayout.NORTH);
        rightTopPanel.add(rightSplitPane, BorderLayout.CENTER);

        rightPanel.add(rightTopPanel, BorderLayout.CENTER);

        // 底部：日志
        logPanel = new LogPanel();

        JPanel rightWithLog = new JPanel(new BorderLayout(5, 5));
        rightWithLog.add(rightPanel, BorderLayout.CENTER);
        rightWithLog.add(logPanel, BorderLayout.SOUTH);

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightWithLog);

        add(mainSplitPane, BorderLayout.CENTER);

        // 菜单栏
        setJMenuBar(createMenuBar());

        setVisible(true);
    }

    /**
     * 创建菜单栏
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        JMenuItem startItem = new JMenuItem("开始爬取");
        startItem.addActionListener(e -> uiController.startCrawl());
        JMenuItem stopItem = new JMenuItem("停止爬取");
        stopItem.addActionListener(e -> uiController.stopCrawl());
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(startItem);
        fileMenu.add(stopItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 编辑菜单（增删改查 + 批量操作）
        JMenu editMenu = new JMenu("编辑");
        JMenuItem addItem = new JMenuItem("添加页面");
        addItem.addActionListener(e -> uiController.addNewPage());
        JMenuItem editItem = new JMenuItem("编辑选中");
        editItem.addActionListener(e -> uiController.editSelectedPage());
        JMenuItem deleteItem = new JMenuItem("删除选中");
        deleteItem.addActionListener(e -> uiController.deleteSelectedPage());
        JMenuItem batchDeleteItem = new JMenuItem("批量删除（多选）");
        batchDeleteItem.addActionListener(e -> uiController.deleteBatchPages());
        JMenuItem deleteAllItem = new JMenuItem("清空所有数据");
        deleteAllItem.addActionListener(e -> uiController.deleteAllPages());
        editMenu.add(addItem);
        editMenu.add(editItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        editMenu.add(batchDeleteItem);
        editMenu.add(deleteAllItem);

        // 视图菜单
        JMenu viewMenu = new JMenu("视图");
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(e -> uiController.refresh());
        JMenuItem clearLogItem = new JMenuItem("清空日志");
        clearLogItem.addActionListener(e -> logPanel.clear());
        viewMenu.add(refreshItem);
        viewMenu.add(clearLogItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * 显示关于对话框
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "星幻爬虫 StarMirage Crawler v1.1.0\n\n" +
            "功能：\n" +
            "  • 多线程网页采集（深度可控）\n" +
            "  • URL 规范化与去重\n" +
            "  • 正文清洗与 SQLite 本地存储\n" +
            "  • 本地全文检索 + 表格增删改查\n\n" +
            "技术栈：Java 17 + Swing + SQLite + jsoup",
            "关于星幻爬虫",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== 公开方法（供UIController调用） =====

    public void updateStats() {
        statsPanel.updateStats(statsController);
    }

    public void updateTable() {
        urlTableView.setData(statsController.getAllPages());
    }

    public void appendLog(String message) {
        logPanel.appendLog(message);
    }

    public String getSeedUrl() {
        return urlInputPanel.getUrl();
    }

    public String getSearchKeyword() {
        return searchBar.getKeyword();
    }

    public CrawlController getCrawlController() {
        return crawlController;
    }

    public StatsController getStatsController() {
        return statsController;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public UIController getUiController() {
        return uiController;
    }

    public ResultList getResultList() {
        return resultList;
    }

    public ContentView getContentView() {
        return contentView;
    }

    public UrlTableView getUrlTableView() {
        return urlTableView;
    }

    /**
     * ⭐ 静态启动方法（供MainApplication调用）
     */
    public static void launchApp(SearchEngine searchEngine, CrawlerEngine engine, UrlRepository urlRepo) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 设置系统外观
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // 使用默认外观
                }

                // 创建控制器
                TaskScheduler scheduler = new TaskScheduler(urlRepo, engine, 4, 1000);
                CrawlController crawlController = new CrawlController(urlRepo, engine, scheduler);
                StatsController statsController = new StatsController();

                // 创建并显示主窗口
                MainWindow window = new MainWindow(crawlController, statsController, searchEngine);
                window.appendLog("[系统] 星幻爬虫 StarMirage Crawler v1.1.0 已启动");
                window.appendLog("[系统] 左侧输入关键词搜索，右侧输入种子URL开始采集");

                // 每 2 秒刷新统计；页面数量变化时同步刷新表格
                Timer timer = new Timer(2000, event -> {
                    window.updateStats();
                    int rowCount = window.getUrlTableView().getTable().getRowCount();
                    if (rowCount != statsController.getAllPages().size()) {
                        window.updateTable();
                    }
                });
                timer.setRepeats(true);
                timer.start();
            }
        });
    }
}