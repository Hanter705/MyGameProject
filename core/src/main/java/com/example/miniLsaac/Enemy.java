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

/**
 * Representa a un enemigo del juego.
 * <p>
 * Los enemigos vuelan hacia el jugador, tienen puntos de vida (HP),
 * se representan con animaciones y pueden ser destruidos por los ataques del jugador.
 * También muestran una barra de salud encima de su sprite.
 * </p>
 *
 * <h3>Comportamiento:</h3>
 * <ul>
 *     <li>Persiguen la posición del jugador.</li>
 *     <li>Evitan superponerse con otros enemigos (mediante una fuerza de repulsión).</li>
 *     <li>Mueren al recibir suficiente daño.</li>
 * </ul>
 */
public class Enemy {

    /** Texturas que componen la animación de vuelo del enemigo. */
    protected Texture[] flyTextures;

    /** Animación que controla los fotogramas de vuelo del enemigo. */
    protected  Animation<TextureRegion> flyAnim;
    protected boolean facingLeft = false;


    /** Fotograma actual mostrado en pantalla. */
    protected  TextureRegion currentFrame;

    /** Tiempo acumulado usado para avanzar la animación. */
    protected float stateTime;

    /** Indica si el enemigo sigue con vida. */
    protected boolean alive = true;

    /** Posición X del enemigo. */
    protected  float x;

    /** Posición Y del enemigo. */
    protected  float y;

    /** Velocidad de movimiento del enemigo. */
    protected float speed = 55f;

    /** Vida máxima del enemigo. */
    protected int maxHP = 120;

    /** Vida actual del enemigo. */
    protected int hp = maxHP;

    /** Daño y experiencia base (para heredar en subclases). */
    protected int damage = 18;   // daño que causa al jugador
    protected int expDrop = 90;  // experiencia que da al morir


    /** Objeto encargado de dibujar la barra de salud. */
    protected ShapeRenderer hpBar;

    /**
     * Crea un nuevo enemigo en una posición inicial.
     *
     * @param startX posición inicial en el eje X.
     * @param startY posición inicial en el eje Y.
     */
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

    /**
     * Actualiza el comportamiento del enemigo.
     * <ul>
     *     <li>Se mueve hacia el jugador.</li>
     *     <li>Evita colisiones con otros enemigos cercanos.</li>
     *     <li>Actualiza la animación de vuelo.</li>
     * </ul>
     *
     * @param delta tiempo transcurrido desde el último frame (en segundos).
     * @param playerX posición actual del jugador en el eje X.
     * @param playerY posición actual del jugador en el eje Y.
     * @param allEnemies lista de todos los enemigos (para manejar la separación entre ellos).
     */
    public void update(float delta, float playerX, float playerY, ArrayList<Enemy> allEnemies) {
        stateTime += delta;
        currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // --- Movimiento hacia el jugador ---
        float dx = playerX - x;
        float dy = playerY - y;
        float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);

        if (distanceToPlayer > 1) {
            x += (dx / distanceToPlayer) * speed * delta;
            y += (dy / distanceToPlayer) * speed * delta;
        }

        // --- Evita superposición entre enemigos ---
        float repelForce = 80f;
        float minDistance = 40f;

        for (Enemy other : allEnemies) {
            if (other == this) continue;

            float ox = other.x - this.x;
            float oy = other.y - this.y;
            float distance = (float) Math.sqrt(ox * ox + oy * oy);

            if (distance < minDistance && distance > 0.1f) {
                float repelX = (this.x - other.x) / distance;
                float repelY = (this.y - other.y) / distance;

                this.x += repelX * repelForce * delta;
                this.y += repelY * repelForce * delta;
            }
        }

        facingLeft = (playerX < x);

    }

    /**
     * Dibuja el enemigo en la pantalla.
     *
     * @param batch el {@link SpriteBatch} usado para renderizar el sprite.
     */
    public void draw(SpriteBatch batch) {

        if (flyAnim == null) return;

        if (currentFrame == null)
            currentFrame = flyAnim.getKeyFrame(0);

        currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // создаём копию, чтобы не портить оригинальный кадр
        TextureRegion frame = new TextureRegion(currentFrame);

        // если смотрит влево — флип по X
        if (facingLeft && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!facingLeft && frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(frame, x, y, 64, 64);
    }


    /**
     * Dibuja la barra de salud del enemigo sobre su sprite.
     *
     * @param camera la cámara ortográfica actual usada para ajustar las coordenadas.
     */
    public void drawHP(OrthographicCamera camera) {
        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 50;
        float barHeight = 5;
        float barX = x + 8;
        float barY = y + 60;

        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }

    /**
     * Indica si el enemigo sigue vivo.
     *
     * @return {@code true} si el enemigo está vivo, {@code false} si ha muerto.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Aplica daño al enemigo y lo marca como muerto si su vida llega a 0.
     *
     * @param dmg cantidad de daño recibido.
     */
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }
    protected void loadAnimation() {
        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }
        flyAnim = new Animation<>(0.12f, frames);
        flyAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Libera los recursos gráficos (texturas y ShapeRenderer).
     * Debe llamarse al eliminar al enemigo o cerrar el juego.
     */
    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        hpBar.dispose();
    }

    public int getExpDrop() {
        return expDrop;
    }
    public int getDamage() {
        return damage;
    }



    /**
     * Devuelve la posición X del enemigo.
     *
     * @return coordenada X.
     */
    public float getX() { return x; }

    /**
     * Devuelve la posición Y del enemigo.
     *
     * @return coordenada Y.
     */
    public float getY() { return y; }
}
