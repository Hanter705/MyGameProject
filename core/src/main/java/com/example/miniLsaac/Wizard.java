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
import java.util.ArrayList;


/**
 * Класс персонажа Wizard (визард)
 * Управляет: движением, анимациями (полёт / атака), здоровьем и направлением взгляда
 */
public class Wizard {
    // === Позиция и движение ===
    private float x = 100, y = 100;   // координаты персонажа
    private float speed = 150;        // скорость движения
    private boolean facingLeft = true; // по умолчанию смотрит влево
    private float stateTime = 0f;     // таймер для анимаций

    // === Анимации ===
    private Texture[] flyTextures;       // кадры полёта
    private Texture[] attackTextures;    // кадры атаки
    private Animation<TextureRegion> flyAnim;     // анимация полёта
    private Animation<TextureRegion> attackAnim;  // анимация атаки
    private TextureRegion currentFrame;  // текущий отображаемый кадр
    private TextureRegion idleFrame;     // кадр покоя

    // === ХП (здоровье) ===
    private int maxHP = 100;
    private int hp = maxHP;
    private ShapeRenderer hpBar = new ShapeRenderer(); // рисует полоску здоровья

    // === Состояния ===
    private boolean isFlying = false;    // движется ли персонаж
    private boolean isAttacking = false; // атакует ли персонаж

    private ArrayList<Fireball> fireballs = new ArrayList<>(); // список активных файрболов
    private float fireCooldown = 0f; // таймер между выстрелами


    // === Конструктор ===
    public Wizard() {
        // === Загружаем кадры полёта ===
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

        flyAnim = new Animation<>(0.12f, flyFrames);           // скорость смены кадров
        flyAnim.setPlayMode(Animation.PlayMode.NORMAL);        // не зацикливается
        idleFrame = flyFrames[1];                              // поза покоя
        currentFrame = idleFrame;

        // === Загружаем кадры атаки ===
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

        attackAnim = new Animation<>(0.04f, attackFrames);// скорасть анимации стрелбы
        attackAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    // === Главная логика персонажа ===
    public void update(float delta) {
        stateTime += delta;
        boolean moving = false;

        // --- движение ---
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed * delta; facingLeft = true; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed * delta; facingLeft = false; moving = true; }

        // --- запуск анимации атаки ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isAttacking) {
            isAttacking = true;
            stateTime = 0f;
        }

        // --- если идёт анимация атаки ---
        if (isAttacking) {
            currentFrame = attackAnim.getKeyFrame(stateTime);

            // Проверяем, закончилась ли анимация
            if (attackAnim.isAnimationFinished(stateTime)) {
                // === создаём файрбол ===
                // === Определяем направление выстрела ===
                // === Определяем направление выстрела ===
                float dirX = 0;
                float dirY = 0;

                // Проверяем клавиши направления
                boolean up = Gdx.input.isKeyPressed(Input.Keys.W);
                boolean down = Gdx.input.isKeyPressed(Input.Keys.S);
                boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
                boolean right = Gdx.input.isKeyPressed(Input.Keys.D);

            // --- строго вверх / вниз ---
                if (up && !left && !right) { dirX = 0; dirY = 1; }
                else if (down && !left && !right) { dirX = 0; dirY = -1; }

                // --- диагонали ---
                else if (up && right) { dirX = 1; dirY = 1; }
                else if (up && left) { dirX = -1; dirY = 1; }
                else if (down && right) { dirX = 1; dirY = -1; }
                else if (down && left) { dirX = -1; dirY = -1; }

                // --- если ни одна клавиша не нажата — стреляет по направлению взгляда ---
                else {
                    dirX = facingLeft ? -1 : 1;
                    dirY = 0;
                }

                // === создаём файрбол ===
                fireballs.add(new Fireball(
                    x + (facingLeft ? -20 : 90), // позиция появления
                    y + 40,                      // чуть выше центра
                    dirX, dirY                   // направление
                ));




                // сбрасываем состояние атаки
                isAttacking = false;
                stateTime = 0f;
            }
        }
        else { // если не атакует, обычная анимация полёта
            if (moving) {
                if (!isFlying) {
                    stateTime = 0f;
                    isFlying = true;
                }
                currentFrame = flyAnim.getKeyFrame(stateTime);
                if (flyAnim.isAnimationFinished(stateTime)) {
                    currentFrame = flyAnim.getKeyFrames()[flyAnim.getKeyFrames().length - 1];
                }
            } else {
                currentFrame = idleFrame;
                isFlying = false;
            }
        }

        // === обновляем файрболы ===
        for (int i = 0; i < fireballs.size(); i++) {
            Fireball f = fireballs.get(i);
            f.update(delta); // теперь обновляется каждый кадр
            if (!f.isActive()) {
                fireballs.remove(i);
                i--;
            }
        }
    }


    // === Отрисовка персонажа ===
    public void draw(SpriteBatch batch) {
        // если смотрит влево — рисуем как есть
        if (facingLeft)
            batch.draw(currentFrame, x, y, 96, 96);
        else // если направо — зеркалим по X
            batch.draw(currentFrame, x + 96, y, -96, 96);
        // Теперь рисуем файрболы
        for (Fireball f : fireballs) {
            f.draw(batch);
        }
    }

    // === Отрисовка полоски здоровья ===
    public void drawHP(OrthographicCamera camera) {
        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 60;
        float barHeight = 6;
        float barX = x + 18;
        float barY = y + 90;

        // фон
        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        // заполнение
        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }

    // === Логика урона ===
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    // === Геттеры для координат и HP ===
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }

    // === Очистка ресурсов ===
    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        hpBar.dispose();
        for (Fireball f : fireballs) {
            f.dispose();
        }
    }
    public ArrayList<Fireball> getFireballs() {
        return fireballs;
    }

}
