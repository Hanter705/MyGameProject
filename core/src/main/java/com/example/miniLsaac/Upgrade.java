package com.example.miniLsaac;

public class Upgrade {
    private final String name;        // название, например "Урон +20%"
    private final String description; // короткое описание
    private final Runnable effect;    // действие (что делает улучшение)

    public Upgrade(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public void apply() { effect.run(); } // выполняет действие
}
