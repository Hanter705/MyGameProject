package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DeathScreen implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;

    private int level;
    private int wave;
    private int enemiesKilled;

    public DeathScreen(int level, int wave, int enemiesKilled) {
        this.level = level;
        this.wave = wave;
        this.enemiesKilled = enemiesKilled;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Получаем ширину и высоту окна
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Главный текст
        String title = " YOU DIED ";
        String levelText = "LEVEL: " + level;
        String waveText = "WAVE: " + wave;
        String killsText = "ENEMIES KILLED: " + enemiesKilled;
        String restartText = "Press [R] to Restart";

        // Измеряем ширину каждого текста
        float titleWidth = font.getRegion().getRegionWidth();
        // Лучше: создаём Layout для точной ширины текста
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

        // Рисуем всё по центру
        layout.setText(font, title);
        font.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight / 2 + 120);

        layout.setText(font, levelText);
        font.draw(batch, levelText, (screenWidth - layout.width) / 2, screenHeight / 2 + 60);

        layout.setText(font, waveText);
        font.draw(batch, waveText, (screenWidth - layout.width) / 2, screenHeight / 2 + 20);

        layout.setText(font, killsText);
        font.draw(batch, killsText, (screenWidth - layout.width) / 2, screenHeight / 2 - 20);

        layout.setText(font, restartText);
        font.draw(batch, restartText, (screenWidth - layout.width) / 2, screenHeight / 2 - 100);

        batch.end();

        // Перезапуск
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            Main.switchScreen(new GameScreen());
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
