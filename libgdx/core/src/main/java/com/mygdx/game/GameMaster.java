package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.MathUtils;

public class GameMaster extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    private TextureObject bucket;
    private TextureObject[] drops;
    private Circle myCircle;
    private Triangle myTriangle;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        bucket = new TextureObject("bucket.jpg", 300, 20, 5, 64, 64);

        myCircle = new Circle(100, 200, 30, Color.RED, 4);

        myTriangle = new Triangle(400, 300, Color.BLUE, 4);

        drops = new TextureObject[5];
        for (int i = 0; i < drops.length; i++) {
            float randomX = MathUtils.random(0, 800 - 48);
            float randomY = MathUtils.random(480, 800);
            drops[i] = new TextureObject("droplet.png", randomX, randomY, 2, 48, 48);
        }
    }

    @Override
    public void render() {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        bucket.bucketMovement();
        myCircle.movement();
        myTriangle.movement();
        
        for (TextureObject drop : drops) {
            drop.raindropMovement();
        }

        batch.begin();
        bucket.draw(batch); //
        for (TextureObject drop : drops) {
            drop.draw(batch);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        myCircle.draw(shapeRenderer);
        myTriangle.draw(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        bucket.dispose();
        for (TextureObject drop : drops) {
            drop.dispose();
        }
    }
}