package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Enemigo de tipo murciélago albino.
 * <p>
 * Este enemigo es más rápido que los murciélagos estándar, pero
 * tiene una cantidad moderada de vida. Representa una amenaza ligera
 * pero ágil para el jugador.
 * </p>
 *
 * <h3>Características:</h3>
 * <ul>
 *     <li>Velocidad media-alta.</li>
 *     <li>Vida moderada.</li>
 *     <li>Daño medio.</li>
 *     <li>Otorga una cantidad moderada de experiencia.</li>
 * </ul>
 */
public class BatAlbino extends Enemy {

    /**
     * Crea un enemigo de tipo BatAlbino en una posición inicial.
     *
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     */
    public BatAlbino(float x, float y) {
        super(x, y);

        this.maxHP = 60;
        this.hp = maxHP;
        this.speed = 65f;
        this.expDrop = 50;
        this.damage = 15;

        // Texturas del murciélago albino
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_4.png"))
        };

        // Inicializar animación
        loadAnimation();
    }
}
