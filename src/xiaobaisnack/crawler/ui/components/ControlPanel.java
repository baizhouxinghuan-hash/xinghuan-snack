package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.ui.UIController;

import javax.swing.*;
import java.awt.*;

/**
 * ⭐ 控制面板
 *
 * 功能：
 * - 开始/停止爬虫按钮
 * - 刷新按钮
 */
public class ControlPanel extends JPanel {

    private final UIController uiController;
    private JButton startButton;
    private JButton stopButton;
    private JButton refreshButton;

    public ControlPanel(UIController uiController) {
        this.uiController = uiController;
        initUI();
    }

    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createTitledBorder("控制面板"));

        startButton = new JButton("▶ 开始爬取");
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            uiController.startCrawl();
        });

        stopButton = new JButton("■ 停止爬取");
        stopButton.setBackground(new Color(244, 67, 54));
        stopButton.setForeground(Color.WHITE);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> {
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            uiController.stopCrawl();
        });

        refreshButton = new JButton("↻ 刷新");
        refreshButton.addActionListener(e -> uiController.refresh());

        add(startButton);
        add(stopButton);
        add(refreshButton);
    }
}