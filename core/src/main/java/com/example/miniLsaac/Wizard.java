package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 * Clase principal que representa al jugador (el mago).
 * <p>
 * Controla el movimiento, disparos, animaciones, experiencia, mejoras,
 * daño, salud y muerte del personaje.
 * </p>
 *
 * <h3>Funciones principales:</h3>
 * <ul>
 *     <li>Movimiento controlado por teclado (WASD).</li>
 *     <li>Disparo automático en la dirección actual.</li>
 *     <li>Animaciones de vuelo, ataque y muerte.</li>
 *     <li>Sistema de experiencia, niveles y mejoras.</li>
 *     <li>Gestión de colisiones con muros.</li>
 * </ul>
 */
public class Wizard {

    // === Posición y movimiento ===

    /** Posición X del mago. */
    private float x = 400;

    /** Posición Y del mago. */
    private float y = 400;

    /** Velocidad base de movimiento. */
    private float speed = 150;

    /** Indica si el mago mira hacia la izquierda. */
    private boolean facingLeft = true;

    /** Tiempo acumulado para actualizar animaciones. */
    private float stateTime = 0f;


    // === Disparo ===

    /** Tiempo entre disparos (en segundos). */
    private float fireCooldown = 0.4f;

    /** Temporizador interno para controlar la cadencia de disparo. */
    private float fireRateTimer = 0f;

    /** Límite mínimo del tiempo entre disparos. */
    private float minFireCooldown = 0.1f;


    // === Daño ===

    /** Daño base de cada bola de fuego. */
    private int baseDamage = 20;

    /** Multiplicador del daño (aumenta con las mejoras). */
    private float damageMultiplier = 1f;


    // === Experiencia y nivel ===

    /** Nivel actual del jugador. */
    private int level = 1;

    /** Experiencia actual. */
    private int exp = 0;

    /** Experiencia necesaria para el siguiente nivel. */
    private int expToNext = 100;

    /** Límite máximo de experiencia. */
    private int maxExp = 9999;


    // === Animaciones ===

    private Texture[] flyTextures;
    private Texture[] deathTextures;
    private Texture[] attackTextures;
    private Animation<TextureRegion> flyAnim;
    private Animation<TextureRegion> deathAnim;
    private Animation<TextureRegion> attackAnim;
    private TextureRegion currentFrame;
    private TextureRegion idleFrame;

    /** Indica si el jugador está atacando. */
    private boolean isAttacking = false;

    /** Indica si se está reproduciendo la animación de muerte. */
    private boolean isDying = false;

    /** Indica si el jugador está completamente muerto. */
    private boolean isDead = false;

    /** Tiempo transcurrido en la animación de muerte. */
    private float deathTime = 0f;


    // === Salud (HP) ===

    /** Salud máxima del jugador. */
    private int maxHP = 100;

    /** Salud actual. */
    private int hp = maxHP;

    /** Renderizador usado para dibujar la barra de salud. */
    private ShapeRenderer hpBar = new ShapeRenderer();

    /** Para logica de generacion de HP */
    // === regeneration ===
    private boolean hasRegeneration = false;
    private float regenRate = 1f;       // HP recupera en  1 cantidac por segundo
    private float regenTimer = 0f;



    // === Proyectiles ===

    /** Lista de todas las bolas de fuego activas. */
    private ArrayList<Fireball> fireballs = new ArrayList<>();


