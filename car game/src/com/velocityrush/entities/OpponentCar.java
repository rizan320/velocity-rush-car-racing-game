package com.velocityrush.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * OpponentCar represents AI-controlled traffic vehicles with intelligent pack behavior.
 * 
 * Features:
 * - Intelligent lane spacing: AI vehicles slow down to match the speed of cars ahead,
 *   preventing premature self-destruction and maintaining dense, thrilling highway congestion!
 * - Instantly blasts into fire and debris if it hits a road obstacle or the player.
 */
public class OpponentCar extends Vehicle {

    private int targetLane;
    private double laneChangeProgress = 1.0;
    private double startLaneX;
    private double targetLaneX;
    private boolean overtaken = false;
    private boolean isBlasted = false;

    public OpponentCar(double x, double y, int targetLane, int modelIndex, String modelName, BufferedImage sprite) {
        super(x, y, modelIndex, modelName, sprite);
        this.targetLane = targetLane;
        this.targetLaneX = x;
        this.startLaneX = x;

        // Base traffic cruise speed
        double baseSpeed = 120.0 + (modelIndex % 4) * 18.0;
        setSpeed(baseSpeed);
        setMaxSpeed(baseSpeed + 25.0);
    }

    /**
     * Updates position and checks distance to cars ahead in the same lane
     * to maintain dense, realistic traffic packs.
     */
    public void updateRelativeWithTrafficAI(double playerSpeed, List<OpponentCar> allOpponents, double deltaTime) {
        if (isBlasted) return;

        // 1. Traffic Following Distance: Check if another car is directly ahead in our lane
        double minDistanceAhead = 999.0;
        OpponentCar carAhead = null;

        for (OpponentCar other : allOpponents) {
            if (other != this && !other.isBlasted() && other.targetLane == this.targetLane) {
                double dist = other.getY() - this.getY(); // other is ahead if dist < 0 in road frame, or y is smaller
                // In our screen coords, cars higher up have smaller Y
                double distanceToLead = this.getY() - other.getY();
                if (distanceToLead > 0 && distanceToLead < minDistanceAhead) {
                    minDistanceAhead = distanceToLead;
                    carAhead = other;
                }
            }
        }

        // If a car is close ahead (within 90 px), match its speed or back off slightly
        if (carAhead != null && minDistanceAhead < 95.0) {
            setSpeed(Math.max(60.0, carAhead.getSpeed() - 15.0));
        }

        // 2. Relative vertical motion down screen
        double relativeSpeed = playerSpeed - getSpeed();
        setY(getY() + relativeSpeed * deltaTime);

        // 3. Lane transitions
        if (laneChangeProgress < 1.0) {
            laneChangeProgress += 1.6 * deltaTime;
            if (laneChangeProgress >= 1.0) {
                laneChangeProgress = 1.0;
                setX(targetLaneX);
                setTiltAngle(0.0);
            } else {
                double newX = startLaneX + (targetLaneX - startLaneX) * laneChangeProgress;
                setX(newX);
                double dir = targetLaneX > startLaneX ? 0.05 : -0.05;
                setTiltAngle(dir);
            }
        }
    }

    @Override
    public void update(double deltaTime) {}

    public void triggerBlast() {
        this.isBlasted = true;
        this.takeDamage(100);
        setHealth(0);
        setSpeed(0);
    }

    @Override
    public void render(Graphics2D g2d) {
        if (!isBlasted) {
            super.render(g2d);
        }
    }

    public boolean isBlasted() { return isBlasted; }
    public boolean isOvertaken() { return overtaken; }
    public void setOvertaken(boolean overtaken) { this.overtaken = overtaken; }
    public int getTargetLane() { return targetLane; }
}
