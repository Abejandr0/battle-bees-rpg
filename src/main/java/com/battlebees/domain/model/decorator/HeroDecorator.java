package com.battlebees.domain.model.decorator;

import com.battlebees.domain.model.hero.Hero;
import com.battlebees.domain.model.strategy.CombatStrategy;
import java.util.List;

public abstract class HeroDecorator implements Hero {
    protected Hero decoratedHero;

    public HeroDecorator(Hero decoratedHero) {
        this.decoratedHero = decoratedHero;
    }

    public Hero getDecoratedHero() {
        return this.decoratedHero;
    }

    public void setDecoratedHero(Hero hero) {
        this.decoratedHero = hero;
    }

    @Override
    public String getName() {
        return decoratedHero.getName();
    }

    @Override
    public String getHeroClass() {
        return decoratedHero.getHeroClass();
    }

    @Override
    public int getHealth() {
        return decoratedHero.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return decoratedHero.getMaxHealth();
    }

    @Override
    public int getBaseAttack() {
        return decoratedHero.getBaseAttack();
    }

    @Override
    public int getBaseDefense() {
        return decoratedHero.getBaseDefense();
    }

    @Override
    public int getAttack() {
        return decoratedHero.getAttack();
    }

    @Override
    public int getDefense() {
        return decoratedHero.getDefense();
    }

    @Override
    public void takeDamage(int damage) {
        decoratedHero.takeDamage(damage);
    }

    @Override
    public void heal(int amount) {
        decoratedHero.heal(amount);
    }

    @Override
    public boolean isDead() {
        return decoratedHero.isDead();
    }

    @Override
    public CombatStrategy getStrategy() {
        return decoratedHero.getStrategy();
    }

    @Override
    public void setStrategy(CombatStrategy strategy) {
        decoratedHero.setStrategy(strategy);
    }

    @Override
    public Hero getBaseHero() {
        return decoratedHero.getBaseHero();
    }

    @Override
    public List<String> getActiveBuffs() {
        return decoratedHero.getActiveBuffs();
    }

    @Override
    public List<String> getEquipment() {
        return decoratedHero.getEquipment();
    }
}
