package com.battlebees;

import com.battlebees.controller.GameController;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(4567);
        staticFiles.location("/public");
        new GameController();
        System.out.println("Battle Bees RPG server started at http://localhost:4567");
    }
}
