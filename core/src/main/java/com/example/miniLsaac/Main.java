package com.example.miniLsaac;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this)); // запускаем экран игры
    }
}
