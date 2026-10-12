package com.velocityrush.missions;

/**
 * DistanceMission evaluates if player survived past a distance target.
 */
public class DistanceMission extends Mission {

    public DistanceMission(String id, String title, String description, int targetMeters, int rewardCredits) {
        super(id, title, description, targetMeters, rewardCredits);
    }

    @Override
    public boolean evaluate(int score, double distance, int overtakes) {
        if (!isCompleted() && distance >= getTargetValue()) {
            setCompleted(true);
            return true;
        }
        return false;
    }
}
