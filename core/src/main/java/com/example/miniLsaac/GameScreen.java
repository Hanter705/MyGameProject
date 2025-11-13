package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.ArrayList;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Clase principal del juego.
 * <p>
 * Esta clase controla todo el ciclo del juego:
 * - Renderiza el mapa, el jugador, los enemigos y la interfaz (HUD).
 * - Administra las oleadas de enemigos.
 * - Detecta colisiones, daño, muerte y subida de nivel.
 * - Coordina las pantallas de pausa, mejoras y muerte.
 * </p>
 */
public class GameScreen implements Screen {

    /** Instancia estática de GameScreen (singleton para acceso global). */
    private static GameScreen instance;

    /**
     * Devuelve la instancia activa de GameScreen.
     * @return la instancia actual del juego.
     */
    public static GameScreen getInstance() {
        return instance;
    }

    /** Constructor: define la instancia global del juego. */
    public GameScreen() {
        instance = this;
    }

    // === VARIABLES PRINCIPALES ===

    private SpriteBatch batch;              // se encarga de dibujar todos los sprites
    private Wizard player;                  // el jugador principal
    private ArrayList<Enemy> enemies;       // lista de enemigos activos

    private OrthographicCamera camera;      // cámara que sigue al jugador

    // === VARIABLES DEL MAPA ===
    private TiledMap map;                   // mapa .tmx cargado desde Tiled
    private OrthogonalTiledMapRenderer mapRenderer; // renderizador del mapa
    private ArrayList<Rectangle> walls;     // lista de muros para detectar colisiones
    private float playTime = 0f;            // tiempo de guego en segundos


    private float damageCooldown = 0f;      // tiempo de espera entre golpes recibidos
    private final float DAMAGE_INTERVAL = 0.5f; // intervalo mínimo entre golpes

    // === OLEADAS DE ENEMIGOS ===
    private float spawnTimer = 0f;          // cronómetro para generar nuevas oleadas
    private final float SPAWN_INTERVAL = 10f; // cada 10 segundos llega una nueva oleada
    private int waveNumber = 1;             // número actual de la oleada
    private int maxEnemies = 30;            // límite máximo de enemigos simultáneos
    private int enemiesKilled = 0;          // contador total de enemigos eliminados

    // === EXPERIENCIA ===
    private float expMultiplier = 1.0f;     // multiplicador de experiencia
    private final float EXP_GROWTH_PER_WAVE = 0.25f; // cada oleada da +25% más de exp




    private ArrayList<FloatingText> floatingTexts; // lista de textos flotantes (+50 XP)
    private boolean paused = false;         // si el juego está en pausa (por ejemplo, menú de mejora)
    private LevelUpScreen levelUpScreen;    // pantalla que aparece al subir de nivel

    // === INTERFAZ DE USUARIO (HUD) ===
    private ShapeRenderer uiShape;          // dibuja las barras (exp, hp)
    private BitmapFont uiFont;              // muestra texto sobre el HUD

    // === TAMAÑO DEL MAPA ===
    private int mapWidth, mapHeight, tileSize;
    private ArrayList<ExpOrb> expOrbs;      // lista de orbes de experiencia
    private ArrayList<HealPotion> healPotions;  // lista de botles heall


