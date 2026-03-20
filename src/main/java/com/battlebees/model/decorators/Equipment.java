package com.battlebees.model.decorators;

import com.battlebees.model.hero.Hero;
import java.util.List;

public class Equipment extends HeroDecorator {
    private String equipmentName;
    private int attackBonus;
    private int defenseBonus;

    public Equipment(Hero decoratedHero, String equipmentName, int attackBonus, int defenseBonus) {
        super(decoratedHero);
        this.equipmentName = equipmentName;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    @Override
    public int getAttack() {
        return super.getAttack() + this.attackBonus;
    }

    @Override
    public int getDefense() {
        return super.getDefense() + this.defenseBonus;
    }

    @Override
    public List<String> getEquipment() {
        List<String> equipment = super.getEquipment();
        equipment.add(this.equipmentName + " (+ATK:" + this.attackBonus + " +DEF:" + this.defenseBonus + ")");
        return equipment;
    }
}
