package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * FireBat — enemigo volador de tipo fuego (hereda de Enemy).
 * <p>
 * Características especiales:
 * <ul>
 *     <li>Más rápido y resistente que los enemigos estándar.</li>
 *     <li>Otorga una mayor cantidad de experiencia al morir.</li>
 *     <li>Su sprite se invierte horizontalmente cuando se mueve hacia la izquierda.</li>
 * </ul>
 * </p>
 */
public class FireBat extends Enemy {

    /**
     * Constructor del FireBat.
     *
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     */
    public FireBat(float x, float y) {
        super(x, y); // llama al constructor de Enemy

        // parámetros individuales
        this.speed = 70f;
        this.maxHP = 220;
        this.hp = maxHP;
        this.damage = 35;
        this.expDrop = 200;

        // carga de texturas propias
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatFire_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatFire_Flying_4.png"))
        };

        TextureRegion[] frames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            frames[i] = new TextureRegion(flyTextures[i]);
        }

        // animación de vuelo
        flyAnim = new Animation<>(0.12f, frames);
        flyAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Actualiza el estado del FireBat:
     * <ul>
     *     <li>Movimiento general y lógica heredada de Enemy.</li>
     *     <li>Ajuste de dirección (mirar a izquierda o derecha).</li>
     * </ul>
     *
     * @param delta      tiempo entre frames.
     * @param playerX    posición X del jugador.
     * @param playerY    posición Y del jugador.
     * @param allEnemies lista de todos los enemigos activos.
     */
    @Override
    public void update(float delta, float playerX, float playerY, java.util.ArrayList<Enemy> allEnemies) {
        super.update(delta, playerX, playerY, allEnemies);

        // determinar dirección (izquierda o derecha)
        facingLeft = (playerX < x);
    }

    /**
     * Dibuja al FireBat usando la animación correspondiente.
     * <p>
     * - Obtiene el fotograma actual de la animación.
     * - Crea una copia del fotograma para evitar modificar el original.
     * - Invierte horizontalmente el sprite si el enemigo mira hacia la izquierda.
     * </p>
     *
     * @param batch SpriteBatch usado para renderizar el sprite.
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (flyAnim == null) return;

        if (currentFrame == null)
            currentFrame = flyAnim.getKeyFrame(0);
        else
            currentFrame = flyAnim.getKeyFrame(stateTime, true);

        // crear una copia del frame (para no modificar el original)
        TextureRegion frame = new TextureRegion(currentFrame);

        // inversión horizontal si va hacia la izquierda
        if (facingLeft && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!facingLeft && frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(frame, x, y, 64, 64);
    }

    /**
     * Libera los recursos gráficos de este enemigo.
     * <p>
     * Incluye las texturas propias y la limpieza general de Enemy.
     * </p>
     */
    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : flyTextures) t.dispose();
    }
}

