package com.mygdx.game.simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.engine.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;

    public static boolean isMuted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        // 1. Initialize Scenes
        MenuScene menuScene = new MenuScene("MENU", sceneManager);
        GameScene gameScene = new GameScene("GAME", sceneManager);
        SettingsScene settingsScene = new SettingsScene("SETTINGS", sceneManager);

        // 2. Register Scenes
        sceneManager.addScene(menuScene.getId(), menuScene);
        sceneManager.addScene(gameScene.getId(), gameScene);
        sceneManager.addScene(settingsScene.getId(), settingsScene);

        // 3. Set initial state
        sceneManager.setActiveScene("MENU");
      
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1f);
        float deltaTime = Gdx.graphics.getDeltaTime();

        sceneManager.updateActiveScene(deltaTime);

        batch.begin();
        sceneManager.renderActiveScene(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}