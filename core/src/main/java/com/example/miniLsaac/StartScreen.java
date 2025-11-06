package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Pantalla inicial del juego.
 * <p>
 * Permite al jugador ingresar su nombre y luego muestra el menú principal con dos opciones:
 * <ul>
 *     <li><b>Start Game</b> – Inicia la partida.</li>
 *     <li><b>Exit</b> – Cierra el juego.</li>
 * </ul>
 * </p>
 */
public class StartScreen implements Screen {

    /** Batch que se utiliza para dibujar texto e imágenes. */
    private SpriteBatch batch;

    /** Fuente utilizada para mostrar los textos del menú. */
    private BitmapFont font;

    /** Indice de la opción actualmente seleccionada en el menú. */
    private int selected = 0;

    /** Indica si el jugador ya ha ingresado su nombre. */
    private boolean nameEntered = false;

    /** Contiene el nombre del jugador ingresado carácter por carácter. */
    private StringBuilder playerName = new StringBuilder();

    /** Opciones disponibles en el menú principal. */
    private final String[] options = {"Start Game", "Leaderboard", "Exit"};

    /**
     * Se ejecuta cuando se muestra esta pantalla.
     * <p>
     * Aquí se inicializan los recursos gráficos (fuente y batch).
     * </p>
     */
    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    /**
     * Método principal que se ejecuta una vez por frame.
     * Dibuja el menú de inicio o la interfaz de ingreso de nombre.
     *
     * @param delta tiempo (en segundos) desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Limpieza del fondo (azul oscuro)
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Título del juego
        font.setColor(Color.GOLD);
        font.draw(batch, "MY GAME", centerX - 100, centerY + 150);

        // === Fase 1: Introducir nombre del jugador ===
        if (!nameEntered) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Enter your name:", centerX - 150, centerY + 30);

            // Cursor que parpadea para dar efecto de escritura
            String cursor = (System.currentTimeMillis() / 500 % 2 == 0) ? "_" : "";
            font.setColor(Color.CYAN);
            font.draw(batch, playerName.toString() + cursor, centerX - 150, centerY - 20);
        } else {
            // === Fase 2: Mostrar el menú principal ===
            for (int i = 0; i < options.length; i++) {
                if (i == selected)
                    font.setColor(Color.CYAN); // opción seleccionada
                else
                    font.setColor(Color.WHITE);

                font.draw(batch, options[i], centerX - 80, centerY - i * 60);
            }
        }

        batch.end();

        // === ENTRADA DE NOMBRE ===
        if (!nameEntered) {
            //  Entrada de letras (A-Z)
            for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
                if (Gdx.input.isKeyJustPressed(key) && playerName.length() < 12) {
                    char c = (char) ('A' + (key - Input.Keys.A));
                    if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                        c = Character.toLowerCase(c);
                    playerName.append(c);
                }
            }

            //  Espacio
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && playerName.length() < 12) {
                playerName.append(" ");
            }

            //  Borrar último carácter
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
                playerName.deleteCharAt(playerName.length() - 1);
            }

            //  Confirmar nombre con ENTER
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && playerName.length() > 0) {
                nameEntered = true;
                Main.playerName = playerName.toString(); // guardamos el nombre globalmente
            }

            return; // 🔹 No muestra el menú hasta que se ingrese el nombre
        }

        // === CONTROL DEL MENÚ ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + options.length) % options.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % options.length;
        }

        //  Confirmar opción seleccionada
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selected == 0) {
                // Iniciar el juego
                Main.switchScreen(new GameScreen());
            } else if (selected == 1) {
                // Tabla de liders
                Main.switchScreen(new LeaderboardScreen());
            }else if (selected == 2) {
                // Salida
                Gdx.app.exit();
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera los recursos gráficos utilizados por la pantalla.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
