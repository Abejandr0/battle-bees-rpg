package com.battlebees.domain.event;

public class CriticalHitEvent implements DomainEvent {
    private final String attackerName;

    public CriticalHitEvent(String attackerName) {
        this.attackerName = attackerName;
    }

    public String getAttackerName() { return attackerName; }

    @Override
    public String getEventName() {
        return "CriticalHitEvent";
    }
}
