package com.battlebees.application.service;

import com.battlebees.infrastructure.persistence.GameContext;
import com.battlebees.domain.model.decorator.Buff;
import com.battlebees.domain.model.decorator.HeroDecorator;
import com.battlebees.domain.model.hero.Hero;
import com.battlebees.domain.event.*;
import java.util.Random;

public class CombatService {
    private final GameContext context;
    private final EventPublisher publisher;
    private final Random random = new Random();

    // Stats for MatchWonEvent
    private boolean tookDamageThisMatch = false;
    private int buffsUsedThisMatch = 0;
    private int highestDamageDealt = 0;

    public CombatService(GameContext context, EventPublisher publisher) {
        this.context = context;
        this.publisher = publisher;
    }

    public void startNewMatch() {
        tookDamageThisMatch = false;
        buffsUsedThisMatch = 0;
        highestDamageDealt = 0;
    }

    public void registerBuffUsed() {
        buffsUsedThisMatch++;
    }

    public void executeTurn() {
        Hero player = context.getPlayerHero();
        Hero enemy = context.getEnemyHero();

        if (player == null || enemy == null || player.isDead() || enemy.isDead()) {
            return;
        }

        processAttack(player, enemy, true);

        if (!enemy.isDead()) {
            processAttack(enemy, player, false);
        }

        Hero cleanedPlayer = processBuffExpiration(player);
        context.setPlayerHero(cleanedPlayer);
        Hero cleanedEnemy = processBuffExpiration(enemy);
        context.setEnemyHero(cleanedEnemy);
        
        if (context.getPlayerHero().isDead()) {
            context.addLog("MISSION FAILED: Your unit was neutralized.");
        } else if (context.getEnemyHero().isDead()) {
            context.addLog("VICTORY: Enemy Hive Guard eliminated.");
            publisher.publish(new MatchWonEvent(player.getHeroClass(), tookDamageThisMatch, buffsUsedThisMatch, highestDamageDealt));
        }
    }

    private void processAttack(Hero attacker, Hero defender, boolean isPlayerAttacking) {
        int attackPower = attacker.getAttack();
        int defensePower = defender.getDefense();
        
        boolean isCritical = false;
        if (attacker.getHeroClass().contains("Assassin")) {
            isCritical = random.nextInt(100) < 40;
        } else {
            isCritical = random.nextInt(100) < 15;
        }

        if (isCritical) {
            attackPower = (int)(attackPower * 1.5);
            context.addLog("CRITICAL STRIKE! " + attacker.getName() + " found a weak point.");
            if (isPlayerAttacking) {
                publisher.publish(new CriticalHitEvent(attacker.getName()));
            }
        }

        int damage = Math.max(1, attackPower - (defensePower / 2));
        defender.takeDamage(damage);
        context.addLog(attacker.getName() + " dealt " + damage + " damage to " + defender.getName() + ".");

        publisher.publish(new HeroAttackedEvent(attacker.getName(), defender.getName(), damage));

        if (isPlayerAttacking) {
            if (damage > highestDamageDealt) {
                highestDamageDealt = damage;
            }
        } else {
            tookDamageThisMatch = true;
        }
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
