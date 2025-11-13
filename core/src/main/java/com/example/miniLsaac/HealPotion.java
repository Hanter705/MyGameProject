package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HealPotion {

    private float x, y;
    private boolean collected = false;

    private Texture texture;
    private int healAmount = 30;

    public HealPotion(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("items/heal_potion.png");
    }

    public void update(float playerX, float playerY) {
        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);

        if (distance < 40) {
            collected = true;
        }
    }

    public boolean isCollected() { return collected; }

    public int getHealAmount() { return healAmount; }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 32, 32);
    }

    public void dispose() {
        texture.dispose();
    }
}
