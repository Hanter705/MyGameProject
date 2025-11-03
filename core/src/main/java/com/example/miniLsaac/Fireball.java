package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

/**
 * Representa un proyectil (bola de fuego) lanzado por el jugador.
 * <p>
 * Cada {@code Fireball} tiene una posición, dirección, velocidad y daño.
 * Se mueve en línea recta en la dirección indicada y desaparece
 * cuando sale del área visible del mapa o al impactar con un enemigo.
 * </p>
 */
public class Fireball {

    /** Posición X del proyectil. */
    private float x;

    /** Posición Y del proyectil. */
    private float y;

    /** Dirección horizontal del movimiento. */
    private float dx;

    /** Dirección vertical del movimiento. */
    private float dy;

    /** Velocidad del proyectil en píxeles por segundo. */
    private float speed = 400f;

    /** Indica si la bola de fuego sigue activa. */
    private boolean active = true;

    /** Daño que inflige este proyectil al impactar con un enemigo. */
    private int damage = 50;

    /** Textura utilizada para representar la bola de fuego. */
    private Texture texture;

    /** Sprite asociado a la textura para aplicar rotación y posición. */
    private Sprite sprite;

    /**
     * Crea un nuevo proyectil con daño personalizado.
     *
     * @param startX posición inicial X.
     * @param startY posición inicial Y.
     * @param dirX dirección horizontal (1 = derecha, -1 = izquierda).
     * @param dirY dirección vertical (1 = arriba, -1 = abajo).
     * @param damage cantidad de daño que inflige.
     */
    public Fireball(float startX, float startY, float dirX, float dirY, int damage) {
        this.x = startX;
        this.y = startY;
        this.dx = dirX;
        this.dy = dirY;
        this.damage = damage;

        // Normaliza la dirección
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len != 0) {
            dx /= len;
            dy /= len;
        }

        // === Selecciona textura según dirección ===
        if (dx < 180) {
            texture = new Texture("fier_ball_4_left.png"); // hacia la izquierda
        } else {
            texture = new Texture("fier_ball_4.png");      // hacia la derecha
        }

        sprite = new Sprite(texture);

        // Calcula el ángulo visual de la bola de fuego
        float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

        // Corrige la rotación (si el sprite base apunta hacia la derecha)
        sprite.setRotation(angle - 180);
    }

    /**
     * Crea una bola de fuego con el daño predeterminado (50).
     *
     * @param startX posición inicial X.
     * @param startY posición inicial Y.
     * @param dirX dirección horizontal.
     * @param dirY dirección vertical.
     */
    public Fireball(float startX, float startY, float dirX, float dirY) {
        this(startX, startY, dirX, dirY, 50); // usa daño por defecto
    }

    /**
     * Actualiza la posición del proyectil en función del tiempo transcurrido.
     * Si sale del área de juego, se desactiva automáticamente.
     *
     * @param delta tiempo transcurrido desde el último frame (en segundos).
     */
    public void update(float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;

        sprite.setPosition(x, y);

        // Desactiva si sale de los límites del mapa
        if (x < -2000 || x > 2000 || y < -2000 || y > 2000) {
            active = false;
        }
    }

    /**
     * Devuelve la cantidad de daño que inflige el proyectil.
     *
     * @return daño base de esta bola de fuego.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Dibuja la bola de fuego en pantalla.
     *
     * @param batch {@link SpriteBatch} usado para renderizar el sprite.
     */
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    /**
     * Indica si el proyectil sigue activo (no ha salido de pantalla ni ha impactado).
     *
     * @return {@code true} si está activo, {@code false} si se ha desactivado.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Obtiene la coordenada X actual del proyectil.
     *
     * @return valor X en píxeles.
     */
    public float getX() {
        return x;
    }

    /**
     * Obtiene la coordenada Y actual del proyectil.
     *
     * @return valor Y en píxeles.
     */
    public float getY() {
        return y;
    }

    /**
     * Establece manualmente si la bola de fuego está activa o no.
     *
     * @param value {@code true} para activar, {@code false} para desactivar.
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Libera la textura asociada a la bola de fuego.
     * Debe llamarse cuando el objeto deje de usarse.
     */
    public void dispose() {
        texture.dispose();
    }
}
