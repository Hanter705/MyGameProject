package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Враг типа FireBat — летает к игроку и наносит урон при касании.
 */
public class FireBat {
    private float x, y;
    private float speed = 120f;
    private int hp = 50, maxHp = 50;
    private boolean alive = true;

    private Texture texture;
    private TextureRegion[] frames;
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    private boolean facingLeft = false;
    private ShapeRenderer hpBar;

    public FireBat(float x, float y) {
        this.x = x;
        this.y = y;

        // 🔥 загружаем 4 кадра анимации (твои PNG)
        TextureRegion[] tmp = new TextureRegion[4];
        tmp[0] = new TextureRegion(new Texture(Gdx.files.internal("bad/BatFire_Flying_1.png")));
        tmp[1] = new TextureRegion(new Texture(Gdx.files.internal("bad/BatFire_Flying_2.png")));
        tmp[2] = new TextureRegion(new Texture(Gdx.files.internal("bad/BatFire_Flying_3.png")));
        tmp[3] = new TextureRegion(new Texture(Gdx.files.internal("bad/BatFire_Flying_4.png")));
        this.frames = tmp;

        animation = new Animation<>(0.15f, frames); // скорость смены кадров
        hpBar = new ShapeRenderer();
    }

    public void update(float delta, float playerX, float playerY) {
        if (!alive) return;

        stateTime += delta;

        // Двигается к игроку
        float dx = playerX - x;
        float dy = playerY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 2) {
            x += dx / dist * speed * delta;
            y += dy / dist * speed * delta;
        }

        // направление взгляда
        facingLeft = dx < 0;
    }

    public void draw(SpriteBatch batch) {
        if (!alive) return;

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        if (facingLeft && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (!facingLeft && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, x, y, 64, 64); // размер 64x64 (можешь поменять)
    }

    public void drawHP(OrthographicCamera camera) {
        if (!alive) return;

        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);
        float width = 40, height = 5;
        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(x + 12, y + 60, width, height);
        hpBar.setColor(Color.RED);
        hpBar.rect(x + 12, y + 60, width * ((float) hp / maxHp), height);
        hpBar.end();
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void dispose() {
        for (TextureRegion region : frames) region.getTexture().dispose();
        hpBar.dispose();
    }
}
