package com.battlebees.domain.model.hero;

import com.battlebees.domain.model.strategy.CombatStrategy;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseHero implements Hero {
    protected String name;
    protected String heroClass;
    protected int health;
    protected int maxHealth;
    protected int baseAttack;
    protected int baseDefense;
    protected CombatStrategy strategy;

    public BaseHero(String name, String heroClass, int maxHealth, int baseAttack, int baseDefense, CombatStrategy strategy) {
        this.name = name;
        this.heroClass = heroClass;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.strategy = strategy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHeroClass() {
        return heroClass;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public int getBaseAttack() {
        return baseAttack;
    }

    @Override
    public int getBaseDefense() {
        return baseDefense;
    }

    @Override
    public int getAttack() {
        return strategy.calculateAttack(baseAttack);
    }

    @Override
    public int getDefense() {
        return strategy.calculateDefense(baseDefense);
    }

    @Override
    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    @Override
    public void heal(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
    }

    @Override
    public boolean isDead() {
        return this.health <= 0;
    }

    @Override
    public CombatStrategy getStrategy() {
        return strategy;
    }

    @Override
    public void setStrategy(CombatStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Hero getBaseHero() {
        return this;
    }

    @Override
    public List<String> getActiveBuffs() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getEquipment() {
        return new ArrayList<>();
    }
}
