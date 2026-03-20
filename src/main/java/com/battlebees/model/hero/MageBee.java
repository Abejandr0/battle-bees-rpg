package com.battlebees.model.hero;

import com.battlebees.model.strategy.NormalStrategy;

public class MageBee extends BaseHero {
    public MageBee(String name) {
        super(name, "Mage Bee", 90, 30, 5, new NormalStrategy());
    }
}
