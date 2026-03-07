package com.mygdx.game.simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.engine.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private Texture uiButtonTexture; 
    public static boolean isMuted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        // Generate the texture once
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        uiButtonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Pass the shared texture to the scenes that need it
        sceneManager.addScene("MENU", new MenuScene("MENU", sceneManager, uiButtonTexture));
        sceneManager.addScene("GAME", new GameScene("GAME", sceneManager));
        sceneManager.addScene("SETTINGS", new SettingsScene("SETTINGS", sceneManager, uiButtonTexture));
        sceneManager.addScene("RESULT", new ResultScene("RESULT", sceneManager, uiButtonTexture));

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
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        uiButtonTexture.dispose(); // Free memory
    }
}