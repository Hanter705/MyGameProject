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
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;


public class Wizard {
    // === Позиция и движение ===
    private float x = 400, y = 400;
    private float speed = 150;
    private boolean facingLeft = true;
    private float stateTime = 0f;

    // === Скорострельность ===
    private float fireCooldown = 0.4f;    // время между выстрелами
    private float fireRateTimer = 0f;     // таймер между выстрелами
    private float minFireCooldown = 0.1f; // минимальный лимит

    // === Анимации ===
    private Texture[] flyTextures;
    private Texture[] attackTextures;
    private Animation<TextureRegion> flyAnim;
    private Animation<TextureRegion> attackAnim;
    private TextureRegion currentFrame;
    private TextureRegion idleFrame;
    private boolean isAttacking = false;

    // === ХП ===
    private int maxHP = 100;
    private int hp = maxHP;
    private ShapeRenderer hpBar = new ShapeRenderer();

    // === Файрболы ===
    private ArrayList<Fireball> fireballs = new ArrayList<>();

    // === Конструктор ===
    public Wizard() {
        // === Анимация полёта ===
        flyTextures = new Texture[]{
            new Texture("wizard_fly_1.png"),
            new Texture("wizard_fly_2.png"),
            new Texture("wizard_fly_3.png"),
            new Texture("wizard_fly_4.png"),
            new Texture("wizard_fly_5.png"),
            new Texture("wizard_fly_6.png")
        };

        TextureRegion[] flyFrames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            flyFrames[i] = new TextureRegion(flyTextures[i]);
        }

        flyAnim = new Animation<>(0.12f, flyFrames);
        idleFrame = flyFrames[1];
        currentFrame = idleFrame;

        // === Анимация атаки ===
        attackTextures = new Texture[]{
            new Texture("wizard_atac_1.png"),
            new Texture("wizard_atac_2.png"),
            new Texture("wizard_atac_3.png"),
            new Texture("wizard_atac_4.png"),
            new Texture("wizard_atac_5.png"),
            new Texture("wizard_atac_6.png")
        };

        TextureRegion[] attackFrames = new TextureRegion[attackTextures.length];
        for (int i = 0; i < attackTextures.length; i++) {
            attackFrames[i] = new TextureRegion(attackTextures[i]);
        }

        attackAnim = new Animation<>(0.05f, attackFrames);
        attackAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    // === Главная логика ===
    public void update(float delta, ArrayList<Rectangle> walls) {
        stateTime += delta;
        boolean moving = false;

        float newX = x;
        float newY = y;

// --- движение ---
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { newY += speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { newY -= speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { newX -= speed * delta; facingLeft = true; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { newX += speed * delta; facingLeft = false; moving = true; }

// --- применяем только если нет коллизии ---
        if (!collides(newX, newY, walls)) {
            x = newX;
            y = newY;
        }


        // === Стрельба ===
        fireRateTimer -= delta;

        float dirX = 0;
        float dirY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dirY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dirY = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dirX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dirX = 1;

        // если стоит — стреляет по направлению взгляда
        if (dirX == 0 && dirY == 0) {
            dirX = facingLeft ? -1 : 1;
        }

        // === создаём выстрел ===
        if (fireRateTimer <= 0f) {
            shoot(dirX, dirY);
            fireRateTimer = fireCooldown;

            // включаем анимацию атаки только если стоим
            if (!moving) {
                isAttacking = true;
                stateTime = 0;
            }
        }

        // === Обновление анимации ===
        if (isAttacking) {
            currentFrame = attackAnim.getKeyFrame(stateTime);
            if (attackAnim.isAnimationFinished(stateTime)) {
                isAttacking = false;
                stateTime = 0;
            }
        } else {
            if (moving) {
                // показываем последний кадр анимации полёта (замороженная поза)
                TextureRegion[] frames = flyAnim.getKeyFrames();
                currentFrame = frames[frames.length - 1];
            } else {
                // стоит — idle
                currentFrame = idleFrame;
            }
        }

        // === Обновляем файрболы ===
        for (int i = 0; i < fireballs.size(); i++) {
            Fireball f = fireballs.get(i);
            f.update(delta);
            if (!f.isActive()) {
                fireballs.remove(i);
                i--;
            }
        }
    }



    // === Создание выстрела ===
    private void shoot(float dirX, float dirY) {
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len != 0) {
            dirX /= len;
            dirY /= len;
        }

        if (dirX != 0) {
            facingLeft = dirX < 0;
        }

        fireballs.add(new Fireball(
            x + (facingLeft ? -10 : 80),
            y + 35,
            dirX, dirY
        ));
    }

    // === Повышение скорости стрельбы ===
    public void increaseFireRate(float amount) {
        fireCooldown -= amount;
        if (fireCooldown < minFireCooldown)
            fireCooldown = minFireCooldown;
    }

    // === Отрисовка ===
    public void draw(SpriteBatch batch) {
        if (facingLeft)
            batch.draw(currentFrame, x, y, 96, 96);
        else
            batch.draw(currentFrame, x + 96, y, -96, 96);

        for (Fireball f : fireballs) {
            f.draw(batch);
        }
    }

    // === стены ===
    private boolean collides(float newX, float newY, ArrayList<Rectangle> walls) {
        Rectangle future = new Rectangle(newX, newY, 64, 64); // примерный размер визарда
        for (Rectangle wall : walls) {
            if (future.overlaps(wall)) {
                return true;
            }
        }
        return false;
    }


    // === Полоска здоровья ===
    public void drawHP(OrthographicCamera camera) {
        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 60;
        float barHeight = 6;
        float barX = x + 18;
        float barY = y + 90;

        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }

    // === Получение урона ===
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    // === Геттеры ===
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }
    public ArrayList<Fireball> getFireballs() { return fireballs; }

    // === Очистка ресурсов ===
    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        hpBar.dispose();
        for (Fireball f : fireballs) f.dispose();
    }
}
