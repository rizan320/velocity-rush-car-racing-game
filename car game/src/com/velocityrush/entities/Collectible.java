package com.velocityrush.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Collectible represents rare loot items spawned on the highway.
 * 
 * Demonstrates:
 * - ENCAPSULATION: Guarded state (collected, value, position).
 * - SPECIALIZATION: Handles animation and hitbox for speed boost canisters.
 */
public class Collectible {

    public enum Type { NITRO_BOOST, SCORE_BONUS }

    private double x;
    private double y;
    private final int width = 28;
    private final int height = 28;
    private final int lane;
    private final Type type;
    private boolean collected = false;
    private double pulseTimer = 0.0;

    public Collectible(double x, double y, int lane, Type type) {
        this.x = x;
        this.y = y;
        this.lane = lane;
        this.type = type;
    }

    public void updateRelative(double playerSpeed, double deltaTime) {
        // Scrolls down at road speed
        y += playerSpeed * deltaTime;
        pulseTimer += deltaTime * 5.0;
    }

    public void render(Graphics2D g2d) {
        if (collected) return;

        int drawX = (int) x;
        int drawY = (int) y;

        // Pulsing glow radius
        float pulse = (float) (Math.sin(pulseTimer) * 4.0);
        int glowSize = (int) (width + 8 + pulse);
        int glowOffset = (glowSize - width) / 2;

        if (type == Type.NITRO_BOOST) {
            // Cyan Nitrous Oxide Canister Loot
            // Outer neon glow
            g2d.setColor(new Color(0, 200, 255, 60));
            g2d.fillOval(drawX - glowOffset, drawY - glowOffset, glowSize, glowSize);

            // Metallic Canister Body
            g2d.setColor(new Color(0, 140, 230));
            g2d.fillRoundRect(drawX + 4, drawY + 2, 20, 24, 6, 6);

            // Chrome Cap
            g2d.setColor(new Color(230, 240, 255));
            g2d.fillRect(drawX + 9, drawY, 10, 5);

            // Glowing "N2O" label stripe
            g2d.setColor(new Color(150, 240, 255));
            g2d.fillRect(drawX + 6, drawY + 11, 16, 5);

            // White highlight
            g2d.setColor(Color.WHITE);
            g2d.fillRect(drawX + 7, drawY + 4, 3, 18);
        } else {
            // Gold Star / Coin Loot
            g2d.setColor(new Color(255, 215, 0, 70));
            g2d.fillOval(drawX - glowOffset, drawY - glowOffset, glowSize, glowSize);

            g2d.setColor(new Color(255, 190, 0));
            g2d.fillOval(drawX + 3, drawY + 3, 22, 22);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(drawX + 6, drawY + 6, 16, 16);
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x - 2, y - 2, width + 4, height + 4);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getLane() { return lane; }
    public Type getType() { return type; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}
