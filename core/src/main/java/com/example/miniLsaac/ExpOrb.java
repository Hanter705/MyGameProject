package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;

public class ExpOrb {
    private Vector2 position;
    private Texture texture;
    private boolean collected = false;
    private int expValue;

    public ExpOrb(float x, float y, int expValue) {
        this.position = new Vector2(x, y);
        this.expValue = expValue;
        this.texture = new Texture(Gdx.files.internal("coin/spr_coin_gri_1.png"));
    }

    public void update(float playerX, float playerY) {
        // Проверяем, близко ли игрок
        float dx = playerX - position.x;
        float dy = playerY - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 40f) { // расстояние подбора
            collected = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!collected)
            batch.draw(texture, position.x, position.y, 24, 24);
    }

    public boolean isCollected() { return collected; }
    public int getExpValue() { return expValue; }
    public float getX() { return position.x; }
    public float getY() { return position.y; }

    public void dispose() { texture.dispose(); }
}
