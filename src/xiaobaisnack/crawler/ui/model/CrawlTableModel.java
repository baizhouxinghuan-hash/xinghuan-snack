package xiaobaisnack.crawler.ui.model;

import xiaobaisnack.crawler.storage.entity.PageEntity;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * ⭐ 爬取数据表格模型
 *
 * 功能：
 * - 为JTable提供数据模型
 * - 显示页面信息
 */
public class CrawlTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ID", "URL", "标题", "状态", "时间"};
    private List<PageEntity> data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PageEntity page = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return page.getId();
            case 1: return page.getUrl();
            case 2: return page.getTitle();
            case 3: return page.getStatus();
            case 4: return page.getCrawlTime();
            default: return "";
        }
    }

    /**
     * 设置数据
     */
    public void setData(List<PageEntity> pages) {
        this.data = pages != null ? pages : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * 获取指定行的页面
     */
    public PageEntity getPageAt(int row) {
        if (row >= 0 && row < data.size()) {
            return data.get(row);
        }
        return null;
    }
}