    /**
     * Constructor del jugador.
     * Inicializa las animaciones (vuelo, ataque y muerte) y define los valores iniciales.
     */
    public Wizard() {
        // Animación de vuelo
        flyTextures = new Texture[]{
            new Texture("wizard_fly_1.png"),
            new Texture("wizard_fly_2.png"),
            new Texture("wizard_fly_3.png"),
            new Texture("wizard_fly_4.png"),
            new Texture("wizard_fly_5.png"),
            new Texture("wizard_fly_6.png")
        };

        TextureRegion[] flyFrames = new TextureRegion[flyTextures.length];
        for (int i = 0; i < flyTextures.length; i++) {
            flyFrames[i] = new TextureRegion(flyTextures[i]);
        }

        flyAnim = new Animation<>(0.12f, flyFrames);
        idleFrame = flyFrames[1];
        currentFrame = idleFrame;

        // Animación de ataque
        attackTextures = new Texture[]{
            new Texture("wizard_atac_1.png"),
            new Texture("wizard_atac_2.png"),
            new Texture("wizard_atac_3.png"),
            new Texture("wizard_atac_4.png"),
            new Texture("wizard_atac_5.png"),
            new Texture("wizard_atac_6.png")
        };

        TextureRegion[] attackFrames = new TextureRegion[attackTextures.length];
        for (int i = 0; i < attackTextures.length; i++) {
            attackFrames[i] = new TextureRegion(attackTextures[i]);
        }

        attackAnim = new Animation<>(0.05f, attackFrames);
        attackAnim.setPlayMode(Animation.PlayMode.NORMAL);

        // Animación de muerte
        deathTextures = new Texture[]{
            new Texture(Gdx.files.internal("wizard_death_1.png")),
            new Texture(Gdx.files.internal("wizard_death_2.png")),
            new Texture(Gdx.files.internal("wizard_death_3.png")),
            new Texture(Gdx.files.internal("wizard_death_4.png")),
            new Texture(Gdx.files.internal("wizard_death_5.png")),
            new Texture(Gdx.files.internal("wizard_death_6.png")),
            new Texture(Gdx.files.internal("wizard_death_7.png")),
            new Texture(Gdx.files.internal("wizard_death_8.png"))
        };

        TextureRegion[] deathFrames = new TextureRegion[deathTextures.length];
        for (int i = 0; i < deathTextures.length; i++) {
            deathFrames[i] = new TextureRegion(deathTextures[i]);
        }

        deathAnim = new Animation<>(0.1f, deathFrames);
        deathAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    /**
     * Actualiza el estado del jugador (movimiento, animaciones, disparo y colisiones).
     *
     * @param delta tiempo transcurrido desde el último frame.
     * @param walls lista de rectángulos que representan las paredes del mapa.
     */
    public void update(float delta, ArrayList<Rectangle> walls) {
        stateTime += delta;
        boolean moving = false;
        float newX = x, newY = y;

        // Si el jugador está muriendo, solo actualiza la animación
        if (isDying) {
            deathTime += delta;
            currentFrame = deathAnim.getKeyFrame(deathTime);
            if (deathAnim.isAnimationFinished(deathTime)) {
                isDead = true;
            }
            return;
        }

        // Movimiento básico (WASD)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { newY += speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { newY -= speed * delta; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { newX -= speed * delta; facingLeft = true; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { newX += speed * delta; facingLeft = false; moving = true; }

        // Verifica colisiones antes de aplicar el movimiento
        if (!collides(newX, newY, walls)) {
            x = newX;
            y = newY;
        }

        // Control de disparos
        fireRateTimer -= delta;
        float dirX = 0, dirY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dirY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dirY = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dirX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dirX = 1;
        if (dirX == 0 && dirY == 0) dirX = facingLeft ? -1 : 1;

        if (fireRateTimer <= 0f) {
            shoot(dirX, dirY);
            fireRateTimer = fireCooldown;
            if (!moving) {
                isAttacking = true;
                stateTime = 0;
            }
        }

        // Actualiza animaciones
        if (isAttacking) {
            currentFrame = attackAnim.getKeyFrame(stateTime);
            if (attackAnim.isAnimationFinished(stateTime)) {
                isAttacking = false;
                stateTime = 0;
            }
        } else {
            currentFrame = moving ? flyAnim.getKeyFrames()[flyAnim.getKeyFrames().length - 1] : idleFrame;
        }

        // Actualiza las bolas de fuego
        for (int i = 0; i < fireballs.size(); i++) {
            Fireball f = fireballs.get(i);
            f.update(delta);
            if (!f.isActive()) {
                fireballs.remove(i);
                i--;
            }
        }

        // === Logica de regeneracion ===
        if (hasRegeneration && hp < maxHP) {
            regenTimer += delta;
            if (regenTimer >= 1f) { // cada segundo
                hp += regenRate;
                if (hp > maxHP) hp = maxHP;
                regenTimer = 0f;
            }
        }
    }

    /**
     * Crea una nueva bola de fuego en la dirección indicada.
     *
     * @param dirX dirección horizontal.
     * @param dirY dirección vertical.
     */
    private void shoot(float dirX, float dirY) {
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        int dmg = Math.round(baseDamage * damageMultiplier);
        if (len != 0) {
            dirX /= len;
            dirY /= len;
        }

        if (dirX != 0) facingLeft = dirX < 0;

        fireballs.add(new Fireball(
            x + (facingLeft ? -10 : 80),
            y + 35,
            dirX, dirY, dmg
        ));
    }

    /**
     * Dibuja el sprite del jugador y sus proyectiles.
     *
     * @param batch SpriteBatch usado para renderizar.
     */
    public void draw(SpriteBatch batch) {
        if (facingLeft)
            batch.draw(currentFrame, x, y, 96, 96);
        else
            batch.draw(currentFrame, x + 96, y, -96, 96);

        if (!isDead && !isDying)
            for (Fireball f : fireballs) f.draw(batch);
    }

    /**
     * Comprueba si el jugador colisiona con una pared.
     *
     * @param newX nueva posición X.
     * @param newY nueva posición Y.
     * @param walls lista de muros del mapa.
     * @return {@code true} si hay colisión, {@code false} si no.
     */
    private boolean collides(float newX, float newY, ArrayList<Rectangle> walls) {
        Rectangle future = new Rectangle(newX, newY, 64, 64);
        for (Rectangle wall : walls)
            if (future.overlaps(wall)) return true;
        return false;
    }

    /**
     * Dibuja la barra de salud del jugador.
     *
     * @param camera cámara ortográfica usada para la proyección.
     */
    public void drawHP(OrthographicCamera camera) {
        hpBar.setProjectionMatrix(camera.combined);
        hpBar.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 60, barHeight = 6;
        float barX = x + 18, barY = y + 90;

        hpBar.setColor(Color.DARK_GRAY);
        hpBar.rect(barX, barY, barWidth, barHeight);

        hpBar.setColor(Color.RED);
        float hpWidth = barWidth * ((float) hp / maxHP);
        hpBar.rect(barX, barY, hpWidth, barHeight);

        hpBar.end();
    }

    /**
     * Aplica daño al jugador.
     * Si la salud llega a 0, inicia la animación de muerte.
     *
     * @param dmg cantidad de daño recibido.
     */
    public void takeDamage(int dmg) {
        if (isDying || isDead) return;
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            startDeath();
        }
    }

