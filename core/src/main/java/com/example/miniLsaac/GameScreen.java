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
 * Pantalla principal del juego.
 * <p>
 * Controla todo el ciclo de partida:
 * <ul>
 *     <li>Renderizado del mapa y todos los elementos del juego</li>
 *     <li>Movimiento del jugador y enemigos</li>
 *     <li>Control de oleadas y aparición de enemigos</li>
 *     <li>Colisiones, daño, experiencia y subida de nivel</li>
 *     <li>Generación de orbes de experiencia y pociones</li>
 *     <li>Gestión del HUD, temporizador, barras de nivel y vida</li>
 * </ul>
 * También coordina pantallas como LevelUpScreen o DeathScreen.
 * </p>
 */
public class GameScreen implements Screen {

    /** Instancia única de GameScreen para acceso global (patrón Singleton). */
    private static GameScreen instance;

    /**
     * Devuelve la instancia actual de GameScreen.
     * @return instancia activa del juego.
     */
    public static GameScreen getInstance() {
        return instance;
    }

    /** Constructor: asigna la instancia global del juego. */
    public GameScreen() {
        instance = this;
    }

    // === VARIABLES PRINCIPALES DEL JUEGO ===

    /** SpriteBatch para dibujar todos los elementos. */
    private SpriteBatch batch;

    /** El jugador controlado por el usuario. */
    private Wizard player;

    /** Lista de enemigos activos en la partida. */
    private ArrayList<Enemy> enemies;

    /** Cámara principal que sigue al jugador. */
    private OrthographicCamera camera;

    // === MAPA Y COLISIONES ===

    /** Mapa en formato Tiled (.tmx). */
    private TiledMap map;

    /** Renderizador que dibuja el mapa en pantalla. */
    private OrthogonalTiledMapRenderer mapRenderer;

    /** Lista de rectángulos que representan paredes y colisiones. */
    private ArrayList<Rectangle> walls;

    /** Tiempo total jugado (en segundos). */
    private float playTime = 0f;

    /** Enfriamiento entre daños del jugador. */
    private float damageCooldown = 0f;

    /** Tiempo mínimo entre golpes recibidos. */
    private final float DAMAGE_INTERVAL = 0.5f;

    // === OLEADAS ===

    /** Tiempo transcurrido desde la última oleada. */
    private float spawnTimer = 0f;

    /** Intervalo entre oleadas (10 segundos). */
    private final float SPAWN_INTERVAL = 10f;

    /** Número de la oleada actual. */
    private int waveNumber = 1;

    /** Máximo de enemigos simultáneos permitidos. */
    private int maxEnemies = 30;

    /** Contador total de enemigos eliminados. */
    private int enemiesKilled = 0;

    // === EXPERIENCIA ===

    /** Multiplicador de experiencia ganado. */
    private float expMultiplier = 1.0f;

    /** Aumento del multiplicador por oleada. */
    private final float EXP_GROWTH_PER_WAVE = 0.25f;

    /** Lista de textos flotantes (daño, XP, etc.). */
    private ArrayList<FloatingText> floatingTexts;

    /** Indica si el juego está en pausa (por LevelUpScreen). */
    private boolean paused = false;

    /** Pantalla mostrada cuando el jugador sube de nivel. */
    private LevelUpScreen levelUpScreen;

    // === HUD E INTERFAZ ===

    /** Dibujador de barras y elementos del HUD. */
    private ShapeRenderer uiShape;

    /** Fuente usada para mostrar texto en la interfaz. */
    private BitmapFont uiFont;

    // === DATOS DEL MAPA ===
    private int mapWidth, mapHeight, tileSize;

    /** Lista de orbes de experiencia. */
    private ArrayList<ExpOrb> expOrbs;

    /** Lista de pociones de curación. */
    private ArrayList<HealPotion> healPotions;

