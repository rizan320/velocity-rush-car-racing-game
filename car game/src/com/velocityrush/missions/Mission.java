package com.velocityrush.missions;

/**
 * Mission is the abstract base class for gameplay challenges and objectives.
 * 
 * Demonstrates:
 * - ABSTRACTION: Common contract for progress evaluation.
 * - ENCAPSULATION: Reward and completion states are guarded.
 */
public abstract class Mission {

    private final String id;
    private final String title;
    private final String description;
    private final int targetValue;
    private final int rewardCredits;
    private boolean completed = false;

    public Mission(String id, String title, String description, int targetValue, int rewardCredits) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetValue = targetValue;
        this.rewardCredits = rewardCredits;
    }

    /**
     * Polymorphic evaluation overridden by specific mission types.
     */
    public abstract boolean evaluate(int score, double distance, int overtakes);

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTargetValue() { return targetValue; }
    public int getRewardCredits() { return rewardCredits; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
