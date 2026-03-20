package com.battlebees.model.hero;

import com.battlebees.model.strategy.NormalStrategy;

public class AssassinBee extends BaseHero {
    public AssassinBee(String name) {
        super(name, "Assassin Bee", 110, 25, 10, new NormalStrategy());
    }
}
