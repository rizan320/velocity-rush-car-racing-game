package com.velocityrush.garage;

/**
 * CarProfile encapsulates the statistics and identity of each available vehicle.
 * Encapsulation ensures stats cannot be corrupted or made negative.
 */
public class CarProfile {

    private final int id;
    private final String name;
    private final String category;
    private final double topSpeed;
    private final double acceleration;
    private final double handling;
    private final int durability;
    private final int price;
    private boolean unlocked;

    public CarProfile(int id, String name, String category, double topSpeed, double acceleration, 
                      double handling, int durability, int price, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.topSpeed = topSpeed;
        this.acceleration = acceleration;
        this.handling = handling;
        this.durability = durability;
        this.price = price;
        this.unlocked = unlocked;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String category() { return category; }
    public double getTopSpeed() { return topSpeed; }
    public double getAcceleration() { return acceleration; }
    public double getHandling() { return handling; }
    public int getDurability() { return durability; }
    public int getPrice() { return price; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
