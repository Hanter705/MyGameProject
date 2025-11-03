package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Representa una orbe de experiencia (EXP) que aparece tras derrotar enemigos.
 * <p>
 * Cada orbe tiene un color y una animación dependiendo de la cantidad de experiencia que otorga.
 * Cuando el jugador se acerca, el orbe se ve atraído hacia él y desaparece al ser recogido.
 * </p>
 */
public class ExpOrb {

    /** Posición actual del orbe en el mapa. */
    private Vector2 position;

    /** Animación del orbe (rotación o brillo). */
    private Animation<TextureRegion> animation;

    /** Tiempo transcurrido desde el inicio de la animación. */
    private float stateTime = 0f;

    /** Indica si el orbe ya fue recogido por el jugador. */
    private boolean collected = false;

    /** Cantidad de experiencia que otorga este orbe. */
    private int expValue;

    /** Velocidad base de movimiento del orbe al acercarse al jugador. */
    private float speed = 80f;

    /**
     * Constructor del orbe de experiencia.
     *
     * @param x posición inicial en el eje X
     * @param y posición inicial en el eje Y
     * @param expValue cantidad de experiencia que otorga
     */
    public ExpOrb(float x, float y, int expValue) {
        this.position = new Vector2(x, y);
        this.expValue = expValue;

        Texture[] frames = new Texture[4];

        // 🎨 Selecciona el color del orbe según la cantidad de experiencia
        String prefix;
        if (expValue <= 20) prefix = "coin/spr_coin_azu";        // Azul — poco EXP
        else if (expValue <= 50) prefix = "coin/spr_coin_gri";   // Gris — medio
        else if (expValue <= 80) prefix = "coin/spr_coin_ama";   // Amarillo — alto
        else if (expValue <= 120) prefix = "coin/spr_coin_berd"; // Verde — raro
        else prefix = "coin/spr_coin_roj";                       // Rojo — épico

        // 🧩 Carga los 4 fotogramas de animación
        frames[0] = new Texture(Gdx.files.internal(prefix + "_1.png"));
        frames[1] = new Texture(Gdx.files.internal(prefix + "_2.png"));
        frames[2] = new Texture(Gdx.files.internal(prefix + "_3.png"));
        frames[3] = new Texture(Gdx.files.internal(prefix + "_4.png"));

        // Crea la animación que se repite continuamente
        TextureRegion[] regions = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            regions[i] = new TextureRegion(frames[i]);
        }
        animation = new Animation<>(0.3f, regions);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    /**
     * Actualiza la posición del orbe.
     * <p>
     * Si el jugador está cerca, el orbe se ve atraído hacia él con velocidad creciente.
     * Cuando la distancia es menor a 30 píxeles, se marca como recogido.
     * </p>
     *
     * @param playerX coordenada X del jugador
     * @param playerY coordenada Y del jugador
     */
    public void update(float playerX, float playerY) {
        if (collected) return;

        stateTime += Gdx.graphics.getDeltaTime();

        // Calcula la distancia al jugador
        float dx = playerX - position.x;
        float dy = playerY - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // 🔄 Si el jugador está a menos de 100 px, el orbe se mueve hacia él
        if (distance < 100f) {
            float pullSpeed = speed + (100 - distance) * 2;
            position.x += (dx / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
            position.y += (dy / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
        }

        // 💥 Si el jugador está lo suficientemente cerca, se recoge
        if (distance < 30f) {
            collected = true;
        }
    }

    /**
     * Dibuja el orbe en pantalla (si aún no fue recogido).
     *
     * @param batch SpriteBatch usado para renderizar el orbe
     */
    public void draw(SpriteBatch batch) {
        if (!collected) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x, position.y, 24, 24);
        }
    }

    /**
     * @return {@code true} si el orbe ya fue recogido, {@code false} si sigue activo.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * @return cantidad de experiencia que otorga este orbe.
     */
    public int getExpValue() {
        return expValue;
    }

    /**
     * Libera los recursos gráficos asociados a las texturas del orbe.
     */
    public void dispose() {
        for (TextureRegion region : animation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }

    /**
     * @return coordenada X actual del orbe.
     */
    public float getX() {
        return position.x;
    }

    /**
     * @return coordenada Y actual del orbe.
     */
    public float getY() {
        return position.y;
    }
}
