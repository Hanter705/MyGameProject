package com.example.miniLsaac;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Clase que representa el proyectil de hielo disparado por el jugador.
 * <p>
 * La IceBall se mueve hacia un objetivo, rota según su dirección y reproduce
 * una animación simple. Desaparece cuando sale de la pantalla o cuando impacta.
 * </p>
 */
public class IceBall {

    /** Posición X del proyectil. */
    private float x, y;

    /** Velocidad base del proyectil (incrementada por mejoras). */
    private float speed = 150f;  // un poco más rápido

    /** Indica si el proyectil sigue activo. */
    private boolean active = true;

    /** Dirección horizontal normalizada. */
    private float dirX;

    /** Dirección vertical normalizada. */
    private float dirY;

    /** Daño que inflige al impactar. */
    private int damage;

    /** Multiplicador de velocidad aplicado por mejoras. */
    private float speedMultiplier = 1f;

    /** Animación del proyectil. */
    private Animation<TextureRegion> anim;

    /** Tiempo acumulado para actualizar la animación. */
    private float stateTime = 0f;

    /** Angulo visual del proyectil basado en su dirección. */
    private float angle = 0f; // angulo de rotación del proyectil

    /** Ultimo fotograma de la animación (cuando termina). */
    private TextureRegion lastFrame;

    /**
     * Constructor de la IceBall.
     *
     * @param startX posición inicial X.
     * @param startY posición inicial Y.
     * @param targetX posición objetivo X.
     * @param targetY posición objetivo Y.
     * @param dmg daño del proyectil.
     * @param speedMultiplier multiplicador de velocidad según mejoras.
     */
    public IceBall(float startX, float startY, float targetX, float targetY, int dmg, float speedMultiplier) {
        this.x = startX;
        this.y = startY;
        this.damage = dmg;
        this.speedMultiplier = speedMultiplier;

        // La velocidad final depende de las mejoras
        speed *= speedMultiplier;

        // === Cargar fotogramas de animación ===
        Texture[] frames = new Texture[]{
            new Texture("atack/Fire_Bullet_1.png"),
            new Texture("atack/Fire_Bullet_2.png"),
            new Texture("atack/Fire_Bullet_3.png"),
            new Texture("atack/Fire_Bullet_4.png"),
        };

        TextureRegion[] regions = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            regions[i] = new TextureRegion(frames[i]);
        }

        anim = new Animation<>(0.1f, regions);
        anim.setPlayMode(Animation.PlayMode.NORMAL);  // punto importante
        lastFrame = regions[regions.length - 1];       // guardamos el último fotograma

        // --- Calcular dirección hacia el objetivo ---
        float dx = targetX - startX;
        float dy = targetY - startY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        if (len != 0) {
            dirX = dx / len;
            dirY = dy / len;
        }

        // === Angulo visual: convertir vector (dx, dy) en grados ===
        angle = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    /**
     * Actualiza la posición del proyectil y el tiempo de animación.
     *
     * @param delta tiempo transcurrido desde el último frame.
     */
    public void update(float delta) {
        if (!active) return;

        stateTime += delta;

        x += dirX * speed * delta;
        y += dirY * speed * delta;

        // Desactivar si sale muy lejos del área de juego
        if (x < -2000 || x > 2000 || y < -2000 || y > 2000)
            active = false;
    }

    /**
     * Dibuja la IceBall en pantalla con rotación.
     *
     * @param batch SpriteBatch utilizado para dibujar.
     */
    public void draw(SpriteBatch batch) {
        if (!active) return;

        TextureRegion frame;

        // Si la animación terminó, usar el último fotograma
        if (anim.isAnimationFinished(stateTime)) {
            frame = lastFrame;
        } else {
            frame = anim.getKeyFrame(stateTime, false);
        }

        batch.draw(
            frame,
            x, y,           // posición
            12, 8,          // centro de rotación
            24, 16,         // tamaño
            1f, 1f,         // escala
            angle           // ángulo de rotación
        );
    }

    /**
     * @return true si el proyectil sigue activo, false si ya desapareció.
     */
    public boolean isActive() { return active; }

    /** Desactiva el proyectil manualmente. */
    public void deactivate() { active = false; }

    /** @return posición X del proyectil. */
    public float getX() { return x; }

    /** @return posición Y del proyectil. */
    public float getY() { return y; }

    /** @return daño infligido por el proyectil. */
    public int getDamage() { return damage; }

    /**
     * Libera las texturas cargadas por la animación.
     * Se debe llamar al cerrar el juego.
     */
    public void dispose() {
        for (TextureRegion region : anim.getKeyFrames())
            region.getTexture().dispose();
    }
}
