package com.battlebees.domain.event;

public class BuffAppliedEvent implements DomainEvent {
    private final String heroName;
    private final String buffName;

    public BuffAppliedEvent(String heroName, String buffName) {
        this.heroName = heroName;
        this.buffName = buffName;
    }

    public String getHeroName() { return heroName; }
    public String getBuffName() { return buffName; }

    @Override
    public String getEventName() {
        return "BuffAppliedEvent";
    }
}
