package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private EntityManager entityManager;

    // global settings state
    public static boolean isMuted = false;


    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();
        entityManager = new EntityManager();


        // create scenes
        MenuScene menuScene = new MenuScene("MENU", sceneManager);
        GameScene gameScene = new GameScene("GAME", sceneManager);
        SettingsScene settingsScene = new SettingsScene("SETTINGS", sceneManager);

        // add to Manager
        sceneManager.addScene(menuScene.getId(), menuScene);
        sceneManager.addScene(gameScene.getId(), gameScene);
        sceneManager.addScene(settingsScene.getId(), settingsScene);

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

        // start at Menu
        sceneManager.setActiveScene("MENU");

    }

    @Override
    public void render() {

        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();
        sceneManager.updateActiveScene(deltaTime);


        batch.begin();
        sceneManager.renderActiveScene(batch, entityManager);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}