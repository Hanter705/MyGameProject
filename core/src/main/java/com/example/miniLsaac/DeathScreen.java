package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Pantalla que aparece cuando el jugador muere.
 * <p>
 * Muestra la información final de la partida (nombre, nivel, oleada, enemigos eliminados)
 * y ofrece dos opciones:
 * <ul>
 *   <li>Guardar el progreso en la base de datos (tecla <b>S</b>).</li>
 *   <li>Reiniciar la partida (tecla <b>R</b>).</li>
 * </ul>
 * </p>
 */
public class DeathScreen implements Screen {

    /** Objeto que permite dibujar todos los textos en pantalla. */
    private SpriteBatch batch;

    /** Fuente usada para mostrar el texto. */
    private BitmapFont font;

    /** Nivel alcanzado por el jugador. */
    private int level;

    /** Número de oleada alcanzado. */
    private int wave;

    /** Cantidad total de enemigos eliminados. */
    private int enemiesKilled;

    /** Indica si el progreso ya fue guardado (para evitar duplicación). */
    private boolean saved = false;
    /** El tiempo de la partida. */
    private int timePlayed;

    /**
     * Constructor que inicializa la pantalla de muerte.
     *
     * @param level nivel alcanzado por el jugador
     * @param wave oleada alcanzada antes de morir
     * @param enemiesKilled número de enemigos eliminados
     */
    public DeathScreen(int level, int wave, int enemiesKilled, int timePlayed) {
        this.level = level;
        this.wave = wave;
        this.enemiesKilled = enemiesKilled;
        this.timePlayed = timePlayed;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    /**
     * Dibuja y actualiza el contenido de la pantalla en cada frame.
     * <p>
     * - Muestra la información final del jugador.<br>
     * - Permite guardar los datos presionando <b>S</b>.<br>
     * - Reinicia el juego con <b>R</b>.
     * </p>
     *
     * @param delta tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Limpieza del fondo (color negro)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Obtenemos el tamaño de la pantalla para centrar el texto
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();

        // Información que se muestra al morir
        String title = "YOU DIED";
        String nameText = "PLAYER: " + Main.playerName;
        String levelText = "LEVEL: " + level;
        String waveText = "WAVE: " + wave;
        String killsText = "ENEMIES KILLED: " + enemiesKilled;
        String timeText = "TIME: " + timePlayed + "s";

        // Mensaje de guardado
        String saveText = saved ? "SAVED SUCCESSFULLY!" : "Press [S] to Save Progress";
        String restartText = "Press [R] to Restart";
        String menuText = "Press [M] to Return to Menu";

        // === Dibujo de cada texto centrado ===

        // Título principal
        font.setColor(Color.RED);
        layout.setText(font, title);
        font.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight / 2 + 140);

        //  Nombre del jugador y estadísticas
        // === Dibujar todos los textos centrados ===
        font.setColor(Color.RED);
        layout.setText(font, title);
        font.draw(batch, title, (screenWidth - layout.width) / 2, screenHeight / 2 + 140);

        font.setColor(Color.GOLD);
        layout.setText(font, nameText);
        font.draw(batch, nameText, (screenWidth - layout.width) / 2, screenHeight / 2 + 100);

        layout.setText(font, levelText);
        font.draw(batch, levelText, (screenWidth - layout.width) / 2, screenHeight / 2 + 60);

        layout.setText(font, waveText);
        font.draw(batch, waveText, (screenWidth - layout.width) / 2, screenHeight / 2 + 20);

        layout.setText(font, killsText);
        font.draw(batch, killsText, (screenWidth - layout.width) / 2, screenHeight / 2 - 20);

        layout.setText(font, timeText);
        font.draw(batch, timeText, (screenWidth - layout.width) / 2, screenHeight / 2 - 60);

        // Texto de guardado
        font.setColor(saved ? Color.GREEN : Color.CYAN);
        layout.setText(font, saveText);
        font.draw(batch, saveText, (screenWidth - layout.width) / 2, screenHeight / 2 - 110);

        // Texto para reiniciar
        font.setColor(Color.WHITE);
        layout.setText(font, restartText);
        font.draw(batch, restartText, (screenWidth - layout.width) / 2, screenHeight / 2 - 160);

        // Texto para volver al menú
        font.setColor(Color.LIGHT_GRAY);
        layout.setText(font, menuText);
        font.draw(batch, menuText, (screenWidth - layout.width) / 2, screenHeight / 2 - 210);

        batch.end();

        // === CONTROL DE TECLAS ===

        //Guardar progreso (solo una vez)
        if (!saved && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            DatabaseManager.savePlayerData(Main.playerName, level, wave, enemiesKilled, timePlayed);
            saved = true;
        }

        // Reiniciar el juego
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Main.switchScreen(new GameScreen());
        }

        // Volver al menú principal
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            Main.switchScreen(new StartScreen());
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera los recursos utilizados por esta pantalla
     * (fuente y batch de dibujo).
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
