package com.velocityrush.missions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MissionManager maintains active missions, tracks progress, and grants rewards.
 * 
 * Demonstrates:
 * - COMPOSITION: Has-a List of Mission objects.
 * - POLYMORPHISM: Loops over List<Mission> and calls mission.evaluate() uniformly.
 */
public class MissionManager {

    private final List<Mission> missions = new ArrayList<>();

    public MissionManager() {
        initMissions();
    }

    private void initMissions() {
        missions.add(new DistanceMission("m1", "Highway Pioneer", "Survive past 800 meters", 800, 300));
        missions.add(new OvertakeMission("m2", "Traffic Phantom", "Overtake 10 vehicles without crashing", 10, 450));
        missions.add(new ScoreMission("m3", "Velocity King", "Reach a race score of 4,000 points", 4000, 600));
        missions.add(new DistanceMission("m4", "Iron Will", "Survive past 2,000 meters", 2000, 800));
    }

    public int evaluateAll(int score, double distance, int overtakes) {
        int earnedCredits = 0;
        for (Mission m : missions) {
            if (!m.isCompleted() && m.evaluate(score, distance, overtakes)) {
                earnedCredits += m.getRewardCredits();
            }
        }
        return earnedCredits;
    }

    public List<Mission> getMissions() {
        return Collections.unmodifiableList(missions);
    }
}
