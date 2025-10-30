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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;




public class GameScreen implements Screen {

    private static GameScreen instance;

    public static GameScreen getInstance() {
        return instance;
    }

    public GameScreen() {
        instance = this;
    }

    private SpriteBatch batch;
    private Wizard player;
    private ArrayList<Enemy> enemies;
    private OrthographicCamera camera; // камера

    // === переменные для карты ===
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ArrayList<Rectangle> walls;

    private float damageCooldown = 0f;
    private final float DAMAGE_INTERVAL = 0.5f; // полсекунды между ударами


    // === переменные для волн врагов ===
    private float spawnTimer = 0f;          // таймер между волнами
    private final float SPAWN_INTERVAL = 10f; // каждые 5 секунд — новая волна
    private int waveNumber = 1;             // номер текущей волны
    private int maxEnemies = 30;            // общий лимит врагов на карте
    private int enemiesKilled = 0;

    private float expMultiplier = 1.0f;   // множитель опыта
    private final float EXP_GROWTH_PER_WAVE = 0.25f; // +% за каждую волну


    private ArrayList<FloatingText> floatingTexts;

    private boolean paused = false;
    private LevelUpScreen levelUpScreen;
    private ShapeRenderer uiShape;   //  UI (полоски, фоны)
    private BitmapFont uiFont;       // текст поверх UI





    // размеры карты
    private int mapWidth, mapHeight, tileSize;
    private ArrayList<ExpOrb> expOrbs; // список орбов опыта



    @Override
    public void show() {
        batch = new SpriteBatch();

        // === создаём камеру ===
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600); // размер “вида” камеры

