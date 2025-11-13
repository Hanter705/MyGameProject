package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BatStandard extends Enemy {

    public BatStandard(float x, float y) {
        super(x, y);

        this.maxHP = 40;
        this.hp = maxHP;
        this.speed = 75f;
        this.expDrop = 35;
        this.damage = 10;

        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatStandard_Flying_4.png"))
        };
        loadAnimation();
    }
}
