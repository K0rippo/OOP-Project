package com.mygdx.game.simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.engine.Engine;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private SceneManager sceneManager;
    private IGameEngine gameEngine;
    private Texture uiButtonTexture;
    public static boolean isMuted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();
        gameEngine = new Engine();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        uiButtonTexture = new Texture(pixmap);
        pixmap.dispose();

        sceneManager.addScene("MENU",     new MenuScene    ("MENU",     sceneManager, gameEngine, uiButtonTexture));
        sceneManager.addScene("GAME",     new GameScene    ("GAME",     sceneManager, gameEngine, new DefaultQuestionProvider()));
        sceneManager.addScene("SETTINGS", new SettingsScene("SETTINGS", sceneManager, gameEngine, uiButtonTexture));
        sceneManager.addScene("RESULT",   new ResultScene  ("RESULT",   sceneManager, gameEngine, uiButtonTexture));

        sceneManager.setActiveScene("MENU");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        uiButtonTexture.dispose();
        gameEngine.dispose();
    }
}