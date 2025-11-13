package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Огненный летучий враг (наследник Enemy)
 * - Быстрее и крепче обычных.
 * - Дает больше опыта.
 * - Отзеркаливается при полёте влево.
 */
public class FireBat extends Enemy {



    public FireBat(float x, float y) {
        super(x, y); // вызывает конструктор Enemy

        // индивидуальные параметры
        this.speed = 70f;
        this.maxHP = 220;
        this.hp = maxHP;
        this.damage = 35;
        this.expDrop = 200;

        // загружаем свои текстуры
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatFire_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_4.png"))
        };

        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }

        // анимация
        flyAnim = new Animation<>(0.12f, frames);
        flyAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta, float playerX, float playerY, java.util.ArrayList<Enemy> allEnemies) {
        super.update(delta, playerX, playerY, allEnemies);

        // определяем направление (влево или вправо)
        facingLeft = (playerX < x);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (flyAnim == null) return;

        if (currentFrame == null)
            currentFrame = flyAnim.getKeyFrame(0);
        else
            currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // создаем копию кадра (чтобы не менять оригинал)
        TextureRegion frame = new TextureRegion(currentFrame);

        // зеркалим, если движется влево
        if (facingLeft && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!facingLeft && frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(frame, x, y, 64, 64);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : flyTextures) t.dispose();
    }
}
