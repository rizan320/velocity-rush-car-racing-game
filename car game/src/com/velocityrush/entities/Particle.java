package com.velocityrush.entities;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Particle represents visual effects such as collision sparks,
 * tire smoke, or skid marks.
 */
public class Particle {

    public enum Type { SPARK, SMOKE, SKID }

    private double x;
    private double y;
    private double vx;
    private double vy;
    private double life;
    private final double maxLife;
    private final Color baseColor;
    private final int size;
    private final Type type;

    public Particle(double x, double y, double vx, double vy, double maxLife, Color baseColor, int size, Type type) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = maxLife;
        this.maxLife = maxLife;
        this.baseColor = baseColor;
        this.size = size;
        this.type = type;
    }

    public void update(double roadSpeed, double deltaTime) {
        x += vx * deltaTime;
        // In relative motion, particles drift down with the road
        y += (vy + roadSpeed) * deltaTime;
        life -= deltaTime;
    }

    public void render(Graphics2D g2d) {
        if (life <= 0) return;
        float alpha = (float) Math.max(0.0, Math.min(1.0, life / maxLife));
        int alphaInt = (int) (alpha * 255);

        Color renderColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alphaInt);
        g2d.setColor(renderColor);

        if (type == Type.SPARK) {
            g2d.fillRect((int) x, (int) y, size, size);
        } else if (type == Type.SMOKE) {
            g2d.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        } else if (type == Type.SKID) {
            g2d.fillRect((int) x, (int) y, size, size * 2);
        }
    }

    public boolean isDead() {
        return life <= 0;
    }
}
