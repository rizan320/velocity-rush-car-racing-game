package com.velocityrush.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

/**
 * UIButton provides interactive clickable buttons with hover animations,
 * gradients, and click callbacks.
 */
public class UIButton {

    private int x;
    private int y;
    private int width;
    private int height;
    private String text;
    private Runnable action;

    private boolean hovered = false;
    private boolean pressed = false;

    private Color normalColor = new Color(30, 36, 48);
    private Color hoverColor = new Color(0, 160, 240);
    private Color textColor = Color.WHITE;
    private Font font = new Font("Segoe UI", Font.BOLD, 16);

    public UIButton(int x, int y, int width, int height, String text, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.action = action;
    }

    public void updateMouse(int mouseX, int mouseY) {
        hovered = contains(mouseX, mouseY);
    }

    public boolean handleMousePressed(int mouseX, int mouseY) {
        if (contains(mouseX, mouseY)) {
            pressed = true;
            return true;
        }
        return false;
    }

    public boolean handleMouseReleased(int mouseX, int mouseY) {
        if (pressed && contains(mouseX, mouseY)) {
            pressed = false;
            if (action != null) {
                action.run();
            }
            return true;
        }
        pressed = false;
        return false;
    }

    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public void render(Graphics2D g2d) {
        int drawY = pressed ? y + 2 : y;

        // Button background
        if (hovered) {
            GradientPaint gp = new GradientPaint(x, drawY, hoverColor, x, drawY + height, new Color(0, 110, 180));
            g2d.setPaint(gp);
        } else {
            GradientPaint gp = new GradientPaint(x, drawY, normalColor, x, drawY + height, new Color(20, 24, 32));
            g2d.setPaint(gp);
        }
        g2d.fillRoundRect(x, drawY, width, height, 14, 14);

        // Border outline
        if (hovered) {
            g2d.setColor(new Color(120, 220, 255));
            g2d.setStroke(new BasicStroke(2f));
        } else {
            g2d.setColor(new Color(80, 90, 110));
            g2d.setStroke(new BasicStroke(1.2f));
        }
        g2d.drawRoundRect(x, drawY, width, height, 14, 14);

        // Centered text
        g2d.setFont(font);
        g2d.setColor(hovered ? Color.WHITE : textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = drawY + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    // Setters
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setDimensions(int w, int h) { this.width = w; this.height = h; }
    public void setText(String text) { this.text = text; }
    public void setColors(Color normal, Color hover, Color text) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.textColor = text;
    }
    public void setFont(Font font) { this.font = font; }
    public void setAction(Runnable action) { this.action = action; }
}
