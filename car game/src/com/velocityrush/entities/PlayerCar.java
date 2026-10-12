package com.velocityrush.entities;

import com.velocityrush.input.InputHandler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * PlayerCar represents the user's high-speed sports vehicle.
 * 
 * Features:
 * - Highly reduced lateral steering sensitivity (calibrated to 48.0 handling with 5.0 damping)
 *   giving the player ultra-calm, leisurely, and micro-precision control between narrow gaps.
 * - Restored natural proportions and daytime running headlight cones for realistic ground blending.
 * - Explosive 10-Second 2X Speed Boost.
 */
public class PlayerCar extends Vehicle {

    private boolean nitroActive = false;
    private double nitroFuel = 100.0;
    private static final double MAX_NITRO = 100.0;
    private double targetCruiseSpeed = 190.0;

    // 10-Second 2X Speed Boost
    private double superBoostTimer = 0.0;
    private static final double BOOST_DURATION = 10.0;
    private boolean isBlasted = false;

    public PlayerCar(double x, double y, int modelIndex, String modelName, BufferedImage sprite) {
        super(x, y, modelIndex, modelName, sprite);
        setMaxSpeed(450.0);
        setAcceleration(220.0);
        setBrakingPower(360.0);
        // Ultra-calm lateral handling for precise, gentle, micro-adjustments
        setHandling(48.0);
        setSpeed(140.0);
    }

    public void activateSuperBoost() {
        this.superBoostTimer = BOOST_DURATION;
        setSpeed(Math.max(getSpeed() * 1.65, 340.0));
    }

    public boolean isSuperBoostActive() {
        return superBoostTimer > 0.0;
    }

    public double getSuperBoostTimer() {
        return Math.max(0.0, superBoostTimer);
    }

    public void setDifficultyCruiseSpeed(double baseSpeed, double bonusSpeed) {
        double target = baseSpeed + bonusSpeed;
        if (isSuperBoostActive()) {
            target = Math.max(target * 2.0, 360.0);
        }
        this.targetCruiseSpeed = Math.min(getMaxSpeed(), target);
    }

    public void handleInput(InputHandler input, double deltaTime) {
        if (isBlasted) return;

        if (superBoostTimer > 0.0) {
            superBoostTimer -= deltaTime;
        }

        // 1. Cruise / Throttle / Braking
        if (input.down) {
            nitroActive = false;
            setSpeed(getSpeed() - getBrakingPower() * deltaTime);
        } else {
            double current = getSpeed();
            double target = targetCruiseSpeed;

            if (input.space && nitroFuel > 5.0) {
                nitroActive = true;
                target += 80.0;
                nitroFuel = Math.max(0, nitroFuel - 45.0 * deltaTime);
                setSpeed(current + getAcceleration() * 2.2 * deltaTime);
            } else {
                nitroActive = false;
                if (current < target) {
                    double accelRate = isSuperBoostActive() ? getAcceleration() * 2.8 : getAcceleration();
                    setSpeed(current + accelRate * deltaTime);
                } else if (current > target + 5) {
                    setSpeed(current - 30.0 * deltaTime);
                }
            }
        }

        if (!nitroActive && nitroFuel < MAX_NITRO) {
            nitroFuel = Math.min(MAX_NITRO, nitroFuel + 14.0 * deltaTime);
        }

        // 2. Ultra-Gentle, Calm Steering (Handling = 48.0)
        double targetLateralVel = 0.0;
        double targetTilt = 0.0;

        if (input.left) {
            targetLateralVel -= getHandling();
            targetTilt = -0.025; // Barely perceptible, realistic roll
        }
        if (input.right) {
            targetLateralVel += getHandling();
            targetTilt = 0.025;
        }

        // Calm damping (5.0 factor provides smooth, leisurely gliding across lanes)
        double currentLat = getLateralVelocity();
        setLateralVelocity(currentLat + (targetLateralVel - currentLat) * 5.0 * deltaTime);
        setTiltAngle(getTiltAngle() + (targetTilt - getTiltAngle()) * 4.5 * deltaTime);
    }

    @Override
    public void update(double deltaTime) {
        if (!isBlasted) {
            setX(getX() + getLateralVelocity() * deltaTime);
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (isBlasted) return;

        renderHeadlightGlow(g2d);

        if (isSuperBoostActive()) {
            renderSuperBoostAura(g2d);
        }

        super.render(g2d);

        if (nitroActive || isSuperBoostActive()) {
            renderNitroFlames(g2d);
        }
    }

    private void renderHeadlightGlow(Graphics2D g2d) {
        int leftX = (int) getX() + 10;
        int rightX = (int) getX() + getWidth() - 10;
        int lightY = (int) getY() + 8;

        Polygon leftCone = new Polygon(
            new int[]{ leftX, leftX - 40, leftX + 20 },
            new int[]{ lightY, lightY - 140, lightY - 140 },
            3
        );
        Polygon rightCone = new Polygon(
            new int[]{ rightX, rightX - 20, rightX + 40 },
            new int[]{ lightY, lightY - 140, lightY - 140 },
            3
        );

        g2d.setColor(new Color(255, 255, 220, 25));
        g2d.fillPolygon(leftCone);
        g2d.fillPolygon(rightCone);
    }

    private void renderSuperBoostAura(Graphics2D g2d) {
        int drawX = (int) getX();
        int drawY = (int) getY();

        g2d.setColor(new Color(0, 230, 255, 75));
        g2d.fillRoundRect(drawX - 8, drawY - 12, getWidth() + 16, getHeight() + 24, 20, 20);

        g2d.setColor(new Color(180, 248, 255, 110));
        g2d.fillRoundRect(drawX - 4, drawY - 6, getWidth() + 8, getHeight() + 12, 16, 16);
    }

    private void renderNitroFlames(Graphics2D g2d) {
        int rearY = (int) getY() + getHeight();
        int leftExhaustX = (int) getX() + 10;
        int rightExhaustX = (int) getX() + getWidth() - 17;

        int flameLength = isSuperBoostActive() ? 32 : 18;
        Color flameColor = isSuperBoostActive() ? new Color(0, 250, 255) : new Color(0, 190, 255);

        g2d.setColor(flameColor);
        g2d.fillOval(leftExhaustX, rearY - 2, 7, flameLength);
        g2d.fillOval(rightExhaustX, rearY - 2, 7, flameLength);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(leftExhaustX + 2, rearY - 2, 3, flameLength / 2);
        g2d.fillOval(rightExhaustX + 2, rearY - 2, 3, flameLength / 2);
    }

    public void triggerBlast() {
        this.isBlasted = true;
        setSpeed(0);
        setHealth(0);
    }

    public boolean isBlasted() { return isBlasted; }
    public double getNitroFuel() { return nitroFuel; }
}
