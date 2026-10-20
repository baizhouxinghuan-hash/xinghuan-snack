package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.storage.entity.PageEntity;

import javax.swing.*;
import java.awt.*;

/**
 * ⭐ 页面内容展示组件
 *
 * 功能：
 * - 展示选中页面的详细内容
 * - 显示标题、URL、正文
 */
public class ContentView extends JPanel {

    private final JLabel titleLabel;
    private final JLabel urlLabel;
    private final JTextArea contentArea;

    public ContentView() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("页面内容"));

        // 顶部信息面板
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        titleLabel = new JLabel("请选择一个页面查看内容");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 33, 33));

        urlLabel = new JLabel("");
        urlLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        urlLabel.setForeground(Color.GRAY);

        infoPanel.add(titleLabel);
        infoPanel.add(urlLabel);

        // 内容区域
        contentArea = new JTextArea();
        contentArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 显示页面内容
     */
    public void showPage(PageEntity page) {
        if (page == null) {
            clear();
            return;
        }

        titleLabel.setText(page.getTitle() != null ? page.getTitle() : "无标题");
        urlLabel.setText(page.getUrl() != null ? page.getUrl() : "");
        contentArea.setText(page.getContent() != null ? page.getContent() : "无内容");
        contentArea.setCaretPosition(0);
    }

    /**
     * 清空显示
     */
    public void clear() {
        titleLabel.setText("请选择一个页面查看内容");
        urlLabel.setText("");
        contentArea.setText("");
    }
}