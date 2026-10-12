package com.velocityrush.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * RoadObstacle represents stationary track hazards (e.g. Traffic Barrels, Cones)
 * that the player must dodge on the 3-lane highway.
 */
public class RoadObstacle {

    public enum Type { BARREL, CONE }

    private double x;
    private double y;
    private final int width = 36;
    private final int height = 36;
    private final int lane;
    private final Type type;
    private boolean hit = false;

    public RoadObstacle(double x, double y, int lane, Type type) {
        this.x = x;
        this.y = y;
        this.lane = lane;
        this.type = type;
    }

    public void updateRelative(double playerSpeed, double deltaTime) {
        // Stationary on the road, so it scrolls down at the player's full road speed
        y += playerSpeed * deltaTime;
    }

    public void render(Graphics2D g2d) {
        int drawX = (int) x;
        int drawY = (int) y;

        // Drop shadow
        g2d.setColor(new Color(20, 25, 30, 90));
        g2d.fillOval(drawX + 3, drawY + 8, width, height);

        if (type == Type.BARREL) {
            // High-visibility orange safety barrel with white reflective stripe
            g2d.setColor(new Color(245, 90, 15));
            g2d.fillRoundRect(drawX, drawY, width, height, 10, 10);

            // White reflective band
            g2d.setColor(new Color(250, 250, 255));
            g2d.fillRect(drawX, drawY + 12, width, 12);

            // Dark rim outline
            g2d.setColor(new Color(180, 50, 10));
            g2d.drawRoundRect(drawX, drawY, width, height, 10, 10);
        } else {
            // Traffic Cone
            g2d.setColor(new Color(245, 110, 15));
            int[] cx = { drawX + width / 2, drawX + width, drawX };
            int[] cy = { drawY, drawY + height, drawY + height };
            g2d.fillPolygon(cx, cy, 3);

            // White stripe
            g2d.setColor(Color.WHITE);
            int[] sx = { drawX + width / 2, drawX + width - 8, drawX + 8 };
            int[] sy = { drawY + 12, drawY + 24, drawY + 24 };
            g2d.fillPolygon(sx, sy, 3);
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x + 4, y + 4, width - 8, height - 8);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getLane() { return lane; }
    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }
}
