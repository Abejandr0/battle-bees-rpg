package com.battlebees.infrastructure.persistence;

import com.battlebees.domain.model.hero.Hero;
import java.util.ArrayList;
import java.util.List;

public class GameContext {
    private static GameContext instance;
    private Hero playerHero;
    private Hero enemyHero;
    private List<String> combatLog;

    private GameContext() {
        combatLog = new ArrayList<>();
    }

    public static synchronized GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }
        return instance;
    }

    public Hero getPlayerHero() {
        return playerHero;
    }

    public void setPlayerHero(Hero playerHero) {
        this.playerHero = playerHero;
    }

    public Hero getEnemyHero() {
        return enemyHero;
    }

    public void setEnemyHero(Hero enemyHero) {
        this.enemyHero = enemyHero;
    }

    public List<String> getCombatLog() {
        return combatLog;
    }

    public void addLog(String message) {
        combatLog.add(message);
    }

    public void clearLog() {
        combatLog.clear();
    }
    
    public void reset() {
        playerHero = null;
        enemyHero = null;
        combatLog.clear();
    }
}
