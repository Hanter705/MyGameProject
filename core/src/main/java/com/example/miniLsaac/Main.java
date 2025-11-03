package com.example.miniLsaac;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Clase principal del juego.
 * <p>
 * Gestiona el ciclo de vida general de la aplicación, las pantallas (screens)
 * y el objeto principal {@link SpriteBatch} utilizado para renderizar gráficos 2D.
 * </p>
 *
 * <p>
 * Es la clase que se ejecuta primero cuando se inicia el juego y controla
 * la pantalla actual (por ejemplo: menú inicial, juego, pantalla de muerte, etc.).
 * </p>
 */
public class Main extends Game {

    /** Instancia única del juego (usada para cambiar pantallas desde cualquier clase). */
    public static Main instance;

    /** SpriteBatch global utilizado para dibujar elementos gráficos. */
    public SpriteBatch batch;

    /** Nombre del jugador actual (se establece desde StartScreen). */
    public static String playerName = "Unknown";

    /**
     * Método llamado al iniciar la aplicación.
     * <p>
     * Inicializa el SpriteBatch, guarda la instancia global del juego
     * y establece la primera pantalla (el menú inicial).
     * </p>
     */
    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();

        // Pantalla inicial del juego (menú de inicio)
        setScreen(new StartScreen());
    }

    /**
     * Llama automáticamente al método {@code render()} de la pantalla activa.
     * <p>
     * Este método se ejecuta continuamente en cada frame del juego.
     * </p>
     */
    @Override
    public void render() {
        super.render(); // llama a render() de la pantalla activa
    }

    /**
     * Libera los recursos gráficos cuando se cierra el juego.
     */
    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * Cambia la pantalla activa del juego.
     * <p>
     * Este método permite a cualquier clase cambiar de pantalla (por ejemplo,
     * pasar del juego al menú o a la pantalla de muerte) sin acceder directamente
     * al objeto {@link Main}.
     * </p>
     *
     * @param newScreen nueva pantalla que se mostrará
     */
    public static void switchScreen(com.badlogic.gdx.Screen newScreen) {
        if (instance != null) {
            instance.setScreen(newScreen);
        }
    }
}
