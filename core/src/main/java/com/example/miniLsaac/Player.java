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


public class Player {
    private Texture idleTexture;
    private Texture[] walkTextures;
    private Animation<TextureRegion> walkAnim;
    private TextureRegion idleFrame, currentFrame;
    private float stateTime;
    private float x = 100, y = 100;
    private float speed = 150;
    private boolean facingRight = true;

    // --- здоровье ---
    private int maxHP = 100;
    private int hp = maxHP;

    private ShapeRenderer hpBar; // для рисования полоски

    public Player() {
        idleTexture = new Texture("player idle.png");

        walkTextures = new Texture[]{
            new Texture("player_walk_1.png"),
            new Texture("player_walk_2.png"),
            new Texture("player_walk_3.png")
        };

        TextureRegion[] walkFrames = new TextureRegion[walkTextures.length];
        for (int i = 0; i < walkTextures.length; i++) {
            walkFrames[i] = new TextureRegion(walkTextures[i]);
        }

        walkAnim = new Animation<>(0.15f, walkFrames);
        idleFrame = new TextureRegion(idleTexture);
        currentFrame = idleFrame;
        stateTime = 0f;

        hpBar = new ShapeRenderer();
    }

    public void update(float delta) {
        stateTime += delta;
        boolean moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed * delta; facingRight = false; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed * delta; facingRight = true; moving = true; }

        if (moving) currentFrame = walkAnim.getKeyFrame(stateTime, true);
        else currentFrame = idleFrame;
    }

    public void draw(SpriteBatch batch) {
        if (facingRight) batch.draw(currentFrame, x, y, 96, 96);
        else batch.draw(currentFrame, x + 96, y, -96, 96);
    }

    // --- рисуем полоску здоровья ---
    public void drawHP(OrthographicCamera camera) {

        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 60;
        float barHeight = 6;

        float barX = x + 18; // чуть смещаем от левого края
        float barY = y + 90; // прямо над персонажем

        // фон
        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        // заполнение
        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }


    // --- наносим урон ---
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    // --- проверка смерти ---
    public boolean isDead() {
        return hp <= 0;
    }

    public void dispose() {
        idleTexture.dispose();
        for (Texture t : walkTextures) t.dispose();
        hpBar.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }
}
