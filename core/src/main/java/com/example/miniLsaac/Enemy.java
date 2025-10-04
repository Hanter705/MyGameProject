package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Enemy {
    private Texture texture;
    private float x = 400, y = 200;
    private float speed = 100;

    public Enemy() {
        texture = new Texture("enamy.png"); // положи enemy.png в папку assets
    }

    public void update(float delta) {
        x -= speed * delta; // враг движется влево
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
