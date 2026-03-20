package com.battlebees.presentation.controller;

import com.battlebees.application.service.CombatService;
import com.battlebees.application.service.GameService;
import com.battlebees.domain.achievement.AchievementSystem;
import com.battlebees.infrastructure.event.SimpleEventBus;
import com.battlebees.infrastructure.persistence.GameContext;
import com.battlebees.domain.model.hero.Hero;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

public class GameController {
    private GameService gameService;
    private CombatService combatService;
    private SimpleEventBus eventBus;
    private AchievementSystem achievementSystem;
    private Gson gson;

    public GameController() {
        this.eventBus = new SimpleEventBus();
        this.achievementSystem = new AchievementSystem(this.eventBus);
        this.eventBus.subscribe(this.achievementSystem);
        
        GameContext context = GameContext.getInstance();
        this.gameService = new GameService(context, this.eventBus);
        this.combatService = new CombatService(context, this.eventBus);
        
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        post("/api/start", (req, res) -> {
            String heroClass = req.queryParams("heroClass");
            if (heroClass == null) heroClass = "WarriorBee";
            gameService.startGame(heroClass);
            combatService.startNewMatch();
            res.type("application/json");
            return "{\"status\":\"ok\"}";
        });

        post("/api/action", (req, res) -> {
            String action = req.queryParams("action");
            String target = req.queryParams("target");
            
            if ("attack".equals(action)) {
                combatService.executeTurn();
            } else if ("equip".equals(action)) {
                gameService.equipItem(target);
            } else if ("buff".equals(action)) {
                gameService.applyBuff(target);
                combatService.registerBuffUsed();
            } else if ("strategy".equals(action)) {
                gameService.switchStrategy(target);
            }
            res.type("application/json");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("events", eventBus.pollEvents());
            
            return gson.toJson(response);
        });

        get("/api/state", (req, res) -> {
            res.type("application/json");
            GameContext ctx = GameContext.getInstance();
            Map<String, Object> state = new HashMap<>();
            state.put("player", buildHeroDTO(ctx.getPlayerHero()));
            state.put("enemy", buildHeroDTO(ctx.getEnemyHero()));
            state.put("log", ctx.getCombatLog());
            return gson.toJson(state);
        });
    }

    private Map<String, Object> buildHeroDTO(Hero hero) {
        if (hero == null) return null;
        Map<String, Object> dto = new HashMap<>();
        dto.put("name", hero.getName());
        dto.put("heroClass", hero.getHeroClass());
        dto.put("health", hero.getHealth());
        dto.put("maxHealth", hero.getMaxHealth());
        dto.put("attack", hero.getAttack());
        dto.put("defense", hero.getDefense());
        dto.put("strategy", hero.getStrategy() != null ? hero.getStrategy().getStrategyName() : "None");
        dto.put("buffs", hero.getActiveBuffs());
        dto.put("equipment", hero.getEquipment());
        dto.put("isDead", hero.isDead());
        return dto;
    }
}
