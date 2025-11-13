package com.example.miniLsaac;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BatAlbino extends Enemy {


    public BatAlbino(float x, float y) {
        super(x, y);

        this.maxHP = 60;
        this.hp = maxHP;
        this.speed = 65f;
        this.expDrop = 50;
        this.damage = 15;

        flyTextures = new Texture[]{
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_1.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_2.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_3.png")),
            new Texture(Gdx.files.internal("bad/BatAlbino_Flying_4.png"))
        };
        loadAnimation();
    }





}
