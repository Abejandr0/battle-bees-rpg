package com.battlebees.factory;

import com.battlebees.model.decorators.Buff;
import com.battlebees.model.decorators.Equipment;
import com.battlebees.model.hero.Hero;

public class EquipmentFactory {
    public static Equipment createHelmet(Hero baseHero) {
        return new Equipment(baseHero, "Tactical Helmet", 0, 15);
    }
    
    public static Equipment createArmor(Hero baseHero) {
        return new Equipment(baseHero, "Kevlar Vest", 0, 30);
    }
    
    public static Equipment createWeapon(Hero baseHero) {
        return new Equipment(baseHero, "Stinger Rifle", 25, 0);
    }

    public static Buff createRageBuff(Hero baseHero) {
        return new Buff(baseHero, "Rage Stimpack", 30, -10, 3);
    }
    
    public static Buff createShieldBuff(Hero baseHero) {
        return new Buff(baseHero, "Energy Shield", 0, 40, 2);
    }
}