    /**
     * Se ejecuta cuando se muestra por primera vez esta pantalla.
     * Inicializa la cámara, el mapa, el jugador, enemigos y la interfaz del HUD.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();

        // Configuración de la cámara (zona visible del mundo)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Carga del mapa desde la carpeta assets/map/
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/sin nombre.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // Propiedades del mapa (ancho, alto, tamaño de tile)
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileSize = map.getProperties().get("tilewidth", Integer.class);

        // Carga de colisiones desde la capa collision del mapa
        walls = new ArrayList<>();
        MapLayer collisionLayer = map.getLayers().get("collision");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    walls.add(rect);
                }
            }
        }

        // Tiempo
        uiFont = new BitmapFont();
        uiFont.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        uiFont.getData().setScale(1.5f);


        // Creación del jugador y enemigos iniciales
        player = new Wizard();
        enemies = new ArrayList<>();
        healPotions = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            enemies.add(spawnRandomEnemy(player.getX(), player.getY(), mapWidth, mapHeight, tileSize, 250));
        }

        // Inicialización de listas del HUD y textos flotantes
        floatingTexts = new ArrayList<>();
        expOrbs = new ArrayList<>();
        uiShape = new ShapeRenderer();
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.5f);
    }

    /**
     * Método principal del juego: se ejecuta una vez por frame.
     * Controla actualizaciones, colisiones y renderizado.
     * @param delta tiempo (en segundos) transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Limpieza del fondo (azul oscuro)
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Si el juego no está en pausa, actualiza toda la lógica
        if (!paused) {

            playTime += delta; // teimer

            // Movimiento y ataques del jugador
            player.update(delta, walls);

            // Movimiento de enemigos
            for (Enemy enemy : enemies) {
                enemy.update(delta, player.getX(), player.getY(), enemies);
            }

            // Si el jugador muere → cambiar a la pantalla de muerte
            if (player.isDead()) {
                int seconds = (int) playTime;
                Main.switchScreen(new DeathScreen(player.getLevel(), waveNumber, enemiesKilled, seconds));
                return;
            }

            // Control del temporizador para nuevas oleadas
            spawnTimer += delta;

            if (spawnTimer >= SPAWN_INTERVAL) {
                spawnTimer = 0f;
                int enemiesToSpawn = 2 + waveNumber;

                for (int i = 0; i < enemiesToSpawn; i++) {
                    if (enemies.size() < maxEnemies) {
                        enemies.add(spawnRandomEnemy(
                            player.getX(), player.getY(),
                            mapWidth, mapHeight, tileSize,
                            250
                        ));
                    }
                }

                waveNumber++;
                System.out.println("🌊 Nueva oleada: " + waveNumber);
                expMultiplier += EXP_GROWTH_PER_WAVE;
            }




            // ⚔Comprobación de colisiones entre jugador y enemigos
            damageCooldown -= delta;
            for (Enemy enemy : enemies) {
                float dx = enemy.getX() - player.getX();
                float dy = enemy.getY() - player.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 40 && damageCooldown <= 0f) {
                    player.takeDamage(enemy.getDamage());
                    damageCooldown = DAMAGE_INTERVAL;
                }
            }

            // Colisiones de las bolas de fuego con enemigos
            for (int i = 0; i < player.getFireballs().size(); i++) {
                Fireball f = player.getFireballs().get(i);

                for (Enemy enemy : enemies) {
                    if (!enemy.isAlive()) continue;

                    float dx = f.getX() - enemy.getX();
                    float dy = f.getY() - enemy.getY();
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);

                    if (distance < 40) {
                        enemy.takeDamage(f.getDamage());
                        f.setActive(false);

                        // Si muere, crear un orbe de experiencia
                        if (!enemy.isAlive()) {
                            enemiesKilled++;
                            int expValue = Math.round(enemy.getExpDrop() * expMultiplier);
                            expOrbs.add(new ExpOrb(enemy.getX(), enemy.getY(), expValue));

                            if (Math.random() < 0.10) {
                                healPotions.add(new HealPotion(enemy.getX(), enemy.getY()));
                            }

                        }
                        break;
                    }
                }
            }

            // Eliminar proyectiles y enemigos inactivos
            player.getFireballs().removeIf(f -> !f.isActive());
            enemies.removeIf(e -> !e.isAlive());

            // Actualización y recolección de orbes de experiencia
            for (int i = 0; i < expOrbs.size(); i++) {
                ExpOrb orb = expOrbs.get(i);
                orb.update(player.getX(), player.getY());
                if (orb.isCollected()) {
                    int value = orb.getExpValue();
                    player.addExperience(value);

                    // Añadir texto flotante sobre el orbe
                    floatingTexts.add(new FloatingText(
                        orb.getX(), orb.getY() + 30,
                        "+" + value + " XP",
                        Color.GOLD,1
                    ));
                    expOrbs.remove(i);
                    i--;
                }
            }

            // ⬆Actualización de los textos flotantes (suben y se desvanecen)
            for (int i = 0; i < floatingTexts.size(); i++) {
                boolean alive = floatingTexts.get(i).update(delta);
                if (!alive) {
                    floatingTexts.remove(i);
                    i--;
                }
            }
        }

        // Movimiento suave de la cámara siguiendo al jugador
        camera.position.lerp(new Vector3(player.getX() + 48, player.getY() + 48, 0), 0.09f);
        camera.update();

        // Renderizado del mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Dibujo de todos los objetos del juego
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        for (Enemy enemy : enemies) enemy.draw(batch);
        for (ExpOrb orb : expOrbs) orb.draw(batch);
        for (FloatingText text : floatingTexts) text.draw(batch);
        batch.end();

        // Dibujo de barras de vida
        player.drawHP(camera);
        for (Enemy enemy : enemies) enemy.drawHP(camera);


        // Si está en pausa (menú de mejoras)
        if (paused && levelUpScreen != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            ShapeRenderer fade = new ShapeRenderer();
            fade.begin(ShapeRenderer.ShapeType.Filled);
            fade.setColor(0, 0, 0, 0.5f); // Fondo semitransparente
            fade.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            fade.end();
            fade.dispose();
            levelUpScreen.render(delta);
        }

        // Dibujo de la barra de experiencia
        int level = player.getLevel();
        int curExp = player.getExp();
        int nextExp = player.getExpToNext();
        float progress = nextExp > 0 ? (float) curExp / nextExp : 0f;
        if (progress > 1f) progress = 1f;

        float barWidth = Gdx.graphics.getWidth() * 0.6f;
        float barHeight = 20f;
        float barX = (Gdx.graphics.getWidth() - barWidth) / 2f;
        float barY = 20f;

        OrthographicCamera uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.setToOrtho(false);
        uiShape.setProjectionMatrix(uiCamera.combined);

        uiShape.begin(ShapeRenderer.ShapeType.Filled);
        uiShape.setColor(Color.DARK_GRAY);
        uiShape.rect(barX, barY, barWidth, barHeight);
        uiShape.setColor(new Color(1f, 0.9f - progress * 0.5f, 0.2f, 1f));
        uiShape.rect(barX, barY, barWidth * progress, barHeight);
        uiShape.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        uiFont.setColor(Color.WHITE);
        uiFont.draw(batch, "LVL " + level, barX, barY + barHeight + 25);
        uiFont.draw(batch, curExp + " / " + nextExp + " XP", barX + barWidth - 180, barY + barHeight + 25);
        batch.end();

        // === UI: Taimer con cordinatos ===
        OrthographicCamera uiCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCam.setToOrtho(false);
        batch.setProjectionMatrix(uiCam.combined);

        batch.begin();
        uiFont.draw(batch, " " + formatTime(playTime), 20, Gdx.graphics.getHeight() - 20);
        batch.end();


    }

    /** Pausa el juego y muestra la pantalla de selección de mejoras. */
    public void pauseForLevelUp(Wizard player) {
        paused = true;
        levelUpScreen = new LevelUpScreen(player, this);
    }

