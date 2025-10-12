package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy {

    private Texture[] flyTextures;
    private Animation<TextureRegion> flyAnim;
    private TextureRegion currentFrame;
    private float stateTime;

    private float x, y;
    private float speed = 60f; // скорость врага
    private float baseY;
    private float moveRange = 30f;

    public Enemy(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.baseY = startY;

        // Загружаем спрайты
        flyTextures = new Texture[]{
            new Texture("enemy_fly_1.png"),
            new Texture("enemy_fly_2.png")
        };

        // Создаём анимацию полёта
        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }

        flyAnim = new Animation<>(0.2f, frames);
        stateTime = 0f;
        currentFrame = frames[0];
    }

    public void update(float delta, float playerX, float playerY) {
        stateTime += delta;
        currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // Движение вверх-вниз (эффект полёта)
        y = y + (float) Math.sin(stateTime * 5) * 0.5f;

        // --- Преследование игрока ---
        float dx = playerX - x;
        float dy = playerY - y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1) {
            // нормализуем движение, чтобы летел плавно
            x += (dx / distance) * speed * delta;
            y += (dy / distance) * speed * delta;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame, x, y, 64, 64);
    }

    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
