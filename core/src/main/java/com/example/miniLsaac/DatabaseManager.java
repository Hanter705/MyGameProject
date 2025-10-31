package com.example.miniLsaac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/mygame"; // БД
    private static final String USER = "root"; // твой логин
    private static final String PASSWORD = ""; // пароль (если есть)

    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded!");

            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mygame?useSSL=false&serverTimezone=UTC",
                "root",
                ""
            );
            System.out.println("✅ Connected to MySQL database!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error connecting to DB: " + e.getMessage());
        }
    }



    public static void savePlayerData(String nickname, int level, int wave, int enemiesKilled) {
        connect();
        String sql = "INSERT INTO player_stats (nickname, level, wave, enemies_killed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nickname);
            stmt.setInt(2, level);
            stmt.setInt(3, wave);
            stmt.setInt(4, enemiesKilled);
            stmt.executeUpdate();
            System.out.println("💾 Player data saved to database!");
        } catch (SQLException e) {
            System.err.println("❌ Error saving data: " + e.getMessage());
        }
    }
}