    /**
     * Se ejecuta al mostrarse la pantalla por primera vez.
     * Inicializa mapa, cámara, jugador, enemigos, HUD y colisiones.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();

        // Configurar la cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Cargar mapa Tiled
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/sin nombre.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // Propiedades del mapa
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileSize = map.getProperties().get("tilewidth", Integer.class);

        // Cargar paredes desde la capa "collision"
        walls = new ArrayList<>();
        MapLayer collisionLayer = map.getLayers().get("collision");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    walls.add(((RectangleMapObject) object).getRectangle());
                }
            }
        }

        // Inicializar HUD
        uiFont = new BitmapFont();
        uiFont.setColor(Color.WHITE);
        uiFont.getData().setScale(1.5f);

        // Crear jugador
        player = new Wizard();

        // Enemigos iniciales
        enemies = new ArrayList<>();
        healPotions = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            enemies.add(spawnRandomEnemy(player.getX(), player.getY(), mapWidth, mapHeight, tileSize, 250));
        }

        floatingTexts = new ArrayList<>();
        expOrbs = new ArrayList<>();
        uiShape = new ShapeRenderer();
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.5f);
    }

    /**
     * Ciclo principal del juego: actualiza lógica y renderiza todo.
     * @param delta Tiempo desde el último frame.
     */
    @Override
    public void render(float delta) {

        // Fondo azul oscuro
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Si no está en pausa:
        if (!paused) {

            playTime += delta; // temporizador

            // Movimiento del jugador
            player.update(delta, walls);

            // Movimiento de cada enemigo
            for (Enemy enemy : enemies) {
                enemy.update(delta, player.getX(), player.getY(), enemies);
            }

            // Si el jugador muere → pantalla de muerte
            if (player.isDead()) {
                int seconds = (int) playTime;
                Main.switchScreen(new DeathScreen(player.getLevel(), waveNumber, enemiesKilled, seconds));
                return;
            }

            // Actualizar IceBalls
            for (IceBall ib : player.getIceBalls()) ib.update(delta);

            // Temporizador de oleadas
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
                expMultiplier += EXP_GROWTH_PER_WAVE;
            }

            // === Colisión: jugador recibe daño si un enemigo está cerca ===
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

            // === Colisiones de Fireball con enemigos ===
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

                        if (!enemy.isAlive()) {
                            enemiesKilled++;
                            int expValue = Math.round(enemy.getExpDrop() * expMultiplier);

                            expOrbs.add(new ExpOrb(enemy.getX(), enemy.getY(), expValue));

                            if (Math.random() < 0.05) {
                                healPotions.add(new HealPotion(enemy.getX(), enemy.getY()));
                            }
                        }
                        break;
                    }
                }
            }

            // === Colisiones de IceBall con enemigos ===
            for (int i = 0; i < player.getIceBalls().size(); i++) {
                IceBall ib = player.getIceBalls().get(i);

                for (Enemy enemy : enemies) {
                    if (!enemy.isAlive()) continue;

                    float dx = ib.getX() - enemy.getX();
                    float dy = ib.getY() - enemy.getY();

                    if (Math.sqrt(dx * dx + dy * dy) < 40) {

                        enemy.takeDamage(ib.getDamage());
                        ib.deactivate();

                        if (!enemy.isAlive()) {
                            enemiesKilled++;
                            int expValue = Math.round(enemy.getExpDrop() * expMultiplier);

                            expOrbs.add(new ExpOrb(enemy.getX(), enemy.getY(), expValue));

                            if (Math.random() < 0.05) {
                                healPotions.add(new HealPotion(enemy.getX(), enemy.getY()));
                            }
                        }
                    }
                }
            }

            // Eliminar Fireballs e IceBalls inactivos
            player.getFireballs().removeIf(f -> !f.isActive());
            player.getIceBalls().removeIf(b -> !b.isActive());

            // Eliminar enemigos muertos
            enemies.removeIf(e -> !e.isAlive());

            // === Recolección de orbes de experiencia ===
            for (int i = 0; i < expOrbs.size(); i++) {
                ExpOrb orb = expOrbs.get(i);

                orb.update(player.getCenterX(), player.getCenterY());

                if (orb.isCollected()) {

                    int value = orb.getExpValue();
                    player.addExperience(value);

                    floatingTexts.add(new FloatingText(
                        orb.getX(), orb.getY() + 30,
                        "+" + value + " XP",
                        Color.GOLD, 1
                    ));

                    expOrbs.remove(i);
                    i--;
                }
            }

            // === Recolección de pociones de curación ===
            for (int i = 0; i < healPotions.size(); i++) {
                HealPotion hp = healPotions.get(i);
                hp.update(player.getCenterX(), player.getCenterY());

                if (hp.isCollected()) {

                    player.heal(hp.getHealAmount());

                    floatingTexts.add(new FloatingText(
                        player.getX(), player.getY() + 40,
                        "+30 HP",
                        Color.GREEN, 1
                    ));

                    healPotions.remove(i);
                    i--;
                }
            }

            // === Actualización de textos flotantes ===
            for (int i = 0; i < floatingTexts.size(); i++) {
                boolean alive = floatingTexts.get(i).update(delta);
                if (!alive) {
                    floatingTexts.remove(i);
                    i--;
                }
            }
        }

        // === Movimiento suave de la cámara siguiendo al jugador ===
        camera.position.lerp(new Vector3(player.getX() + 48, player.getY() + 48, 0), 0.09f);
        camera.update();

        // === Renderizar mapa ===
        mapRenderer.setView(camera);
        mapRenderer.render();

        // === Dibujar objetos del juego ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        for (Enemy enemy : enemies) enemy.draw(batch);
        for (ExpOrb orb : expOrbs) orb.draw(batch);
        for (HealPotion hp : healPotions) hp.draw(batch);
        for (FloatingText text : floatingTexts) text.draw(batch);
        for (IceBall ib : player.getIceBalls()) ib.draw(batch);
        batch.end();

        // === Dibujar barras de vida ===
        player.drawHP(camera);
        for (Enemy enemy : enemies) enemy.drawHP(camera);

        // === Menú de mejoras si está en pausa ===
        if (paused && levelUpScreen != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            ShapeRenderer fade = new ShapeRenderer();
            fade.begin(ShapeRenderer.ShapeType.Filled);
            fade.setColor(0, 0, 0, 0.5f);
            fade.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            fade.end();
            fade.dispose();
            levelUpScreen.render(delta);
        }

        // === Barra de experiencia (HUD) ===
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

        // Texto del nivel y XP
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        uiFont.setColor(Color.WHITE);
        uiFont.draw(batch, "LVL " + level, barX, barY + barHeight + 25);
        uiFont.draw(batch, curExp + " / " + nextExp + " XP", barX + barWidth - 180, barY + barHeight + 25);
        batch.end();

        // === Temporizador (HUD) ===
        OrthographicCamera uiCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCam.setToOrtho(false);
        batch.setProjectionMatrix(uiCam.combined);

        batch.begin();
        uiFont.draw(batch, " " + formatTime(playTime), 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }

    /**
     * Pausa la partida y muestra la pantalla de selección de mejoras.
     * @param player jugador que ha subido de nivel.
     */
    public void pauseForLevelUp(Wizard player) {
        paused = true;
        levelUpScreen = new LevelUpScreen(player, this);
    }

    /**
     * Reanuda el juego tras elegir una mejora.
     */
    public void resumeAfterLevelUp() {
        paused = false;
        levelUpScreen = null;
    }

    /**
     * Genera un enemigo en posición aleatoria alejada del jugador.
     *
     * @param playerX posición X del jugador.
     * @param playerY posición Y del jugador.
     * @param mapWidth ancho del mapa en tiles.
     * @param mapHeight alto del mapa en tiles.
     * @param tileSize tamaño del tile.
     * @param minDistance distancia mínima desde el jugador.
     * @return enemigo generado.
     */
    private Enemy spawnRandomEnemy(float playerX, float playerY,
                                   int mapWidth, int mapHeight, int tileSize, float minDistance) {

        Random rand = new Random();
        float x, y;

        // Buscar posición lejana al jugador
        while (true) {
            x = rand.nextInt(mapWidth * tileSize - 100) + 50;
            y = rand.nextInt(mapHeight * tileSize - 100) + 50;

            float dx = x - playerX;
            float dy = y - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > minDistance)
                break;
        }

        // Crear enemigo mediante EnemyFactory
        return EnemyFactory.create(x, y, playTime);
    }

    /**
     * Encuentra al enemigo más cercano a una posición dada.
     *
     * @param x coordenada X.
     * @param y coordenada Y.
     * @return enemigo más cercano o null si no hay.
     */
    public Enemy findNearestEnemy(float x, float y) {
        Enemy best = null;
        float bestDist = Float.MAX_VALUE;

        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;

            float dx = e.getX() - x;
            float dy = e.getY() - y;
            float dist = dx * dx + dy * dy;

            if (dist < bestDist) {
                bestDist = dist;
                best = e;
            }
        }
        return best;
    }

    /** @return lista de enemigos vivos. */
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Convierte segundos en formato MM:SS.
     * @param timeInSeconds segundos totales.
     * @return tiempo formateado.
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
     * Libera recursos del mapa, jugador, enemigos y HUD.
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
