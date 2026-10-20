package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.api.StatsController;
import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.ui.model.CrawlTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ⭐ 爬取数据表格面板
 *
 * 功能：
 * - 显示已爬取的页面列表
 * - 表格形式展示
 */
public class CrawlTablePanel extends JPanel {

    private JTable table;
    private CrawlTableModel tableModel;

    public CrawlTablePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("爬取数据"));

        tableModel = new CrawlTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 更新表格数据
     */
    public void updateTable(StatsController statsController) {
        try {
            List<PageEntity> pages = statsController.getAllPages();
            tableModel.setData(pages);
        } catch (Exception e) {
            // 忽略更新错误
        }
    }
}