package FreshKeeper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private int roundRadius;
    private Color borderColor;
    private int borderWidth;

    public RoundedPanel(int radius) {
        this.roundRadius = radius;
        this.borderColor = new Color(0, 212, 255);
        this.borderWidth = 1;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color borderColor, int borderWidth) {
        this.roundRadius = radius;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그리기
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, roundRadius, roundRadius);

        // 테두리 그리기
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderWidth));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, roundRadius, roundRadius);
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setRoundRadius(int radius) {
        this.roundRadius = radius;
        repaint();
    }
}
