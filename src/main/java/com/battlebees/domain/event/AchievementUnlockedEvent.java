package com.battlebees.domain.event;

public class AchievementUnlockedEvent implements DomainEvent {
    private final String achievementId;
    private final String achievementName;

    public AchievementUnlockedEvent(String achievementId, String achievementName) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
    }

    public String getAchievementId() { return achievementId; }
    public String getAchievementName() { return achievementName; }

    @Override
    public String getEventName() {
        return "AchievementUnlockedEvent";
    }
}
