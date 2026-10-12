package com.velocityrush.missions;

/**
 * ScoreMission evaluates if player achieved a target score.
 */
public class ScoreMission extends Mission {

    public ScoreMission(String id, String title, String description, int targetScore, int rewardCredits) {
        super(id, title, description, targetScore, rewardCredits);
    }

    @Override
    public boolean evaluate(int score, double distance, int overtakes) {
        if (!isCompleted() && score >= getTargetValue()) {
            setCompleted(true);
            return true;
        }
        return false;
    }
}
