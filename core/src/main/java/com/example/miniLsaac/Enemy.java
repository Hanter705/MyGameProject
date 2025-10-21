package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;




public class Enemy {

    private Texture[] flyTextures;
    private Animation<TextureRegion> flyAnim;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean alive = true;

    private float x, y;
    private float speed = 60f;

    private int maxHP = 50;
    private int hp = maxHP;

    private ShapeRenderer hpBar;

    public Enemy(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("enemy_fly_1.png")),
            new Texture(Gdx.files.internal("enemy_fly_2.png"))
        };

        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }

        flyAnim = new Animation<>(0.2f, frames);
        hpBar = new ShapeRenderer();
    }

    public void update(float delta, float playerX, float playerY, ArrayList<Enemy> allEnemies){
    stateTime += delta;
        currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // --- движение к игроку ---
        float dx = playerX - x;
        float dy = playerY - y;
        float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);

        if (distanceToPlayer > 1) {
            x += (dx / distanceToPlayer) * speed * delta;
            y += (dy / distanceToPlayer) * speed * delta;
        }

        // --- отталкивание от других врагов ---
        float repelForce = 80f; // сила отталкивания
        float minDistance = 40f; // минимальная дистанция между врагами

        for (Enemy other : allEnemies) {
            if (other == this) continue; // пропускаем самого себя

            float ox = other.x - this.x;
            float oy = other.y - this.y;
            float distance = (float) Math.sqrt(ox * ox + oy * oy);

            if (distance < minDistance && distance > 0.1f) {
                // направление отталкивания
                float repelX = (this.x - other.x) / distance;
                float repelY = (this.y - other.y) / distance;

                // применяем силу отталкивания
                this.x += repelX * repelForce * delta;
                this.y += repelY * repelForce * delta;
            }
        }
    }

    //public void draw(SpriteBatch batch) {
    //    batch.draw(currentFrame, x, y, 64, 64);
    //}


    public void draw(SpriteBatch batch) {
        if (currentFrame == null && flyAnim != null) {
            currentFrame = flyAnim.getKeyFrame(0);
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, 64, 64);
        }
    }


    public void drawHP(OrthographicCamera camera) {

        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 50;
        float barHeight = 5;

        float barX = x + 8;
        float barY = y + 60; // чуть выше врага

        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }


    public boolean isAlive() {
        return alive;
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }


    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        hpBar.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
