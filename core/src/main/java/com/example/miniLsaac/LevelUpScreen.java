package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelUpScreen implements Screen {
    private Wizard player;
    private GameScreen gameScreen;
    private SpriteBatch batch;
    private BitmapFont font;
    private int selectedOption = 0;

    private final String[] options = {
        "Increase Damage +20%",
        "Increase Speed +10%",
        "Increase Fire Rate +15%"
    };

    public LevelUpScreen(Wizard player, GameScreen gameScreen) {
        this.player = player;
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }



    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        font.setColor(Color.GOLD);
        font.draw(batch, "LEVEL UP!", centerX - 120, centerY + 150);

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) font.setColor(Color.CYAN);
            else font.setColor(Color.WHITE);
            font.draw(batch, options[i], centerX - 150, centerY + 80 - i * 60);
        }

        batch.end();

        // Навигация ↑ ↓
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % options.length;
        }

        // Подтверждение Enter
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            applyUpgrade(selectedOption);
            gameScreen.resumeAfterLevelUp();
            // возвращаемся в игру
        }
    }

    private void applyUpgrade(int option) {
        switch (option) {
            case 0:
                player.increaseDamage(0.2f);
                break;
            case 1:
                player.increaseSpeed(0.1f);
                break;
            case 2:
                player.increaseFireRate(0.15f);
                break;
        }

        // Возвращаем игрока на тот же экран
        gameScreen.resumeAfterLevelUp();

    }



    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

