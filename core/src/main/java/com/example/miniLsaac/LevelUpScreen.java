package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class LevelUpScreen implements Screen {
    private Wizard player;
    private GameScreen gameScreen;
    private SpriteBatch batch;
    private BitmapFont font;
    private int selectedOption = 0;

    private final ArrayList<Upgrade> upgrades = new ArrayList<>();

    public LevelUpScreen(Wizard player, GameScreen gameScreen) {
        this.player = player;
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        // === Добавляем улучшения ===
        upgrades.add(new Upgrade(
            "Increase damage +20%",
            "Fireballs deal more damage",
            () -> player.increaseDamage(0.2f)
        ));

        upgrades.add(new Upgrade(
            "Increase speed +10%",
            "The character moves faster",
            () -> player.increaseSpeed(0.1f)
        ));

        upgrades.add(new Upgrade(
            "Rate of Fire +15%",
            "Fireballs are released more frequently",
            () -> player.increaseFireRate(0.15f)
        ));

        // 🔮 Пример будущих апгрейдов:
        // upgrades.add(new Upgrade("Щит", "Автоматически блокирует один удар", () -> player.enableShield()));
        // upgrades.add(new Upgrade("Вампиризм", "Восстанавливает HP при убийстве врагов", () -> player.enableVampire()));
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

        // === Отрисовываем варианты ===
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade up = upgrades.get(i);
            if (i == selectedOption) font.setColor(Color.CYAN);
            else font.setColor(Color.WHITE);

            font.draw(batch, up.getName(), centerX - 220, centerY + 80 - i * 80);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, up.getDescription(), centerX - 200, centerY + 50 - i * 80);
        }

        batch.end();

        // Навигация ↑ ↓
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + upgrades.size()) % upgrades.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % upgrades.size();
        }

        // Подтверждение выбора
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            upgrades.get(selectedOption).apply(); // выполняем действие
            gameScreen.resumeAfterLevelUp(); // возвращаемся в игру
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
