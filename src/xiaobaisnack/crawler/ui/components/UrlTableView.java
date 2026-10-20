package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.storage.entity.PageEntity;
import xiaobaisnack.crawler.ui.model.CrawlTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ⭐ URL表格视图组件
 *
 * 功能：
 * - 以表格形式展示已抓取的URL列表
 * - 支持排序和选择
 */
public class UrlTableView extends JPanel {

    private final JTable table;
    private final CrawlTableModel tableModel;

    public UrlTableView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("已抓取页面列表"));

        tableModel = new CrawlTableModel();
        table = new JTable(tableModel);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300);  // URL
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // 标题
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // 状态
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // 时间

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 更新表格数据
     */
    public void setData(List<PageEntity> pages) {
        tableModel.setData(pages);
    }

    /**
     * 获取选中的页面（单选时使用）
     */
    public PageEntity getSelectedPage() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return tableModel.getPageAt(row);
        }
        return null;
    }

    /**
     * 获取所有选中的页面（多选时使用）
     */
    public List<PageEntity> getSelectedPages() {
        List<PageEntity> selected = new java.util.ArrayList<>();
        int[] rows = table.getSelectedRows();
        for (int row : rows) {
            PageEntity page = tableModel.getPageAt(row);
            if (page != null) {
                selected.add(page);
            }
        }
        return selected;
    }

    /**
     * 获取选中的行数
     */
    public int getSelectedRowCount() {
        return table.getSelectedRowCount();
    }

    /**
     * 全选
     */
    public void selectAll() {
        table.selectAll();
    }

    /**
     * 获取JTable组件
     */
    public JTable getTable() {
        return table;
    }

    /**
     * 清空表格
     */
    public void clear() {
        tableModel.setData(null);
    }
}