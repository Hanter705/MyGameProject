package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private Player player;
    private ArrayList<Enemy> enemies;
    private OrthographicCamera camera; // камера
    private float damageCooldown = 0f;
    private final float DAMAGE_INTERVAL = 0.5f; // полсекунды между ударами


    @Override
    public void show() {
        batch = new SpriteBatch();

        // === создаём камеру ===
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600); // размер “вида” камеры

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
        // === очищаем экран ===
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // === обновляем игрока и врагов ===
        player.update(delta);
        for (Enemy enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY(), enemies);

        }
        // === Проверка столкновений игрока с врагами ===
        damageCooldown -= delta;
        for (Enemy enemy : enemies) {
            float dx = enemy.getX() - player.getX();
            float dy = enemy.getY() - player.getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < 40 && damageCooldown <= 0f) {
                player.takeDamage(10); // отнимаем 10 HP
                damageCooldown = DAMAGE_INTERVAL; // перезарядка
            }
        }



        // === камера следует за игроком ===
        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(player.getX() + 48, player.getY() + 48, 0),
            0.09f
        );
        camera.update();

        // === применяем камеру к SpriteBatch ===
        batch.setProjectionMatrix(camera.combined);

        // === отрисовка спрайтов ===
        batch.begin();
        player.draw(batch);
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
        batch.end();

        // === отрисовка HP-шек ===
        player.drawHP(camera); // ← добавили передачу камеры
        for (Enemy enemy : enemies) {
            enemy.drawHP(camera); // ← добавили передачу камеры
        }
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
