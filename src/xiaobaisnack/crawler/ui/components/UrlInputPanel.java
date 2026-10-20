package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.ui.UIController;

import javax.swing.*;
import java.awt.*;

/**
 * ⭐ URL输入面板
 *
 * 功能：
 * - 输入种子URL
 * - 添加URL到队列
 */
public class UrlInputPanel extends JPanel {

    private final UIController uiController;
    private JTextField urlField;
    private JButton addButton;

    public UrlInputPanel(UIController uiController) {
        this.uiController = uiController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("URL管理"));

        // URL输入
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        urlField = new JTextField();
        urlField.setToolTipText("输入种子URL，如 https://www.example.com");
        inputPanel.add(new JLabel("种子URL:"), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);

        // 添加按钮
        addButton = new JButton("添加URL");
        addButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            if (!url.isEmpty()) {
                uiController.addUrl(url);
                urlField.setText("");
            }
        });

        add(inputPanel, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
    }

    /**
     * 获取输入的URL
     */
    public String getUrl() {
        return urlField.getText().trim();
    }
}