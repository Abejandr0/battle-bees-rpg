package com.battlebees.domain.model.hero;

import com.battlebees.domain.model.strategy.NormalStrategy;

public class WarriorBee extends BaseHero {
    public WarriorBee(String name) {
        super(name, "Warrior Bee", 150, 15, 20, new NormalStrategy());
    }
}
