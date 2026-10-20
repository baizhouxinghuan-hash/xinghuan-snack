package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.storage.entity.PageEntity;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ⭐ 搜索结果列表组件
 *
 * 功能：
 * - 展示搜索结果
 * - 支持点击查看详情
 */
public class ResultList extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JList<String> resultJList;
    private List<PageEntity> currentResults;

    public ResultList() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("搜索结果"));

        listModel = new DefaultListModel<>();
        resultJList = new JList<>(listModel);
        resultJList.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        resultJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(resultJList);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签
        JLabel statusLabel = new JLabel("请输入关键词搜索");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置搜索结果
     */
    public void setResults(List<PageEntity> results) {
        this.currentResults = results;
        listModel.clear();

        if (results == null || results.isEmpty()) {
            listModel.addElement("未找到相关结果");
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            PageEntity page = results.get(i);
            String display = (i + 1) + ". " + page.getTitle() + " - " + page.getUrl();
            listModel.addElement(display);
        }
    }

    /**
     * 获取选中的页面
     */
    public PageEntity getSelectedPage() {
        int index = resultJList.getSelectedIndex();
        if (index >= 0 && currentResults != null && index < currentResults.size()) {
            return currentResults.get(index);
        }
        return null;
    }

    /**
     * 获取JList组件（用于添加事件监听）
     */
    public JList<String> getJList() {
        return resultJList;
    }

    /**
     * 清空结果
     */
    public void clear() {
        listModel.clear();
        currentResults = null;
    }
}