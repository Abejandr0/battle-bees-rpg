package com.battlebees.domain.model.hero;

import com.battlebees.domain.model.strategy.NormalStrategy;

public class MageBee extends BaseHero {
    public MageBee(String name) {
        super(name, "Mage Bee", 90, 30, 5, new NormalStrategy());
    }
}
