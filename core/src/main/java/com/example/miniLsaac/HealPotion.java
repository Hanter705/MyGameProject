package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Objeto: Botella de curación.
 * Funciona como un orbe de EXP: se extiende hacia el jugador,
 * desaparece al recogerla y cura.
 */
public class HealPotion {

    /** Posición de la botella */
    private Vector2 position;

    /** Textura (imagen de una botella) */
    private Texture texture;

    /** esta recogida o no  */
    private boolean collected = false;

    /** Cuánto HP restaura */
    private int healAmount = 30;

    /** Velocidad de tracción básica */
    private float speed = 80f;

    public HealPotion(float x, float y) {
        this.position = new Vector2(x, y);

        // imagen - establece tu camino
        this.texture = new Texture(Gdx.files.internal("items/botle_heal.png"));
    }

    /**
     * Lógica de atracción hacia el jugador (como orbe EXP)
     */
    public void update(float playerX, float playerY) {
        if (collected) return;

        float dx = playerX - position.x;
        float dy = playerY - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // si el jugador está cerca - alcanza
        if (distance < 100f) {
            float pullSpeed = speed + (100 - distance) * 2;

            position.x += (dx / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
            position.y += (dy / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
        }

        // Si el jugador recoge
        if (distance < 30f) {
            collected = true;
        }
    }

    /**
     * renderizando una botella
     */
    public void draw(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, position.x, position.y, 24, 24);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void dispose() {
        texture.dispose();
    }
}
