package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Wizard {
    // === Позиция и движение ===
    private float x = 100, y = 100;
    private float speed = 150;
    private boolean facingRight = true;
    private float stateTime = 0f;

    // === Анимация ===
    private Texture[] flyTextures;
    private Animation<TextureRegion> flyAnim;
    private TextureRegion currentFrame;

    // === Здоровье ===
    private int maxHP = 100;
    private int hp = maxHP;
    private ShapeRenderer hpBar = new ShapeRenderer();

    public Wizard() {
        // Загружаем кадры полета
        flyTextures = new Texture[]{
            new Texture("wizard_fly_1.png"),
            new Texture("wizard_fly_2.png"),
            new Texture("wizard_fly_3.png"),
            new Texture("wizard_fly_4.png"),
            new Texture("wizard_fly_5.png"),
            new Texture("wizard_fly_6.png")
        };

        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }

        // Создаём анимацию полета (0.1f = скорость смены кадров)
        flyAnim = new Animation<>(0.1f, frames);
        currentFrame = frames[0];
    }

    // === Обновление логики движения ===
    public void update(float delta) {
        stateTime += delta;
        boolean moving = false;

        // Управление (WASD)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * delta;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * delta;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
            facingRight = false;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
            facingRight = true;
            moving = true;
        }

        // Обновляем кадр анимации
        if (moving)
            currentFrame = flyAnim.getKeyFrame(stateTime, true);
        else
            currentFrame = flyAnim.getKeyFrame(0); // стоячий кадр
    }

    // === Отрисовка персонажа ===
    public void draw(SpriteBatch batch) {
        if (facingRight)
            batch.draw(currentFrame, x, y, 96, 96);
        else
            batch.draw(currentFrame, x + 96, y, -96, 96); // зеркалим влево
    }

    // === Отрисовка полоски здоровья ===
    public void drawHP(OrthographicCamera camera) {
        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 60;
        float barHeight = 6;
        float barX = x + 18;
        float barY = y + 90;

        // фон
        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        // заполнение
        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }

    // === Логика урона и здоровья ===
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    // === Геттеры для координат и HP ===
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }

    // === Очистка ресурсов ===
    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        hpBar.dispose();
    }
}
