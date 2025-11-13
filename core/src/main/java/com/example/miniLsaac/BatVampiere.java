package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BatVampiere extends Enemy {

    public BatVampiere(float x, float y) {
        super(x, y);

        this.maxHP = 80;
        this.hp = maxHP;
        this.speed = 90f;
        this.expDrop = 100;
        this.damage = 25;

        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatVampiere_Flying_4.png"))
        };
        loadAnimation();
    }
}

