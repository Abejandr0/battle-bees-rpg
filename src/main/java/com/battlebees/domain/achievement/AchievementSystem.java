package com.battlebees.domain.achievement;

import com.battlebees.domain.event.*;
import java.util.HashMap;
import java.util.Map;

public class AchievementSystem implements EventSubscriber {
    private final Map<String, Achievement> achievements;
    private final EventPublisher publisher;

    // Tracking stats
    private int criticalHits = 0;
    private int totalWins = 0;
    private int consecutiveWinsWithBuffsOnly = 0;
    private boolean wonWithWarrior = false;
    private boolean wonWithMage = false;
    private boolean wonWithAssassin = false;

    public AchievementSystem(EventPublisher publisher) {
        this.publisher = publisher;
        this.achievements = new HashMap<>();
        initAchievements();
    }

    private void initAchievements() {
        addAchievement("first_blood", "First Blood");
        addAchievement("critical_master", "Critical Master");
        addAchievement("unbreakable", "Unbreakable");
        addAchievement("overkill", "Overkill");
        addAchievement("hive_mind", "Hive Mind");
        addAchievement("elite_soldier", "Elite Soldier");
        addAchievement("strategist", "Strategist");
    }

    private void addAchievement(String id, String name) {
        achievements.put(id, new Achievement(id, name));
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof CriticalHitEvent) {
            handleCriticalHit();
        } else if (event instanceof MatchWonEvent) {
            handleMatchWon((MatchWonEvent) event);
        }
    }

    private void handleCriticalHit() {
        criticalHits++;
        if (criticalHits >= 50) {
            unlock("critical_master");
        }
    }

    private void handleMatchWon(MatchWonEvent event) {
        totalWins++;
        
        if (totalWins == 1) {
            unlock("first_blood");
        }
        if (totalWins >= 100) {
            unlock("elite_soldier");
        }

        if (!event.isTookDamage()) {
            unlock("unbreakable");
        }

        if (event.getHighestDamageDealt() >= 200) { // Assuming 100 is base HP
            unlock("overkill");
        }

        if (event.getTotalBuffsUsed() > 0 && event.getHighestDamageDealt() < 50) {
            // Simplified logic: used buffs, but damage wasn't primarily from base attacks (simulation of 'only buffs')
            // To be precise with "only buffs", we just track if buffs were used
            consecutiveWinsWithBuffsOnly++;
            if (consecutiveWinsWithBuffsOnly >= 3) {
                unlock("hive_mind");
            }
        } else {
            consecutiveWinsWithBuffsOnly = 0;
        }

        String heroClass = event.getWinningHeroClass();
        if (heroClass.contains("Warrior")) wonWithWarrior = true;
        if (heroClass.contains("Mage")) wonWithMage = true;
        if (heroClass.contains("Assassin")) wonWithAssassin = true;

        if (wonWithWarrior && wonWithMage && wonWithAssassin) {
            unlock("strategist");
        }
    }

    private void unlock(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null && !achievement.isUnlocked()) {
            achievement.unlock();
            publisher.publish(new AchievementUnlockedEvent(achievement.getId(), achievement.getName()));
        }
    }
}
