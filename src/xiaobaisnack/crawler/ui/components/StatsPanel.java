package xiaobaisnack.crawler.ui.components;

import xiaobaisnack.crawler.api.StatsController;

import javax.swing.*;
import java.awt.*;

/**
 * ⭐ 统计面板
 *
 * 功能：
 * - 显示爬虫运行统计
 * - URL和页面计数
 */
public class StatsPanel extends JPanel {

    private JLabel totalUrlsLabel;
    private JLabel newUrlsLabel;
    private JLabel doneUrlsLabel;
    private JLabel failedUrlsLabel;
    private JLabel totalPagesLabel;

    public StatsPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("统计信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // 标题行
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("统计项"), gbc);
        gbc.gridx = 1;
        add(new JLabel("数值"), gbc);

        // 分隔线
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // URL总数
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("URL总数:"), gbc);
        gbc.gridx = 1;
        totalUrlsLabel = new JLabel("0");
        add(totalUrlsLabel, gbc);

        // 新URL
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("待处理:"), gbc);
        gbc.gridx = 1;
        newUrlsLabel = new JLabel("0");
        add(newUrlsLabel, gbc);

        // 已完成
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("已完成:"), gbc);
        gbc.gridx = 1;
        doneUrlsLabel = new JLabel("0");
        add(doneUrlsLabel, gbc);

        // 失败
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("失败:"), gbc);
        gbc.gridx = 1;
        failedUrlsLabel = new JLabel("0");
        add(failedUrlsLabel, gbc);

        // 分隔线
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // 页面总数
        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("页面总数:"), gbc);
        gbc.gridx = 1;
        totalPagesLabel = new JLabel("0");
        add(totalPagesLabel, gbc);

        // 填充空白
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JLabel(), gbc);
    }

    /**
     * 更新统计数据
     */
    public void updateStats(StatsController statsController) {
        try {
            StatsController.UrlStats urlStats = statsController.getUrlStats();
            StatsController.PageStats pageStats = statsController.getPageStats();

            totalUrlsLabel.setText(String.valueOf(urlStats.total));
            newUrlsLabel.setText(String.valueOf(urlStats.newCount));
            doneUrlsLabel.setText(String.valueOf(urlStats.done));
            failedUrlsLabel.setText(String.valueOf(urlStats.failed));
            totalPagesLabel.setText(String.valueOf(pageStats.total));
        } catch (Exception e) {
            // 忽略更新错误
        }
    }
}