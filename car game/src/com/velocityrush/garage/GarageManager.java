package com.velocityrush.garage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GarageManager manages the catalog of 12 playable cars, player selection,
 * upgrades, and credits.
 * 
 * Apex Flame (Car #1, Red) is enhanced as the flagship hypercar with superior stats.
 */
public class GarageManager {

    private final List<CarProfile> availableCars = new ArrayList<>();
    private int selectedCarIndex = 0;
    private int credits = 500;

    public GarageManager() {
        initCarCatalog();
    }

    private void initCarCatalog() {
        // Car #1 (Red Flagship Hypercar): Max performance and aggressive attributes
        availableCars.add(new CarProfile(0, "Apex Flame V10", "Flagship Hypercar", 285.0, 210.0, 370.0, 100, 0, true));

        // Remaining 11 Cars
        availableCars.add(new CarProfile(1, "Solar Strike", "Hypercar", 255.0, 185.0, 310.0, 95, 250, true));
        availableCars.add(new CarProfile(2, "Emerald Viper", "Hypercar", 250.0, 180.0, 330.0, 100, 300, true));
        availableCars.add(new CarProfile(3, "Phantom Violet", "Hypercar", 270.0, 200.0, 340.0, 90, 500, true));

        // Row 2: Muscle / Sports Coupe
        availableCars.add(new CarProfile(4, "Cobalt Muscle", "Muscle V8", 240.0, 175.0, 290.0, 115, 200, true));
        availableCars.add(new CarProfile(5, "Crimson Fury", "Muscle V8", 242.0, 178.0, 295.0, 110, 250, true));
        availableCars.add(new CarProfile(6, "Midnight Shadow", "Muscle V8", 245.0, 180.0, 300.0, 120, 350, true));
        availableCars.add(new CarProfile(7, "Amber Drift", "Muscle V8", 238.0, 170.0, 315.0, 110, 200, true));

        // Row 3: Grand Tourers (GT)
        availableCars.add(new CarProfile(8, "QuickSilver GT", "Grand Tourer", 250.0, 175.0, 310.0, 125, 300, true));
        availableCars.add(new CarProfile(9, "Midas Luxury", "Grand Tourer", 252.0, 178.0, 315.0, 130, 400, true));
        availableCars.add(new CarProfile(10, "Obsidian GT", "Grand Tourer", 258.0, 182.0, 320.0, 125, 450, true));
        availableCars.add(new CarProfile(11, "Pearl Ghost", "Grand Tourer", 262.0, 188.0, 325.0, 120, 500, true));
    }

    public List<CarProfile> getAvailableCars() {
        return Collections.unmodifiableList(availableCars);
    }

    public CarProfile getSelectedCar() {
        return availableCars.get(selectedCarIndex);
    }

    public void selectNextCar() {
        selectedCarIndex = (selectedCarIndex + 1) % availableCars.size();
    }

    public void selectPreviousCar() {
        selectedCarIndex = (selectedCarIndex - 1 + availableCars.size()) % availableCars.size();
    }

    public int getSelectedCarIndex() {
        return selectedCarIndex;
    }

    public void setSelectedCarIndex(int index) {
        if (index >= 0 && index < availableCars.size()) {
            this.selectedCarIndex = index;
        }
    }

    public int getCredits() {
        return credits;
    }

    public void addCredits(int amount) {
        if (amount > 0) {
            this.credits += amount;
        }
    }
}
