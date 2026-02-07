package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();
        entityManager = new EntityManager();

        // Create scenes
        Scene sandbox = new Scene("sandbox");
        sceneManager.addScene("sandbox", sandbox);
        sceneManager.setActiveScene("sandbox");

        // Create entities
        Entity ball = new Circle(
                1,
                "Ball",
                new Vector2(200, 200),
                15,
                Color.BROWN
        );


        Entity trampoline = new RectangleEntity(
        		2,
                "Trampoline",
                new Vector2(50, 50),
                150,
                20,
                Color.GREEN
        );

        Entity wall = new RectangleEntity(
        		3,
                "Wall",
                new Vector2(600, 50),
                40,
                200,
                Color.BLACK
        );

        // Coins
        for (int i = 0; i < 2; i++) {
            Entity coin = new Circle(
            		100 + i,
                    "Coin" + i,
                    new Vector2(300 + i * 40, 250),
                    5,
                    Color.GOLD
            );
            entityManager.addEntity(coin);
        }

        // Register main entities
        entityManager.addEntity(ball);
        entityManager.addEntity(trampoline);
        entityManager.addEntity(wall);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1f);

        batch.begin();
        sceneManager.renderActiveScene(batch, entityManager);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}