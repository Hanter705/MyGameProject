package com.example.miniLsaac;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Clase que representa un texto flotante que aparece en pantalla por un tiempo limitado.
 * <p>
 * Este efecto se usa, por ejemplo, al recoger experiencia o al mostrar daño recibido.
 * El texto se eleva lentamente y se desvanece hasta desaparecer.
 * </p>
 */
public class FloatingText {

    /** Posición X del texto en pantalla. */
    private float x;

    /** Posición Y del texto en pantalla. */
    private float y;

    /** Contenido del texto a mostrar (por ejemplo, "+50 XP"). */
    private String text;

    /** Tiempo total de vida del texto (en segundos). */
    private float lifeTime = 1.0f;

    /** Tiempo transcurrido desde que apareció el texto. */
    private float elapsed = 0f;

    /** Color del texto (incluye transparencia al desvanecerse). */
    private Color color;

    /** Fuente utilizada para dibujar el texto. */
    private BitmapFont font;

    /**
     * Constructor del texto flotante.
     *
     * @param x posición X inicial del texto
     * @param y posición Y inicial del texto
     * @param text contenido del texto que se mostrará
     * @param color color base del texto
     */
    public FloatingText(float x, float y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = new Color(color);

        // Inicializa la fuente con el color indicado
        font = new BitmapFont();
        font.setColor(color);
        font.getData().setScale(1.3f);
    }

    /**
     * Actualiza la posición y transparencia del texto.
     * <p>
     * Cada frame:
     * <ul>
     *     <li>El texto sube lentamente hacia arriba.</li>
     *     <li>Su transparencia disminuye hasta desaparecer.</li>
     * </ul>
     * </p>
     *
     * @param delta tiempo transcurrido desde el último frame
     * @return {@code true} si el texto aún está activo,
     *         {@code false} si ya debe eliminarse.
     */
    public boolean update(float delta) {
        elapsed += delta;

        // Movimiento vertical: el texto se eleva poco a poco
        y += 30 * delta;

        // Cálculo de transparencia (alpha)
        float alpha = 1f - (elapsed / lifeTime);
        font.setColor(color.r, color.g, color.b, alpha);

        // Si ya pasó su tiempo de vida, se elimina
        return elapsed < lifeTime;
    }

    /**
     * Dibuja el texto flotante en pantalla.
     *
     * @param batch SpriteBatch utilizado para dibujar el texto
     */
    public void draw(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }

    /**
     * Libera los recursos utilizados por la fuente.
     * Es importante llamarlo al eliminar el objeto.
     */
    public void dispose() {
        font.dispose();
    }
}
