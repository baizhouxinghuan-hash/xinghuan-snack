package xiaobaisnack.crawler.ui.components;

import java.awt.*;
import javax.swing.*;

/**
 * ⭐ 日志面板
 *
 * 功能：
 * - 显示运行日志
 * - 支持清空
 */
public class LogPanel extends JPanel {

    private JTextArea logArea;

    public LogPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("运行日志"));
        setPreferredSize(new Dimension(0, 150));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 追加日志
     */
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // 自动滚动到底部
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * 清空日志
     */
    public void clear() {
        logArea.setText("");
    }
}