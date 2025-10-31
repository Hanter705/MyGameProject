package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class DeathScreen implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;

    private int level;
    private int wave;
    private int enemiesKilled;
    private boolean saved = false; // 🔹 чтобы не сохранял дважды

    public DeathScreen(int level, int wave, int enemiesKilled) {
        this.level = level;
        this.wave = wave;
        this.enemiesKilled = enemiesKilled;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();

        String title = "YOU DIED";
        String nameText = "PLAYER: " + Main.playerName;
        String levelText = "LEVEL: " + level;
        String waveText = "WAVE: " + wave;
        String killsText = "ENEMIES KILLED: " + enemiesKilled;

        String saveText = saved ? "SAVED SUCCESSFULLY!" : "Press [S] to Save Progress";
        String restartText = "Press [R] to Restart";

        // центрирование текста
        font.setColor(Color.RED);
        layout.setText(font, title);
        font.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight / 2 + 140);

        font.setColor(Color.GOLD);
        layout.setText(font, nameText);
        font.draw(batch, nameText, (screenWidth - layout.width) / 2, screenHeight / 2 + 100);

        layout.setText(font, levelText);
        font.draw(batch, levelText, (screenWidth - layout.width) / 2, screenHeight / 2 + 60);

        layout.setText(font, waveText);
        font.draw(batch, waveText, (screenWidth - layout.width) / 2, screenHeight / 2 + 20);

        layout.setText(font, killsText);
        font.draw(batch, killsText, (screenWidth - layout.width) / 2, screenHeight / 2 - 20);

        // Текст "сохранить"
        font.setColor(saved ? Color.GREEN : Color.CYAN);
        layout.setText(font, saveText);
        font.draw(batch, saveText, (screenWidth - layout.width) / 2, screenHeight / 2 - 80);

        font.setColor(Color.WHITE);
        layout.setText(font, restartText);
        font.draw(batch, restartText, (screenWidth - layout.width) / 2, screenHeight / 2 - 130);

        batch.end();

        // === Нажатие клавиш ===
        if (!saved && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            DatabaseManager.savePlayerData(Main.playerName, level, wave, enemiesKilled);
            saved = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Main.switchScreen(new GameScreen());
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
