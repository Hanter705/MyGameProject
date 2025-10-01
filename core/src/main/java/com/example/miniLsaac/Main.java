package com.example.miniLsaac;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shape;
    private float playerX, playerY;
    private float speed = 200; // пикселей в секунду

    @Override
    public void create() {
        shape = new ShapeRenderer();
        playerX = 100;
        playerY = 100;
    }

    @Override
    public void render() {
        // очищаем экран синим цветом
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // обработка ввода
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) playerY += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) playerY -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) playerX -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) playerX += speed * delta;

        // рисуем игрока (зелёный квадрат)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 1, 0, 1);
        shape.rect(playerX, playerY, 40, 40);
        shape.end();
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
