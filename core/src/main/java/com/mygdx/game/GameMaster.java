package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    
    // global settings state
    public static boolean isMuted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        // create scenes
        MenuScene menuScene = new MenuScene("MENU", sceneManager);
        GameScene gameScene = new GameScene("GAME", sceneManager);
        SettingsScene settingsScene = new SettingsScene("SETTINGS", sceneManager);

        // add to Manager
        sceneManager.addScene(menuScene.getId(), menuScene);
        sceneManager.addScene(gameScene.getId(), gameScene);
        sceneManager.addScene(settingsScene.getId(), settingsScene);

        // start at Menu
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