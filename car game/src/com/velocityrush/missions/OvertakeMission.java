package com.velocityrush.missions;

/**
 * OvertakeMission evaluates if player overtook enough traffic cars.
 */
public class OvertakeMission extends Mission {

    public OvertakeMission(String id, String title, String description, int targetOvertakes, int rewardCredits) {
        super(id, title, description, targetOvertakes, rewardCredits);
    }

    @Override
    public boolean evaluate(int score, double distance, int overtakes) {
        if (!isCompleted() && overtakes >= getTargetValue()) {
            setCompleted(true);
            return true;
        }
        return false;
    }
}
