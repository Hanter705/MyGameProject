package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ExpOrb {
    private Vector2 position;
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private boolean collected = false;
    private int expValue;
    private float speed = 80f;

    public ExpOrb(float x, float y, int expValue) {
        this.position = new Vector2(x, y);
        this.expValue = expValue;

        Texture[] frames = new Texture[4];

        // 🎨 Выбираем цвет по количеству опыта
        String prefix;
        if (expValue <= 20) prefix = "coin/spr_coin_azu";       // синий — маленький
        else if (expValue <= 50) prefix = "coin/spr_coin_gri";  // серый — средний
        else if (expValue <= 80) prefix = "coin/spr_coin_ama";  // жёлтый — крупный
        else if (expValue <= 120) prefix = "coin/spr_coin_berd"; // зелёный — редкий
        else prefix = "coin/spr_coin_roj";                      // красный — эпический

        // Загружаем 4 кадра анимации
        frames[0] = new Texture(Gdx.files.internal(prefix + "_1.png"));
        frames[1] = new Texture(Gdx.files.internal(prefix + "_2.png"));
        frames[2] = new Texture(Gdx.files.internal(prefix + "_3.png"));
        frames[3] = new Texture(Gdx.files.internal(prefix + "_4.png"));


        // Создаём Animation
        TextureRegion[] regions = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            regions[i] = new TextureRegion(frames[i]);
        }
        animation = new Animation<>(0.3f, regions);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float playerX, float playerY) {
        if (collected) return;

        stateTime += Gdx.graphics.getDeltaTime();

        // Притягиваемся к игроку, если он близко
        float dx = playerX - position.x;
        float dy = playerY - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 100f) {
            // тянемся к игроку
            float pullSpeed = speed + (100 - distance) * 2;
            position.x += (dx / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
            position.y += (dy / distance) * pullSpeed * Gdx.graphics.getDeltaTime();
        }

        // Если игрок подобрал
        if (distance < 30f) {
            collected = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!collected) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x, position.y, 24, 24);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public int getExpValue() {
        return expValue;
    }

    public void dispose() {
        for (TextureRegion region : animation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }
}
