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

    // Global settings state (Optional: could be moved to a SettingsProfile class later)
    public static boolean isMuted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        // 1. Initialize Scenes
        // Note: We pass sceneManager to scenes so they can trigger scene-switches themselves
        MenuScene menuScene = new MenuScene("MENU", sceneManager);
        GameScene gameScene = new GameScene("GAME", sceneManager);
        SettingsScene settingsScene = new SettingsScene("SETTINGS", sceneManager);

        // 2. Register Scenes with the Manager
        sceneManager.addScene(menuScene.getId(), menuScene);
        sceneManager.addScene(gameScene.getId(), gameScene);
        sceneManager.addScene(settingsScene.getId(), settingsScene);

        // 3. Create Entities and add them to the specific scene they belong to
        createGameLevel(gameScene);

        // 4. Set the initial state
        sceneManager.setActiveScene("MENU");
    }

    /**
     * Helper method to keep create() clean. 
     * This populates the GameScene with its specific entities.
     */
    private void createGameLevel(GameScene gameScene) {
        Entity ball = new Circle(1, "Ball", new Vector2(200, 200), 15, Color.BROWN);
        
        Entity trampoline = new RectangleEntity(2, "Trampoline", new Vector2(50, 50), 150, 20, Color.GREEN);
        
        Entity wall = new RectangleEntity(3, "Wall", new Vector2(600, 50), 40, 200, Color.BLACK);

        // Add them to the gameScene's internal EntityManager
        gameScene.addEntity(ball);
        gameScene.addEntity(trampoline);
        gameScene.addEntity(wall);

        // Create coins
        for (int i = 0; i < 2; i++) {
            Entity coin = new Circle(100 + i, "Coin" + i, new Vector2(300 + i * 40, 250), 5, Color.GOLD);
            gameScene.addEntity(coin);
        }
    }

    @Override
    public void render() {
        // Clear screen
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. Update Logic: The manager tells the ACTIVE scene to update its own EntityManager
        sceneManager.updateActiveScene(deltaTime);

        // 2. Rendering: The manager tells the ACTIVE scene to draw its own entities
        batch.begin();
        sceneManager.renderActiveScene(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Future-proofing: You could tell sceneManager to dispose all scenes here
    }
}