package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Fireball {
    private float x, y;           // позиция
    private float dx, dy;         // направление движения
    private float speed = 400f;   // скорость
    private boolean active = true;
    private int damage = 20; // урон, который наносит файрбол

    private Texture texture;
    private Sprite sprite;

    public Fireball(float startX, float startY, float dirX, float dirY) {
        this.x = startX;
        this.y = startY;
        this.dx = dirX;
        this.dy = dirY;

        // нормализуем направление
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len != 0) {
            dx /= len;
            dy /= len;
        }

        // === Выбираем текстуру в зависимости от направления ===
        if (dx < 180) {
            texture = new Texture("fier_ball_4_left.png"); // летит влево
        } else {
            texture = new Texture("fier_ball_4.png");      // летит вправо
        }

        sprite = new Sprite(texture);

        // вычисляем угол
        float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

        // корректируем угол (если исходный спрайт смотрит вправо)
        sprite.setRotation(angle - 180);
    }



    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;

        sprite.setPosition(x, y);

        // удаляем, если вылетел за экран
        if (x < -100 || x > 2000 || y < -100 || y > 1200) {
            active = false;
        }
    }
    public int getDamage() {
        return damage;
    }


    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isActive() {
        return active;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public void dispose() {
        texture.dispose();
    }
}
