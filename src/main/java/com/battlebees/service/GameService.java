package com.battlebees.service;

import com.battlebees.context.GameContext;
import com.battlebees.factory.EquipmentFactory;
import com.battlebees.model.decorators.Buff;
import com.battlebees.model.decorators.Equipment;
import com.battlebees.model.decorators.HeroDecorator;
import com.battlebees.model.hero.AssassinBee;
import com.battlebees.model.hero.Hero;
import com.battlebees.model.hero.MageBee;
import com.battlebees.model.hero.WarriorBee;
import com.battlebees.model.strategy.AggressiveStrategy;
import com.battlebees.model.strategy.DefensiveStrategy;
import com.battlebees.model.strategy.NormalStrategy;
import java.util.Random;

public class GameService {
    private GameContext context = GameContext.getInstance();
    private Random random = new Random();

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
        } else if (buffName.equals("shield")) {
            context.setPlayerHero(EquipmentFactory.createShieldBuff(current));
            context.addLog("Deployed Energy Shield!");
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

    public void executeTurn() {
        Hero player = context.getPlayerHero();
        Hero enemy = context.getEnemyHero();

        if (player == null || enemy == null || player.isDead() || enemy.isDead()) {
            return;
        }

        processAttack(player, enemy);

        if (!enemy.isDead()) {
            processAttack(enemy, player);
        }

        Hero cleanedPlayer = processBuffExpiration(player);
        context.setPlayerHero(cleanedPlayer);
        Hero cleanedEnemy = processBuffExpiration(enemy);
        context.setEnemyHero(cleanedEnemy);
        
        if (context.getPlayerHero().isDead()) {
            context.addLog("MISSION FAILED: Your unit was neutralized.");
        } else if (context.getEnemyHero().isDead()) {
            context.addLog("VICTORY: Enemy Hive Guard eliminated.");
        }
    }

    private void processAttack(Hero attacker, Hero defender) {
        int attackPower = attacker.getAttack();
        int defensePower = defender.getDefense();
        
        boolean isCritical = false;
        if (attacker.getHeroClass().equals("Assassin Bee")) {
            isCritical = random.nextInt(100) < 40;
        } else {
            isCritical = random.nextInt(100) < 15;
        }

        if (isCritical) {
            attackPower = (int)(attackPower * 1.5);
            context.addLog("CRITICAL STRIKE! " + attacker.getName() + " found a weak point.");
        }

        int damage = Math.max(1, attackPower - (defensePower / 2));
        defender.takeDamage(damage);
        context.addLog(attacker.getName() + " dealt " + damage + " damage to " + defender.getName() + ".");
    }

    private Hero processBuffExpiration(Hero hero) {
        if (hero == null) return null;

        if (hero instanceof Buff) {
            Buff buff = (Buff) hero;
            buff.decrementDuration();
            Hero next = processBuffExpiration(buff.getDecoratedHero());
            
            if (buff.isExpired()) {
                context.addLog(buff.getBuffName() + " on " + hero.getName() + " dissipated.");
                return next;
            } else {
                buff.setDecoratedHero(next);
                return buff;
            }
        } else if (hero instanceof HeroDecorator) {
            HeroDecorator decorator = (HeroDecorator) hero;
            Hero next = processBuffExpiration(decorator.getDecoratedHero());
            decorator.setDecoratedHero(next);
            return decorator;
        }
        return hero;
    }
}
