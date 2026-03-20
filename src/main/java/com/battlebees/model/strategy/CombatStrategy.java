package com.battlebees.model.strategy;

public interface CombatStrategy {
    int calculateAttack(int baseAttack);
    int calculateDefense(int baseDefense);
    String getStrategyName();
}
