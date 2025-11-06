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

public class LeaderboardScreen implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;

    public LeaderboardScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        titleFont = new BitmapFont();
        font.getData().setScale(1.4f);
        titleFont.getData().setScale(2f);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // === Заголовок ===
        titleFont.setColor(Color.GOLD);
        layout.setText(titleFont, " LEADERBOARD ");
        titleFont.draw(batch, layout, (screenWidth - layout.width) / 2, screenHeight - 80);

        // === Заголовки таблицы ===
        font.setColor(Color.SKY);
        font.draw(batch, "RANK", 140, screenHeight - 150);
        font.draw(batch, "PLAYER", 240, screenHeight - 150);
        font.draw(batch, "LVL", 500, screenHeight - 150);
        font.draw(batch, "WAVE", 580, screenHeight - 150);
        font.draw(batch, "KILLS", 680, screenHeight - 150);
        font.draw(batch, "TIME (s)", 800, screenHeight - 150);

        // === Линия под заголовками ===
        font.setColor(Color.GRAY);
        font.draw(batch, "---------------------------------------------------------------------------------------------------------------", 120, screenHeight - 160);

        // === Вывод данных из БД ===
        font.setColor(Color.WHITE);
        int y = (int) (screenHeight - 200);
        try {
            if (DatabaseManager.getConnection() == null) DatabaseManager.connect();
            String sql = "SELECT nickname, level, wave, enemies_killed, timePlayed FROM player_stats ORDER BY wave DESC, level DESC LIMIT 5";
            PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("nickname");
                int lvl = rs.getInt("level");
                int wave = rs.getInt("wave");
                int kills = rs.getInt("enemies_killed");
                int time = rs.getInt("timePlayed");

                font.setColor(rank == 1 ? Color.GOLD : (rank == 2 ? Color.CYAN : (rank == 3 ? Color.FIREBRICK : Color.WHITE)));

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

        // === Подсказка ===
        font.setColor(Color.CYAN);
        font.draw(batch, "Press [M] to return to Menu", (screenWidth / 2f) - 160, 80);

        batch.end();

        // === Возврат в меню ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            Main.switchScreen(new StartScreen());
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
    }
}
