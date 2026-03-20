package com.battlebees.model.strategy;

public class AggressiveStrategy implements CombatStrategy {
    @Override
    public int calculateAttack(int baseAttack) {
        return (int) (baseAttack * 1.5);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 0.5);
    }

    @Override
    public String getStrategyName() {
        return "Aggressive";
    }
}
