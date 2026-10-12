package com.velocityrush.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Explosion represents a high-impact blast when a vehicle collides with anything.
 * 
 * Features:
 * - Expanding shockwave blast ring.
 * - Multi-layered fiery explosion fireball cloud.
 * - High-speed vehicle shrapnel and wheel debris flying in all directions.
 * - Dense smoke plumes dissipating into the air.
 * - Charred asphalt scorch mark left on the road.
 * 
 * OOP Principles Applied:
 * - ENCAPSULATION: Internal particle lifecycles and shockwave radii are fully encapsulated.
 * - COMPOSITION: Contains collections of debris pieces and smoke particles.
 */
public class Explosion {

    private double x;
    private double y;
    private double age = 0.0;
    private static final double MAX_DURATION = 1.4; // seconds
    private final double maxRadius = 75.0;

    // Debris piece
    private static class Debris {
        double x, y;
        double vx, vy;
        double rotation;
        double vRot;
        int size;
        Color color;

        Debris(double x, double y, double vx, double vy, int size, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
            this.rotation = Math.random() * Math.PI * 2;
            this.vRot = (Math.random() - 0.5) * 15.0;
        }

        void update(double deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            vx *= 0.94; // Air drag
            vy *= 0.94;
            rotation += vRot * deltaTime;
        }
    }

    // Fire & Smoke puffs
    private static class Cloud {
        double x, y;
        double vx, vy;
        double radius;
        double maxRadius;
        Color color;
        double life;
        double maxLife;

        Cloud(double x, double y, double vx, double vy, double maxR, Color color, double maxLife) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = 10;
            this.maxRadius = maxR;
            this.color = color;
            this.life = maxLife;
            this.maxLife = maxLife;
        }

        void update(double deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            radius += (maxRadius - radius) * 6.0 * deltaTime;
            life -= deltaTime;
        }
    }

    private final List<Debris> debrisList = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
        Random rng = new Random();

        // 1. Generate Fireball clouds
        Color[] fireColors = {
            new Color(255, 240, 150), // White-yellow core
            new Color(255, 140, 20),  // Bright orange
            new Color(230, 45, 10),   // Crimson fire
            new Color(70, 70, 75)     // Dark smoke
        };

        for (int i = 0; i < 22; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double speed = 40 + rng.nextDouble() * 120;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            Color c = fireColors[rng.nextInt(fireColors.length)];
            clouds.add(new Cloud(x, y, vx, vy, 24 + rng.nextDouble() * 26, c, 0.7 + rng.nextDouble() * 0.6));
        }

        // 2. Generate flying car shrapnel & metal fragments
        Color[] shrapnelColors = {
            new Color(30, 30, 32),    // Dark chassis metal
            new Color(220, 30, 30),   // Car body paint
            new Color(180, 185, 195), // Chrome/silver trim
            new Color(255, 190, 20)   // Burning fragment
        };

        for (int i = 0; i < 18; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double speed = 120 + rng.nextDouble() * 260;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            int size = 4 + rng.nextInt(6);
            debrisList.add(new Debris(x, y, vx, vy, size, shrapnelColors[rng.nextInt(shrapnelColors.length)]));
        }
    }

    public void update(double roadSpeed, double deltaTime) {
        age += deltaTime;

        // Scorch mark and explosion center drift down with the road
        y += roadSpeed * deltaTime;

        for (Cloud c : clouds) {
            c.y += roadSpeed * deltaTime;
            c.update(deltaTime);
        }

        for (Debris d : debrisList) {
            d.y += roadSpeed * deltaTime;
            d.update(deltaTime);
        }
    }

    public void render(Graphics2D g2d) {
        int cx = (int) x;
        int cy = (int) y;

        // 1. Charred Road Scorch Mark (permanent burn spot on tarmac)
        g2d.setColor(new Color(15, 16, 20, 180));
        g2d.fillOval(cx - 36, cy - 24, 72, 48);
        g2d.setColor(new Color(25, 26, 32, 120));
        g2d.fillOval(cx - 48, cy - 32, 96, 64);

        // 2. Expanding Shockwave Blast Ring
        if (age < 0.45) {
            float shockProgress = (float) (age / 0.45);
            int ringRadius = (int) (shockProgress * maxRadius);
            int alpha = (int) ((1.0f - shockProgress) * 230);
            g2d.setColor(new Color(255, 210, 100, Math.max(0, alpha)));
            g2d.setStroke(new BasicStroke(4.0f * (1.0f - shockProgress) + 1.0f));
            g2d.drawOval(cx - ringRadius, cy - ringRadius, ringRadius * 2, ringRadius * 2);
        }

        // 3. Fire and Smoke Clouds
        for (Cloud c : clouds) {
            if (c.life > 0) {
                float alphaRatio = (float) Math.max(0.0, Math.min(1.0, c.life / c.maxLife));
                int alpha = (int) (alphaRatio * 210);
                Color renderCol = new Color(c.color.getRed(), c.color.getGreen(), c.color.getBlue(), alpha);
                g2d.setColor(renderCol);
                int r = (int) c.radius;
                g2d.fillOval((int) c.x - r, (int) c.y - r, r * 2, r * 2);
            }
        }

        // 4. Flying Shrapnel Pieces
        for (Debris d : debrisList) {
            g2d.setColor(d.color);
            g2d.fillRect((int) d.x, (int) d.y, d.size, d.size);
        }
    }

    public boolean isDead() {
        return age >= MAX_DURATION;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
