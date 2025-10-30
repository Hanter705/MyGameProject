package com.example.miniLsaac;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {

    public static Main instance;
    public SpriteBatch batch;
    public static String playerName = "Unknown";


    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();
        setScreen(new StartScreen());

    }

    @Override
    public void render() {
        super.render(); // вызывает render() активного экрана
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    // Удобный метод для смены экранов
    public static void switchScreen(com.badlogic.gdx.Screen newScreen) {
        if (instance != null) {
            instance.setScreen(newScreen);
        }
    }
}
