package com.velocityrush.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * SaveManager handles local file persistence for player progression,
 * high scores, best distance, and credits.
 * 
 * Demonstrates:
 * - EXCEPTION HANDLING: Try-with-resources prevents memory/resource leaks
 *   and handles missing/corrupted save files gracefully.
 * - ENCAPSULATION: Save data is guarded behind validated accessors.
 */
public class SaveManager {

    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE = "saves/records.dat";

    private int highScore = 0;
    private double bestDistance = 0.0;
    private int totalOvertakes = 0;
    private int credits = 500;

    public SaveManager() {
        loadData();
    }

    public void loadData() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return; // Fresh start with defaults
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String val = parts[1].trim();

                    switch (key) {
                        case "highScore":
                            this.highScore = Math.max(0, Integer.parseInt(val));
                            break;
                        case "bestDistance":
                            this.bestDistance = Math.max(0.0, Double.parseDouble(val));
                            break;
                        case "totalOvertakes":
                            this.totalOvertakes = Math.max(0, Integer.parseInt(val));
                            break;
                        case "credits":
                            this.credits = Math.max(0, Integer.parseInt(val));
                            break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Safe fallback if save file is corrupt or unreadable
            System.err.println("[SaveManager] Warning: Could not parse save file, using defaults.");
        }
    }

    public void saveData() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(SAVE_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("highScore=" + highScore + "\n");
            writer.write("bestDistance=" + bestDistance + "\n");
            writer.write("totalOvertakes=" + totalOvertakes + "\n");
            writer.write("credits=" + credits + "\n");
        } catch (IOException e) {
            System.err.println("[SaveManager] Error saving records to file: " + e.getMessage());
        }
    }

    public boolean recordRace(int raceScore, double distance, int overtakes) {
        boolean isNewRecord = false;
        if (raceScore > highScore) {
            this.highScore = raceScore;
            isNewRecord = true;
        }
        if (distance > bestDistance) {
            this.bestDistance = distance;
        }
        this.totalOvertakes += overtakes;
        this.credits += (raceScore / 10);
        saveData();
        return isNewRecord;
    }

    public int getHighScore() { return highScore; }
    public double getBestDistance() { return bestDistance; }
    public int getTotalOvertakes() { return totalOvertakes; }
    public int getCredits() { return credits; }
}
