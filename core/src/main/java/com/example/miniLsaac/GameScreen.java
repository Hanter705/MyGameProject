package com.example.miniLsaac;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Player player;
    private Enemy enemy;

    public GameScreen(Main game) {
        batch = new SpriteBatch();
        player = new Player();
        enemy = new Enemy();
    }

    @Override
    public void render(float delta) {
        // очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // отрисовка
        batch.begin();
        player.draw(batch);
        enemy.draw(batch);
        batch.end();

        // обновление логики
        player.update(delta);
        enemy.update(delta);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); }
}
