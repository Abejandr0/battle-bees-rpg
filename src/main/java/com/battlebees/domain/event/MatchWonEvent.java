package com.battlebees.domain.event;

public class MatchWonEvent implements DomainEvent {
    private final String winningHeroClass;
    private final boolean tookDamage;
    private final int totalBuffsUsed;
    private final int highestDamageDealt;

    public MatchWonEvent(String winningHeroClass, boolean tookDamage, int totalBuffsUsed, int highestDamageDealt) {
        this.winningHeroClass = winningHeroClass;
        this.tookDamage = tookDamage;
        this.totalBuffsUsed = totalBuffsUsed;
        this.highestDamageDealt = highestDamageDealt;
    }

    public String getWinningHeroClass() { return winningHeroClass; }
    public boolean isTookDamage() { return tookDamage; }
    public int getTotalBuffsUsed() { return totalBuffsUsed; }
    public int getHighestDamageDealt() { return highestDamageDealt; }

    @Override
    public String getEventName() {
        return "MatchWonEvent";
    }
}