        // === загружаем карту ===
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/sin nombre.tmx");  // путь к твоей карте
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f); // 1f — масштаб тайлов

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileSize = map.getProperties().get("tilewidth", Integer.class);


        // === стена ===
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


        // Создаём игрока
        player = new Wizard();
        // Создаём список врагов
        enemies = new ArrayList<>();

        for (int i = 0; i < 5; i++) { // например, 5 врагов
            enemies.add(spawnRandomEnemy(player.getX(), player.getY(), mapWidth, mapHeight, tileSize, 250));
        }
        // список орбов
        floatingTexts = new ArrayList<>();
        expOrbs = new ArrayList<>();

        uiShape = new ShapeRenderer();
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.5f);


    }

    @Override
    public void render(float delta) {
        // === очищаем экран ===
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // === если игра на паузе — не обновляем логику ===
        if (!paused) {

            // === обновляем игрока и врагов ===
            player.update(delta, walls);
            for (Enemy enemy : enemies) {
                enemy.update(delta, player.getX(), player.getY(), enemies);
            }

            if (player.isDead()) {
                Main.switchScreen(new DeathScreen(player.getLevel(), waveNumber, enemiesKilled));
                return;
            }

            // === периодический спавн врагов волнами ===
            spawnTimer += delta;

            if (spawnTimer >= SPAWN_INTERVAL) {
                spawnTimer = 0f;
                int enemiesToSpawn = 2 + waveNumber; // каждая волна сильнее

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
                System.out.println("🌊 Wave " + waveNumber + " Started!");
                // Увеличиваем опыт, выпадающий за врагов
                expMultiplier += EXP_GROWTH_PER_WAVE;
                System.out.println("💫 Exp enemy! x" + expMultiplier);

            }

            // === Проверка столкновений игрока с врагами ===
            damageCooldown -= delta;
            for (Enemy enemy : enemies) {
                float dx = enemy.getX() - player.getX();
                float dy = enemy.getY() - player.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 40 && damageCooldown <= 0f) {
                    player.takeDamage(10);
                    damageCooldown = DAMAGE_INTERVAL;
                }
            }

            // === Проверка попадания файрбола во врагов ===
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
                            // базовый опыт за врага
                            int baseExp = 50;
                            // умножаем на множитель волны
                            int expValue = Math.round(baseExp * expMultiplier);
                            // создаём орб с усиленным опытом
                            expOrbs.add(new ExpOrb(enemy.getX(), enemy.getY(), expValue));

                        }


                        break;
                    }
                }
            }
            player.getFireballs().removeIf(f -> !f.isActive());
            enemies.removeIf(e -> !e.isAlive());

            // === обновление и подбор EXP-орбов ===
            for (int i = 0; i < expOrbs.size(); i++) {
                ExpOrb orb = expOrbs.get(i);
                orb.update(player.getX(), player.getY());
                if (orb.isCollected()) {
                    int value = orb.getExpValue();
                    player.addExperience(value);

                    floatingTexts.add(new FloatingText(
                        orb.getX(), orb.getY() + 30,
                        "+" + value + " XP",
                        com.badlogic.gdx.graphics.Color.GOLD
                    ));

                    expOrbs.remove(i);
                    i--;
                }
            }

            // === обновление всплывающих текстов ===
            for (int i = 0; i < floatingTexts.size(); i++) {
                boolean alive = floatingTexts.get(i).update(delta);
                if (!alive) {
                    floatingTexts.remove(i);
                    i--;
                }
            }
        }

        // === камера ===
        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(player.getX() + 48, player.getY() + 48, 0),
            0.09f
        );
        camera.update();

        // === рисуем карту ===
        mapRenderer.setView(camera);
        mapRenderer.render();

        // === применяем камеру к SpriteBatch ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // === отрисовка всех спрайтов ===
        player.draw(batch);
        for (Enemy enemy : enemies) enemy.draw(batch);
        for (ExpOrb orb : expOrbs) orb.draw(batch);
        for (FloatingText text : floatingTexts) text.draw(batch);

        batch.end();

        // === отрисовка HP-шек ===
        player.drawHP(camera);
        for (Enemy enemy : enemies) enemy.drawHP(camera);

        // === если включён экран улучшений — показываем его поверх ===
        if (paused && levelUpScreen != null) {
            // рисуем затемнённый фон (эффект “паузы”)
            Gdx.gl.glEnable(GL20.GL_BLEND);
            ShapeRenderer fade = new ShapeRenderer();
            fade.begin(ShapeRenderer.ShapeType.Filled);
            fade.setColor(0, 0, 0, 0.5f); // чёрный с прозрачностью
            fade.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            fade.end();
            fade.dispose();

            // показываем меню улучшений
            levelUpScreen.render(delta);
        }
        // === === === EXP UI BAR === === ===
        int level = player.getLevel();
        int curExp = player.getExp();
        int nextExp = player.getExpToNext();

        // Защита от деления на 0
        float progress = nextExp > 0 ? (float)curExp / nextExp : 0f;
        if (progress > 1f) progress = 1f;

        // Размер и позиция полоски
        float barWidth = Gdx.graphics.getWidth() * 0.6f;
        float barHeight = 20f;
        float barX = (Gdx.graphics.getWidth() - barWidth) / 2f;
        float barY = 20f;

        // Камера для UI (фиксированная, чтобы HUD не двигался с игроком)
        OrthographicCamera uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.setToOrtho(false);
        uiShape.setProjectionMatrix(uiCamera.combined);

        // --- Рисуем саму полосу ---
        uiShape.begin(ShapeRenderer.ShapeType.Filled);

        // фон (тёмный)
        uiShape.setColor(Color.DARK_GRAY);
        uiShape.rect(barX, barY, barWidth, barHeight);

        // заполнение (золотое)
        uiShape.setColor(new Color(1f, 0.9f - progress * 0.5f, 0.2f, 1f));

        uiShape.rect(barX, barY, barWidth * progress, barHeight);

        uiShape.end();

        // --- Текст уровня и значения XP ---
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        uiFont.setColor(Color.WHITE);
        uiFont.draw(batch, "LVL " + level, barX, barY + barHeight + 25);
        uiFont.draw(batch, curExp + " / " + nextExp + " XP", barX + barWidth - 180, barY + barHeight + 25);

        batch.end();

    }

    public void pauseForLevelUp(Wizard player) {
        paused = true;
        levelUpScreen = new LevelUpScreen(player, this);
    }
    public void resumeAfterLevelUp() {
        paused = false;
        levelUpScreen = null;
    }




    private Enemy spawnRandomEnemy(float playerX, float playerY, int mapWidth, int mapHeight, int tileSize, float minDistance) {
        Random rand = new Random();

        float x, y;
        while (true) {
            // выбираем случайную точку внутри карты
            x = rand.nextInt(mapWidth * tileSize - 100) + 50;
            y = rand.nextInt(mapHeight * tileSize - 100) + 50;

            // считаем дистанцию до игрока
            float dx = x - playerX;
            float dy = y - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // если точка достаточно далеко — берём её
            if (distance > minDistance)
                break;
        }

        return new Enemy(x, y);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (ExpOrb orb : expOrbs) {
            orb.dispose();
        }
        for (FloatingText text : floatingTexts) {
            text.dispose();
        }
        uiShape.dispose();
        uiFont.dispose();

    }
}
