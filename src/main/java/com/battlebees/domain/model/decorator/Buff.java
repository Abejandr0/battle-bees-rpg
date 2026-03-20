package com.battlebees.domain.model.decorator;

import com.battlebees.domain.model.hero.Hero;
import java.util.List;

public class Buff extends HeroDecorator {
    private String buffName;
    private int attackModifier;
    private int defenseModifier;
    private int duration;

    public Buff(Hero decoratedHero, String buffName, int attackModifier, int defenseModifier, int duration) {
        super(decoratedHero);
        this.buffName = buffName;
        this.attackModifier = attackModifier;
        this.defenseModifier = defenseModifier;
        this.duration = duration;
    }

    public void decrementDuration() {
        if (this.duration > 0) {
            this.duration--;
        }
    }

    public boolean isExpired() {
        return this.duration <= 0;
    }
    
    public String getBuffName() {
        return this.buffName;
    }

    public int getDuration() {
        return this.duration;
    }

    @Override
    public int getAttack() {
        return super.getAttack() + this.attackModifier;
    }

    @Override
    public int getDefense() {
        return super.getDefense() + this.defenseModifier;
    }

    @Override
    public List<String> getActiveBuffs() {
        List<String> buffs = super.getActiveBuffs();
        buffs.add(this.buffName + " (" + this.duration + " turns left)");
        return buffs;
    }
}
