package com.battlebees.domain.event;

public class HeroAttackedEvent implements DomainEvent {
    private final String attackerName;
    private final String defenderName;
    private final int damage;
    
    public HeroAttackedEvent(String attackerName, String defenderName, int damage) {
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.damage = damage;
    }

    public String getAttackerName() { return attackerName; }
    public String getDefenderName() { return defenderName; }
    public int getDamage() { return damage; }

    @Override
    public String getEventName() {
        return "HeroAttackedEvent";
    }
}
