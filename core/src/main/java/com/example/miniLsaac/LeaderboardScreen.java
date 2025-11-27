package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.sql.*;

/**
 * Pantalla que muestra la tabla de clasificaciones (Leaderboard).
 * <p>
 * Recupera datos desde la base de datos y los dibuja en columnas:
 * <ul>
 *     <li>Posición del jugador (RANK)</li>
 *     <li>Nombre del jugador (PLAYER)</li>
 *     <li>Nivel alcanzado (LVL)</li>
 *     <li>Wave máxima alcanzada (WAVE)</li>
 *     <li>Enemigos eliminados (KILLS)</li>
 *     <li>Tiempo jugado (TIME)</li>
 * </ul>
 * También permite regresar al menú principal pulsando la tecla <b>[M]</b>.
 * </p>
 */
public class LeaderboardScreen implements Screen {

    /** Batch utilizado para dibujar texto e imágenes en pantalla. */
    private SpriteBatch batch;

    /** Fuente para los textos normales de la tabla. */
    private BitmapFont font;

    /** Fuente más grande para mostrar el título de la pantalla. */
    private BitmapFont titleFont;

    /** Objeto para medir texto y centrarlo correctamente. */
    private GlyphLayout layout;

    /**
     * Constructor: inicializa fuentes, batch y layout.
     */
    public LeaderboardScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        titleFont = new BitmapFont();
        font.getData().setScale(1.4f);
        titleFont.getData().setScale(2f);
        layout = new GlyphLayout();
    }

    /**
     * Método principal de la pantalla. Dibuja:
     * <ul>
     *     <li>El título "Leaderboard"</li>
     *     <li>Las columnas de la tabla</li>
     *     <li>Los registros obtenidos desde la base de datos</li>
     *     <li>Un mensaje para regresar al menú</li>
     * </ul>
     *
     * @param delta tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Fondo azul oscuro
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // === Título ===
        titleFont.setColor(Color.GOLD);
        layout.setText(titleFont, " LEADERBOARD ");
        titleFont.draw(batch, layout, (screenWidth - layout.width) / 2, screenHeight - 80);

        // === Encabezados de tabla ===
        font.setColor(Color.SKY);
        font.draw(batch, "RANK", 140, screenHeight - 150);
        font.draw(batch, "PLAYER", 240, screenHeight - 150);
        font.draw(batch, "LVL", 500, screenHeight - 150);
        font.draw(batch, "WAVE", 580, screenHeight - 150);
        font.draw(batch, "KILLS", 680, screenHeight - 150);
        font.draw(batch, "TIME (s)", 800, screenHeight - 150);

        // === Línea de separación ===
        font.setColor(Color.GRAY);
        font.draw(batch,
            "---------------------------------------------------------------------------------------------------------------",
            120, screenHeight - 160
        );

        // === Mostrar datos desde la base de datos ===
        font.setColor(Color.WHITE);
        int y = (int) (screenHeight - 200);

        try {
            if (DatabaseManager.getConnection() == null)
                DatabaseManager.connect();

            String sql =
                "SELECT nickname, level, wave, enemies_killed, timePlayed " +
                    "FROM player_stats " +
                    "ORDER BY wave DESC, level DESC LIMIT 5";

            PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int rank = 1;
            while (rs.next()) {

                String name = rs.getString("nickname");
                int lvl = rs.getInt("level");
                int wave = rs.getInt("wave");
                int kills = rs.getInt("enemies_killed");
                int time = rs.getInt("timePlayed");

                // Colores especiales según la posición
                font.setColor(
                    rank == 1 ? Color.GOLD :
                        rank == 2 ? Color.CYAN :
                            rank == 3 ? Color.FIREBRICK :
                                Color.WHITE
                );

                font.draw(batch, rank + ".", 150, y);
                font.draw(batch, name, 240, y);
                font.draw(batch, String.valueOf(lvl), 520, y);
                font.draw(batch, String.valueOf(wave), 600, y);
                font.draw(batch, String.valueOf(kills), 700, y);
                font.draw(batch, String.valueOf(time), 820, y);

                y -= 50;
                rank++;
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            font.setColor(Color.RED);
            font.draw(batch, " Error loading leaderboard: " + e.getMessage(), 150, y);
        }

        // === Mensaje de retorno al menú ===
        font.setColor(Color.CYAN);
        font.draw(batch, "Press [M] to return to Menu", (screenWidth / 2f) - 160, 80);

        batch.end();

        // === Volver al menú ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            Main.switchScreen(new StartScreen());
        }
    }

    /** No utilizado en esta pantalla. */
    @Override public void show() {}

    /** No utilizado en esta pantalla. */
    @Override public void resize(int width, int height) {}

    /** No utilizado en esta pantalla. */
    @Override public void pause() {}

    /** No utilizado en esta pantalla. */
    @Override public void resume() {}

    /** No utilizado en esta pantalla. */
    @Override public void hide() {}

    /**
     * Libera los recursos gráficos utilizados en esta pantalla.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
    }
}

