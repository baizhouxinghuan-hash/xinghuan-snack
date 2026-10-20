package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.ui.UIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * ⭐ 搜索框组件
 *
 * 功能：
 * - 用户输入搜索关键词
 * - 支持回车键触发搜索
 * - 支持搜索按钮点击
 */
public class SearchBar extends JPanel {

    private final JTextField searchField;
    private final JButton searchButton;
    private final UIController uiController;

    public SearchBar(UIController uiController) {
        this.uiController = uiController;

        setLayout(new BorderLayout(5, 0));
        setBorder(BorderFactory.createTitledBorder("搜索"));

        // 搜索输入框
        searchField = new JTextField();
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setToolTipText("输入关键词搜索已抓取的网页内容");

        // 搜索按钮
        searchButton = new JButton("🔍 搜索");
        searchButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        searchButton.setBackground(new Color(66, 133, 244));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        // 回车键触发搜索
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSearch();
                }
            }
        });

        // 按钮点击触发搜索
        searchButton.addActionListener(e -> doSearch());

        add(searchField, BorderLayout.CENTER);
        add(searchButton, BorderLayout.EAST);

        setPreferredSize(new Dimension(0, 45));
    }

    /**
     * 执行搜索
     */
    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            uiController.search(keyword);
        }
    }

    /**
     * 获取搜索关键词
     */
    public String getKeyword() {
        return searchField.getText().trim();
    }

    /**
     * 清空搜索框
     */
    public void clear() {
        searchField.setText("");
    }
}