    /** Reanuda el juego después de seleccionar una mejora. */
    public void resumeAfterLevelUp() {
        paused = false;
        levelUpScreen = null;
    }

    /**
     * Genera un enemigo en una posición aleatoria del mapa,
     * asegurando que esté lejos del jugador.
     */
    private Enemy spawnRandomEnemy(float playerX, float playerY,
                                   int mapWidth, int mapHeight, int tileSize, float minDistance) {

        Random rand = new Random();
        float x, y;

        // Generar una posición alejada del jugador
        while (true) {
            x = rand.nextInt(mapWidth * tileSize - 100) + 50;
            y = rand.nextInt(mapHeight * tileSize - 100) + 50;

            float dx = x - playerX;
            float dy = y - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > minDistance)
                break;
        }

        // La selección de enemigos se realiza a través de EnemyFactory.
        return EnemyFactory.create(x, y, playTime);
    }


    /**
     * El tiempo en segundos pasa a formato MM:SS
     */
    private String formatTime(float timeInSeconds) {
        int totalSeconds = (int) timeInSeconds;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera todos los recursos del juego (sprites, fuentes, shapeRenderers, etc.).
     */
    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        for (Enemy enemy : enemies) enemy.dispose();
        for (ExpOrb orb : expOrbs) orb.dispose();
        for (FloatingText text : floatingTexts) text.dispose();
        uiShape.dispose();
        uiFont.dispose();
    }
}
