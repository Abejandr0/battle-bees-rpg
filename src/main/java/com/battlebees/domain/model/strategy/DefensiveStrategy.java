package com.battlebees.domain.model.strategy;

public class DefensiveStrategy implements CombatStrategy {
    @Override
    public int calculateAttack(int baseAttack) {
        return (int) (baseAttack * 0.5);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 1.5);
    }

    @Override
    public String getStrategyName() {
        return "Defensive";
    }
}
