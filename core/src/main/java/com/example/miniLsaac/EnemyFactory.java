package com.example.miniLsaac;

import java.util.Random;

public class EnemyFactory {

    private static final Random rand = new Random();

    /**
     * Genera un enemigo según el tiempo de juego.
     *
     * @param x - x - posición
     * @param y - y - posición Y
     * @param playTime - tiempo en segundos
     * @return un objeto de la clase Enemy o sus subclases
     */
    public static Enemy create(float x, float y, float playTime) {

        // --- tiempo en minutos ---
        float m = playTime / 60f;

        int roll = rand.nextInt(100);

        // ---------- 0–1 minutos ----------
        if (m < 1f) {
            return new BatStandard(x, y); // solo basicos
        }

        // ---------- 1–2 minutos ----------
        if (m < 2f) {
            if (roll < 70) return new BatStandard(x, y);          // 70%
            else           return new BatAlbino(x, y);    // 30%
        }

        // ---------- 2–3 minutos ----------
        if (m < 3f) {
            if (roll < 50) return new BatStandard(x, y);          // 50%
            if (roll < 80) return new BatAlbino(x, y);    // 30%
            else           return new BatRoot(x, y);      // 20%
        }

        // ---------- 3–4 minutos ----------
        if (m < 4f) {
            if (roll < 30) return new BatStandard(x, y);    // 30%
            if (roll < 60) return new BatAlbino(x, y);      // 30%
            if (roll < 85) return new BatRoot(x, y);        // 25%
            else           return new Enemy(x, y);          // 15%
        }

        // ---------- 4–5 minutos ----------
        if (m < 5f) {
            if (roll < 35) return new BatAlbino(x, y);
            if (roll < 65) return new BatRoot(x, y);
            if (roll < 90) return new BatVampiere(x, y);
            else           return new Enemy(x, y);
        }

        // ---------- 5+ minutos ----------
        if (roll < 20) return new BatVampiere(x, y); // 20%
        if (roll < 50) return new BatRoot(x, y);     // 30%
        if (roll < 70) return new BatAlbino(x, y);   // 20%
        if (roll < 85) return new BatStandard(x, y); // 15%
        if (roll < 95) return new Enemy(x, y);       // 10%
        else           return new FireBat(x, y);     // 5% mini boss
    }
}
