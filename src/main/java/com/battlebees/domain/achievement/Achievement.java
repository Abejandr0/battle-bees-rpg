package com.battlebees.domain.achievement;

public class Achievement {
    private final String id;
    private final String name;
    private boolean unlocked;

    public Achievement(String id, String name) {
        this.id = id;
        this.name = name;
        this.unlocked = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isUnlocked() { return unlocked; }

    public void unlock() {
        this.unlocked = true;
    }
}
