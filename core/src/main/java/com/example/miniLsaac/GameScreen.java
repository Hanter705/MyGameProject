package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private Player player;
    private ArrayList<Enemy> enemies;

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Создаём игрока
        player = new Player();

        // Создаём список врагов
        enemies = new ArrayList<>();

        // Добавляем нескольких врагов в разные позиции
        enemies.add(new Enemy(400, 200));
        enemies.add(new Enemy(600, 400));
        enemies.add(new Enemy(200, 500));
    }

    @Override
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем игрока
        player.update(delta);

        // Обновляем всех врагов
        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
        }

        // Отрисовка
        batch.begin();
        player.draw(batch);

        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
