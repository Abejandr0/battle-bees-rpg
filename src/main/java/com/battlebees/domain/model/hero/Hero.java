package com.battlebees.domain.model.hero;

import com.battlebees.domain.model.strategy.CombatStrategy;
import java.util.List;

public interface Hero {
    String getName();
    String getHeroClass();
    int getHealth();
    int getMaxHealth();
    int getBaseAttack();
    int getBaseDefense();
    int getAttack();
    int getDefense();
    void takeDamage(int damage);
    void heal(int amount);
    boolean isDead();
    CombatStrategy getStrategy();
    void setStrategy(CombatStrategy strategy);
    Hero getBaseHero();
    List<String> getActiveBuffs();
    List<String> getEquipment();
}
