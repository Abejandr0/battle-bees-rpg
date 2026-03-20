package com.battlebees.domain.model.strategy;

public class NormalStrategy implements CombatStrategy {
    @Override
    public int calculateAttack(int baseAttack) {
        return baseAttack;
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return baseDefense;
    }

    @Override
    public String getStrategyName() {
        return "Normal";
    }
}
