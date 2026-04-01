package com.battlebees.application.service;

import com.battlebees.domain.event.BuffAppliedEvent;
import com.battlebees.domain.event.CriticalHitEvent;
import com.battlebees.domain.event.EventPublisher;
import com.battlebees.domain.event.HeroAttackedEvent;
import com.battlebees.domain.model.decorator.Buff;
import com.battlebees.domain.model.hero.Hero;
import com.battlebees.infrastructure.persistence.GameContext;

import java.util.Random;

public class EnemyAIService {
    private final EventPublisher publisher;
    private final Random random = new Random();

    public EnemyAIService(EventPublisher publisher) {
        this.publisher = publisher;
    }

    public void processEnemyAction(GameContext context) {
        Hero player = context.getPlayerHero();
        Hero enemy = context.getEnemyHero();

        if (player == null || enemy == null || player.isDead() || enemy.isDead()) {
            return;
        }

        int choice = random.nextInt(100);

        if (choice < 20) {
            applyEnemyBuff(enemy, context);
        } else if (choice < 30) {
            performAttack(enemy, player, true, context);
        } else {
            performAttack(enemy, player, false, context);
        }
    }

    private void applyEnemyBuff(Hero enemy, GameContext context) {
        Buff enemyBuff = new Buff(enemy, "Feral Instinct", 15, 10, 2);
        context.setEnemyHero(enemyBuff);
        context.addLog(enemy.getName() + " uses Feral Instinct!");
        publisher.publish(new BuffAppliedEvent(enemy.getName(), "Feral Instinct"));
    }

    private void performAttack(Hero attacker, Hero defender, boolean isCriticalAttempt, GameContext context) {
        int attackPower = attacker.getAttack();
        int defensePower = defender.getDefense();
        boolean isCritical = false;

        if (isCriticalAttempt) {
            isCritical = true;
        } else {
            isCritical = random.nextInt(100) < 15;
        }

        if (isCritical) {
            attackPower = (int)(attackPower * 1.5);
            context.addLog("CRITICAL STRIKE! " + attacker.getName() + " found a weak point.");
            publisher.publish(new CriticalHitEvent(attacker.getName()));
        }

        int damage = Math.max(1, attackPower - (defensePower / 2));
        defender.takeDamage(damage);
        context.addLog(attacker.getName() + " dealt " + damage + " damage to " + defender.getName() + ".");

        publisher.publish(new HeroAttackedEvent(attacker.getName(), defender.getName(), damage));
    }
}
