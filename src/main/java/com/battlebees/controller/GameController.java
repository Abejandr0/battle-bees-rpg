package com.battlebees.controller;

import com.battlebees.context.GameContext;
import com.battlebees.model.hero.Hero;
import com.battlebees.service.GameService;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

public class GameController {
    private GameService gameService;
    private Gson gson;

    public GameController() {
        this.gameService = new GameService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        post("/api/start", (req, res) -> {
            String heroClass = req.queryParams("heroClass");
            if (heroClass == null) heroClass = "WarriorBee";
            gameService.startGame(heroClass);
            res.type("application/json");
            return "{\"status\":\"ok\"}";
        });

        post("/api/action", (req, res) -> {
            String action = req.queryParams("action");
            String target = req.queryParams("target");
            
            if ("attack".equals(action)) {
                gameService.executeTurn();
            } else if ("equip".equals(action)) {
                gameService.equipItem(target);
            } else if ("buff".equals(action)) {
                gameService.applyBuff(target);
            } else if ("strategy".equals(action)) {
                gameService.switchStrategy(target);
            }
            res.type("application/json");
            return "{\"status\":\"ok\"}";
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
        dto.put("strategy", hero.getStrategy().getStrategyName());
        dto.put("buffs", hero.getActiveBuffs());
        dto.put("equipment", hero.getEquipment());
        dto.put("isDead", hero.isDead());
        return dto;
    }
}
