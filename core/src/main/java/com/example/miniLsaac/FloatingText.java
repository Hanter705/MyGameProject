package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FloatingText {
    private float x, y;         // позиция
    private String text;        // сам текст
    private float lifeTime = 1.0f; // сколько секунд живёт
    private float elapsed = 0f; // время, прошедшее с начала
    private Color color;
    private BitmapFont font;

    public FloatingText(float x, float y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = new Color(color);
        font = new BitmapFont();
        font.setColor(color);
        font.getData().setScale(1.3f);
    }

    public boolean update(float delta) {
        elapsed += delta;
        y += 30 * delta; // надпись медленно поднимается вверх
        float alpha = 1f - (elapsed / lifeTime); // плавное исчезание
        font.setColor(color.r, color.g, color.b, alpha);
        return elapsed < lifeTime; // если false — удалить
    }

    public void draw(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }

    public void dispose() {
        font.dispose();
    }
}
