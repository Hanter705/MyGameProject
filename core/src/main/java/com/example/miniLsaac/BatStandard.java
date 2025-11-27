package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Enemigo básico de tipo murciélago.
 * <p>
 * Es el tipo de enemigo más débil del juego:
 * tiene poca vida, velocidad moderada y otorga una cantidad baja de experiencia.
 * Utiliza una animación simple basada en cuatro fotogramas.
 * </p>
 *
 * <h3>Características:</h3>
 * <ul>
 *     <li>Vida baja.</li>
 *     <li>Ataque débil.</li>
 *     <li>Velocidad media.</li>
 *     <li>Ideal como enemigo común en las primeras oleadas.</li>
 * </ul>
 */
public class BatStandard extends Enemy {

    /**
     * Crea un enemigo de tipo BatStandard en una posición específica.
     *
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     */
    public BatStandard(float x, float y) {
        super(x, y);

        this.maxHP = 40;
        this.hp = maxHP;
        this.speed = 75f;
        this.expDrop = 35;
        this.damage = 10;

        // Texturas específicas del murciélago estándar
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_4.png"))
        };

        // Cargar animación de vuelo
        loadAnimation();
    }
}
