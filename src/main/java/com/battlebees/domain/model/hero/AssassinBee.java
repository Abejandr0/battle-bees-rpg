package com.battlebees.domain.model.hero;

import com.battlebees.domain.model.strategy.NormalStrategy;

public class AssassinBee extends BaseHero {
    public AssassinBee(String name) {
        super(name, "Assassin Bee", 110, 25, 10, new NormalStrategy());
    }
}
