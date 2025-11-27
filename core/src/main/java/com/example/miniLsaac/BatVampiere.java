package com.example.miniLsaac;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Enemigo de tipo murciélago vampiro.
 * <p>
 * Este enemigo es más rápido que los murciélagos normales, pero tiene menos vida.
 * Otorga una cantidad moderada de experiencia al morir.
 * Utiliza un conjunto propio de texturas para su animación de vuelo.
 * </p>
 *
 * <h3>Características:</h3>
 * <ul>
 *     <li>Velocidad alta.</li>
 *     <li>Vida baja.</li>
 *     <li>Ataque más fuerte que el enemigo básico.</li>
 *     <li>Animación personalizada de vuelo.</li>
 * </ul>
 */
public class BatVampiere extends Enemy {

    /**
     * Crea un nuevo BatVampiere en una posición específica.
     *
     * @param x posición inicial en el eje X.
     * @param y posición inicial en el eje Y.
     */
    public BatVampiere(float x, float y) {
        super(x, y);

        this.maxHP = 80;
        this.hp = maxHP;
        this.speed = 90f;
        this.expDrop = 100;
        this.damage = 25;

        // Texturas de animación del murciélago vampiro
        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_4.png"))
        };

        // Cargar la animación con las texturas definidas
        loadAnimation();
    }
}
