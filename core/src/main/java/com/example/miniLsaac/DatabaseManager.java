package com.example.miniLsaac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase encargada de la conexión y gestión de la base de datos MySQL.
 * <p>
 * Su función principal es conectar el juego con la base de datos local
 * y guardar las estadísticas del jugador (nivel, oleada, enemigos eliminados, etc.).
 * </p>
 *
 * <h3>Funciones principales:</h3>
 * <ul>
 *     <li>Conectar con la base de datos MySQL.</li>
 *     <li>Guardar los datos del jugador al morir o finalizar partida.</li>
 * </ul>
 *
 * <h3>Tabla esperada:</h3>
 * <pre>
 * CREATE TABLE player_stats (
 *     id INT AUTO_INCREMENT PRIMARY KEY,
 *     nickname VARCHAR(50),
 *     level INT,
 *     wave INT,
 *     enemies_killed INT,
 *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * </pre>
 */
public class DatabaseManager {

    /** URL de conexión a la base de datos MySQL. */
    private static final String URL = "jdbc:mysql://localhost:3306/mygame";
    /** Usuario de la base de datos (por defecto: root). */
    private static final String USER = "root";
    /** Contraseña del usuario (vacía si no se usa). */
    private static final String PASSWORD = "";

    /** Conexión activa a la base de datos. */
    private static Connection connection;

    /**
     * Conecta con la base de datos MySQL.
     * <p>
     * Carga el driver JDBC y establece la conexión usando los parámetros definidos.
     * Si ocurre un error, muestra un mensaje en la consola.
     * </p>
     */
    public static void connect() {
        try {
            // 🔌 Carga del driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded!");

            // Intento de conexión
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mygame?useSSL=false&serverTimezone=UTC",
                "root",
                ""
            );
            System.out.println("Connected to MySQL database!");
        } catch (ClassNotFoundException e) {
            // Error: driver JDBC no encontrado
            System.err.println("MySQL driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            // Error de conexión con la base de datos
            System.err.println("Error connecting to DB: " + e.getMessage());
        }
    }

    /**
     * Guarda los datos del jugador en la base de datos.
     * <p>
     * Inserta un nuevo registro en la tabla <code>player_stats</code> con:
     * nickname, nivel alcanzado, número de oleada y enemigos eliminados.
     * </p>
     *
     * @param nickname nombre del jugador (introducido en el inicio del juego)
     * @param level nivel alcanzado por el jugador
     * @param wave número de la última oleada alcanzada
     * @param enemies_killed cantidad total de enemigos eliminados
     */
    public static void savePlayerData(String nickname, int level, int wave, int enemies_killed, int timePlayed) {
        try {
            // asegura que el conecion ete activa
            if (connection == null) connect();

            // asigna SQL para insertar un nuevo registro
            String sql = "INSERT INTO player_stats (nickname, level, wave, enemies_killed, timePlayed) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            // asigna valores a los parametros de la consulta
            ps.setString(1, nickname);
            ps.setInt(2, level);
            ps.setInt(3, wave);
            ps.setInt(4, enemies_killed);
            ps.setInt(5, timePlayed);
            ps.executeUpdate();

            System.out.println("Player data saved!");
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    public static Connection getConnection() {
        return connection;
    }


}
