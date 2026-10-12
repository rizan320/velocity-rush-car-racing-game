package com.velocityrush.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Vehicle is the abstract base class for all motorized entities in Velocity Rush.
 * 
 * Features:
 * - Natural 50x100 proportions matching the original template perfectly.
 * - Multi-layered ambient occlusion and tire contact shadows that seamlessly blend
 *   the vehicle with the asphalt road surface (no paper-cutout appearance).
 */
public abstract class Vehicle {

    private double x;
    private double y;
    // Restored natural 50x100 proportions from original design
    private int width = 50;
    private int height = 100;

    private double speed = 0.0;
    private double maxSpeed = 260.0;
    private double acceleration = 140.0;
    private double brakingPower = 280.0;
    private double handling = 80.0; // Reduced sensitivity for calm, precision control
    private double lateralVelocity = 0.0;

    private int health = 100;
    private int maxHealth = 100;

    private int carModelIndex = 0;
    private String modelName = "Racer";
    private BufferedImage sprite;
    private double tiltAngle = 0.0;

    public Vehicle(double x, double y, int modelIndex, String modelName, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.carModelIndex = modelIndex;
        this.modelName = modelName;
        this.sprite = sprite;
    }

    public abstract void update(double deltaTime);

    public void render(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();

        int drawX = (int) x;
        int drawY = (int) y;

        // 1. Multi-Layer Ground Contact Shadows (Seamlessly blends car with asphalt)
        // Broad soft ambient occlusion shadow
        g2d.setColor(new Color(15, 18, 24, 70));
        g2d.fillRoundRect(drawX + 3, drawY + 8, width, height, 16, 16);

        // Core chassis shadow
        g2d.setColor(new Color(8, 10, 14, 110));
        g2d.fillRoundRect(drawX + 5, drawY + 12, width - 4, height - 10, 12, 12);

        // 4 Wheel Tire Contact Patches (Anchors the car firmly to the ground)
        g2d.setColor(new Color(4, 5, 8, 150));
        g2d.fillRoundRect(drawX + 3, drawY + 20, 6, 14, 3, 3);          // Front-Left
        g2d.fillRoundRect(drawX + width - 9, drawY + 20, 6, 14, 3, 3);  // Front-Right
        g2d.fillRoundRect(drawX + 3, drawY + 68, 6, 16, 3, 3);          // Rear-Left
        g2d.fillRoundRect(drawX + width - 9, drawY + 68, 6, 16, 3, 3);  // Rear-Right

        // 2. Apply steering tilt rotation around vehicle center
        g2d.translate(drawX + width / 2.0, drawY + height / 2.0);
        g2d.rotate(tiltAngle);

        // Enable high-quality bilinear interpolation for clean, smooth vehicle graphics
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. Render Car Body
        if (sprite != null) {
            g2d.drawImage(sprite, -width / 2, -height / 2, width, height, null);
        } else {
            g2d.setColor(new Color(230, 30, 30));
            g2d.fillRoundRect(-width / 2, -height / 2, width, height, 12, 12);
        }

        g2d.setTransform(oldTransform);
    }

    public Rectangle2D.Double getBounds() {
        int paddingX = 6;
        int paddingY = 8;
        return new Rectangle2D.Double(x + paddingX, y + paddingY, width - paddingX * 2, height - paddingY * 2);
    }

    public void takeDamage(int amount) {
        setHealth(this.health - amount);
    }

    // Encapsulation
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) {
        this.speed = Math.max(0.0, Math.min(speed, maxSpeed));
    }

    public double getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = Math.max(50.0, maxSpeed);
    }

    public double getAcceleration() { return acceleration; }
    public void setAcceleration(double acceleration) {
        this.acceleration = Math.max(10.0, acceleration);
    }

    public double getBrakingPower() { return brakingPower; }
    public void setBrakingPower(double brakingPower) {
        this.brakingPower = Math.max(10.0, brakingPower);
    }

    public double getHandling() { return handling; }
    public void setHandling(double handling) {
        this.handling = Math.max(25.0, handling);
    }

    public double getLateralVelocity() { return lateralVelocity; }
    public void setLateralVelocity(double lateralVelocity) {
        this.lateralVelocity = lateralVelocity;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
    }

    public int getCarModelIndex() { return carModelIndex; }
    public void setCarModelIndex(int index) { this.carModelIndex = index; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public BufferedImage getSprite() { return sprite; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }

    public double getTiltAngle() { return tiltAngle; }
    public void setTiltAngle(double tiltAngle) { this.tiltAngle = tiltAngle; }

    public boolean isDestroyed() { return health <= 0; }
}
