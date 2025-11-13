package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BatRoot extends Enemy {

    public BatRoot(float x, float y) {
        super(x, y);

        this.maxHP = 110;
        this.hp = maxHP;
        this.speed = 45f;
        this.expDrop = 70;
        this.damage = 20;

        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatRoot_Flying_4.png"))
        };
        loadAnimation();
    }
}
