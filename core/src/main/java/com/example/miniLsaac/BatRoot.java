package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Enemigo de tipo murciélago resistente.
 * <p>
 * Este enemigo posee más vida que los murciélagos comunes, pero
 * se mueve más lentamente. Es un enemigo intermedio que aparece
 * en oleadas más avanzadas.
 * </p>
 *
 * <h3>Características:</h3>
 * <ul>
 *     <li>Alta cantidad de vida.</li>
 *     <li>Velocidad baja.</li>
 *     <li>Daño moderado.</li>
 *     <li>Otorga experiencia media al morir.</li>
 * </ul>
 */
public class BatRoot extends Enemy {

    /**
     * Crea un enemigo de tipo BatRoot en una posición inicial.
     *
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     */
    public BatRoot(float x, float y) {
        super(x, y);

        this.maxHP = 110;
        this.hp = maxHP;
        this.speed = 45f;
        this.expDrop = 70;
        this.damage = 20;

        // Texturas de animación del murciélago resistente
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_4.png"))
        };

        // Inicializar animación
        loadAnimation();
    }
}