    /**
     * Inicia la animación de muerte.
     */
    private void startDeath() {
        isDying = true;
        deathTime = 0f;
        currentFrame = deathAnim.getKeyFrame(0);
    }

    /**
     * Añade experiencia al jugador y verifica si debe subir de nivel.
     *
     * @param amount cantidad de experiencia obtenida.
     */
    public void addExperience(int amount) {
        exp += amount;
        if (exp >= expToNext) levelUp();
    }

    /**
     * Incrementa el nivel del jugador y abre el menú de mejoras.
     */
    private void levelUp() {
        exp -= expToNext;
        level++;
        expToNext = (int) (expToNext * 1.5f);
        GameScreen.getInstance().pauseForLevelUp(this);
    }

    /**
     * Incrementa el daño en un porcentaje.
     *
     * @param percent porcentaje de mejora (por ejemplo 0.2 = +20%).
     */
    public void increaseDamage(float percent) {
        damageMultiplier += percent;
    }

    /**
     * Incrementa la velocidad del jugador en un porcentaje.
     *
     * @param percent porcentaje de mejora.
     */
    public void increaseSpeed(float percent) {
        speed += speed * percent;
    }

    /**
     * Aumenta la cadencia de disparo (dispara más rápido).
     *
     * @param percent porcentaje de reducción del tiempo entre disparos.
     */
    public void increaseFireRate(float percent) {
        fireCooldown -= fireCooldown * percent;
        if (fireCooldown < 0.1f) fireCooldown = 0.1f;
    }
    public void heal(int amount) {
        hp += amount;
        if (hp > maxHP) hp = maxHP;
    }

    /**
     * Activa regeneratin de HP con cegundos indicados
     * @param rate cantidad de HP por segundo.
     */
    public void enableRegen(float rate) {
        hasRegeneration = true;
        regenRate += rate; // siempre suma la cantidad por segundo

    }

    // === Getters ===

    public float getX() { return x; }
    public float getY() { return y; }
    public float getCenterX() {return x + 48;}
    public float getCenterY() {return y + 48;}
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }
    public ArrayList<Fireball> getFireballs() { return fireballs; }
    public int getExp() { return exp; }
    public int getExpToNext() { return expToNext; }
    public boolean isDead() { return isDead; }
    public int getLevel() { return level; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public float getSpeed() { return speed; }

    /**
     * Libera los recursos gráficos (texturas y ShapeRenderer).
     */
    public void dispose() {
        for (Texture t : flyTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        for (Texture t : deathTextures) t.dispose();
        hpBar.dispose();
        for (Fireball f : fireballs) f.dispose();
    }
}

