package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class IceBall {

    private float x, y;
    private float speed = 150f;  // чуть быстрее
    private boolean active = true;

    private float dirX, dirY;
    private int damage;
    private float speedMultiplier = 1f;


    private Animation<TextureRegion> anim;
    private float stateTime = 0f;

    private float angle = 0f; // угол поворота пули
    private TextureRegion lastFrame;   // последний кадр анимации

    public IceBall(float startX, float startY, float targetX, float targetY, int dmg, float speedMultiplier) {
        this.x = startX;
        this.y = startY;
        this.damage = dmg;
        this.speedMultiplier = speedMultiplier;

        speed = 260f * speedMultiplier;  // скорость зависит от улучшений

        // === Загружаем кадры анимации ===
        Texture[] frames = new Texture[]{
            new Texture("atack/Fire_Bullet_1.png"),
            new Texture("atack/Fire_Bullet_2.png"),
            new Texture("atack/Fire_Bullet_3.png"),
            new Texture("atack/Fire_Bullet_4.png"),
        };

        TextureRegion[] regions = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            regions[i] = new TextureRegion(frames[i]);
        }

        anim = new Animation<>(0.1f, regions);
        anim.setPlayMode(Animation.PlayMode.NORMAL);  // важный момент!
        lastFrame = regions[regions.length - 1];       // сохранили последний кадр

        // --- направление на цель ---
        float dx = targetX - startX;
        float dy = targetY - startY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        if (len != 0) {
            dirX = dx / len;
            dirY = dy / len;
        }

        // === УГОЛ: превращаем направление (dx,dy) → угол в градусах ===
        angle = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void update(float delta) {
        if (!active) return;

        stateTime += delta;

        x += dirX * speed * delta;
        y += dirY * speed * delta;

        if (x < -2000 || x > 2000 || y < -2000 || y > 2000)
            active = false;
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        TextureRegion frame;

        // если анимация закончилась → всегда показываем последний кадр
        if (anim.isAnimationFinished(stateTime)) {
            frame = lastFrame;
        } else {
            frame = anim.getKeyFrame(stateTime, false);
        }

        batch.draw(
            frame,
            x, y,           // позиция
            12, 8,          // точка поворота (центр картинки)
            24, 16,         // ширина/высота
            1f, 1f,         // масштаб
            angle           // угол поворота
        );
    }

    public boolean isActive() { return active; }
    public void deactivate() { active = false; }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getDamage() { return damage; }


    public void dispose() {
        for (TextureRegion region : anim.getKeyFrames())
            region.getTexture().dispose();
    }
}
