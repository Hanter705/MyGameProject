package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private Texture idleTexture;
    private Texture[] walkTextures;
    private Animation<TextureRegion> walkAnim;
    private TextureRegion idleFrame, currentFrame;
    private float stateTime;
    private float x = 100, y = 100;
    private float speed = 150;
    private boolean facingRight = true;

    public Player() {
        // === Загружаем кадры ===
        idleTexture = new Texture("player idle.png");

        walkTextures = new Texture[]{
            new Texture("player_walk_1.png"),
            new Texture("player_walk_2.png"),
            new Texture("player_walk_3.png")
        };

        // === Создаём анимацию ходьбы вправо ===
        TextureRegion[] walkFrames = new TextureRegion[walkTextures.length];
        for (int i = 0; i < walkTextures.length; i++) {
            walkFrames[i] = new TextureRegion(walkTextures[i]);
        }

        walkAnim = new Animation<>(0.15f, walkFrames); // 0.15f — скорость анимации
        idleFrame = new TextureRegion(idleTexture);
        currentFrame = idleFrame;
        stateTime = 0f;
    }

    public void update(float delta) {
        stateTime += delta;
        boolean moving = false;

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
            facingRight = false; // поворачиваем влево
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
            facingRight = true; // поворачиваем вправо
            moving = true;
        }

        // --- Определяем кадр ---
        if (moving) {
            currentFrame = walkAnim.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleFrame;
        }
    }

    public void draw(SpriteBatch batch) {
        // --- Отрисовываем в зависимости от направления ---
        if (facingRight) {
            batch.draw(currentFrame, x, y, 96, 96);
        } else {
            // Рисуем зеркально по X
            batch.draw(currentFrame, x + 96, y, -96, 96);
        }
    }

    public void dispose() {
        idleTexture.dispose();
        for (Texture t : walkTextures) t.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
