package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private int selected = 0;
    private boolean nameEntered = false;
    private StringBuilder playerName = new StringBuilder();

    private final String[] options = {"Start Game", "Exit"};

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        font.setColor(Color.GOLD);
        font.draw(batch, "MY GAME", centerX - 100, centerY + 150);

        if (!nameEntered) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Enter your name:", centerX - 150, centerY + 30);

            // курсор для визуального эффекта
            String cursor = (System.currentTimeMillis() / 500 % 2 == 0) ? "_" : "";
            font.setColor(Color.CYAN);
            font.draw(batch, playerName.toString() + cursor, centerX - 150, centerY - 20);
        } else {
            // меню после ввода имени
            for (int i = 0; i < options.length; i++) {
                if (i == selected)
                    font.setColor(Color.CYAN);
                else
                    font.setColor(Color.WHITE);

                font.draw(batch, options[i], centerX - 80, centerY - i * 60);
            }
        }

        batch.end();

        // === Ввод имени ===
        if (!nameEntered) {
            for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
                if (Gdx.input.isKeyJustPressed(key) && playerName.length() < 12) {
                    char c = (char) ('A' + (key - Input.Keys.A));
                    if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                        c = Character.toLowerCase(c);
                    playerName.append(c);
                }
            }

            // пробел
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && playerName.length() < 12) {
                playerName.append(" ");
            }

            // удаление символа
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
                playerName.deleteCharAt(playerName.length() - 1);
            }

            // подтверждение имени
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && playerName.length() > 0) {
                nameEntered = true;
                Main.playerName = playerName.toString(); // сохраняем глобально
            }

            return; // пока не введено имя — не показываем меню
        }

        // === Управление меню ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + options.length) % options.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % options.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selected == 0) {
                Main.switchScreen(new GameScreen());
            } else if (selected == 1) {
                Gdx.app.exit();
            }
        }
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
