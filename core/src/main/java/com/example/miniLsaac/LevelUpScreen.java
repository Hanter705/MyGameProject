package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

/**
 * Pantalla que aparece cuando el jugador sube de nivel.
 * <p>
 * Permite elegir entre diferentes mejoras (upgrades) que modifican las estadísticas del personaje,
 * como daño, velocidad o frecuencia de disparo.
 * </p>
 */
public class LevelUpScreen implements Screen {

    /** Referencia al jugador actual (para aplicar las mejoras). */
    private Wizard player;

    /** Referencia a la pantalla principal del juego. */
    private GameScreen gameScreen;

    /** Objeto encargado de dibujar textos y gráficos. */
    private SpriteBatch batch;

    /** Fuente utilizada para renderizar el texto en pantalla. */
    private BitmapFont font;

    /** Indice de la opción actualmente seleccionada. */
    private int selectedOption = 0;

    /** Lista que contiene todas las mejoras disponibles. */
    private final ArrayList<Upgrade> upgrades = new ArrayList<>();

    /**
     * Constructor de la pantalla de subida de nivel.
     *
     * @param player referencia al jugador (para aplicar los efectos)
     * @param gameScreen referencia a la pantalla del juego (para reanudarlo tras elegir)
     */
    public LevelUpScreen(Wizard player, GameScreen gameScreen) {
        this.player = player;
        this.gameScreen = gameScreen;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        // === Mejora 1: Daño aumentado ===
        upgrades.add(new Upgrade(
            "Increase damage +20%",
            "Fireballs deal more damage",
            () -> player.increaseDamage(0.2f)
        ));

        // === Mejora 2: Velocidad de movimiento ===
        upgrades.add(new Upgrade(
            "Increase speed +10%",
            "The character moves faster",
            () -> player.increaseSpeed(0.1f)
        ));

        // === Mejora 3: Frecuencia de disparo ===
        upgrades.add(new Upgrade(
            "Rate of Fire +15%",
            "Fireballs are released more frequently",
            () -> player.increaseFireRate(0.15f)
        ));

        // === Mejora 4: Regeneración de salud ===
        upgrades.add(new Upgrade(
            "Regeneration +1 HP/sec",
            "Slowly regenerates health over time",
            () -> player.enableRegen(2f)
        ));


    }

    /**
     * Dibuja el menú de selección de mejoras y gestiona la navegación del jugador.
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    @Override
    public void render(float delta) {
        // Limpiar la pantalla con fondo negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // 🪄 Título principal
        font.setColor(Color.GOLD);
        font.draw(batch, "LEVEL UP!", centerX - 120, centerY + 150);

        // === Dibuja las opciones de mejora ===
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade up = upgrades.get(i);

            // Resalta la opción seleccionada en color cian
            if (i == selectedOption)
                font.setColor(Color.CYAN);
            else
                font.setColor(Color.WHITE);

            font.draw(batch, up.getName(), centerX - 220, centerY + 80 - i * 80);

            // Descripción de la mejora
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, up.getDescription(), centerX - 200, centerY + 50 - i * 80);
        }

        batch.end();

        // === Navegación con el teclado ===

        // Flecha ↑ — mover selección hacia arriba
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + upgrades.size()) % upgrades.size();
        }

        // Flecha ↓ — mover selección hacia abajo
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % upgrades.size();
        }

        // ENTER — aplicar la mejora seleccionada
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            upgrades.get(selectedOption).apply(); // ejecuta el efecto (Runnable)
            gameScreen.resumeAfterLevelUp(); // reanuda el juego
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera los recursos gráficos usados por la pantalla de mejoras.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
