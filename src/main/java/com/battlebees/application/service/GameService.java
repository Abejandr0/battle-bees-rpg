package com.battlebees.application.service;

import com.battlebees.infrastructure.persistence.GameContext;
import com.battlebees.domain.model.factory.EquipmentFactory;
import com.battlebees.domain.model.hero.*;
import com.battlebees.domain.model.strategy.*;
import com.battlebees.domain.event.EventPublisher;
import com.battlebees.domain.event.BuffAppliedEvent;

public class GameService {
    private final GameContext context;
    private final EventPublisher publisher;

    public GameService(GameContext context, EventPublisher publisher) {
        this.context = context;
        this.publisher = publisher;
    }

    public void startGame(String heroClass) {
        context.reset();
        Hero player = createHeroByClass(heroClass, "Player Wing Leader");
        Hero enemy = createHeroByClass("WarriorBee", "Enemy Hive Guard");
        context.setPlayerHero(player);
        context.setEnemyHero(enemy);
        context.addLog("Battle started! You deployed " + player.getHeroClass() + ".");
    }

    private Hero createHeroByClass(String heroClass, String name) {
        switch (heroClass) {
            case "WarriorBee": return new WarriorBee(name);
            case "MageBee": return new MageBee(name);
            case "AssassinBee": return new AssassinBee(name);
            default: return new WarriorBee(name);
        }
    }

    public void equipItem(String itemName) {
        Hero current = context.getPlayerHero();
        if (current == null || current.isDead()) return;
        
        if (itemName.equals("helmet")) {
            context.setPlayerHero(EquipmentFactory.createHelmet(current));
            context.addLog("Equipped Tactical Helmet.");
        } else if (itemName.equals("armor")) {
            context.setPlayerHero(EquipmentFactory.createArmor(current));
            context.addLog("Equipped Kevlar Vest.");
        } else if (itemName.equals("weapon")) {
            context.setPlayerHero(EquipmentFactory.createWeapon(current));
            context.addLog("Equipped Stinger Rifle.");
        }
    }

    public void applyBuff(String buffName) {
        Hero current = context.getPlayerHero();
        if (current == null || current.isDead()) return;

        if (buffName.equals("rage")) {
            context.setPlayerHero(EquipmentFactory.createRageBuff(current));
            context.addLog("Injected Rage Stimpack!");
            publisher.publish(new BuffAppliedEvent(current.getName(), "Rage"));
        } else if (buffName.equals("shield")) {
            context.setPlayerHero(EquipmentFactory.createShieldBuff(current));
            context.addLog("Deployed Energy Shield!");
            publisher.publish(new BuffAppliedEvent(current.getName(), "Shield"));
        }
    }

    public void switchStrategy(String strategyName) {
        Hero current = context.getPlayerHero();
        if (current == null || current.isDead()) return;

        if (strategyName.equals("Normal")) current.setStrategy(new NormalStrategy());
        else if (strategyName.equals("Aggressive")) current.setStrategy(new AggressiveStrategy());
        else if (strategyName.equals("Defensive")) current.setStrategy(new DefensiveStrategy());
        
        context.addLog("Tactics shifted to: " + strategyName + ".");
    }
}
