package com.mygdx.game.simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.engine.*;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch  batch;
    private SceneManager sceneManager;
    private IGameEngine  gameEngine;
    private Texture      uiButtonTexture;
    private AudioManager audioManager;
    
    // --- NEW GLOBAL SETTINGS ---
    private static float musicVolume = 0.5f;
    private static float sfxVolume = 0.8f;
    private static boolean useWASD = false;

    public static float getMusicVolume() { return musicVolume; }
    public static void setMusicVolume(float v) { musicVolume = v; }
    
    public static float getSfxVolume() { return sfxVolume; }
    public static void setSfxVolume(float v) { sfxVolume = v; }
    
    public static boolean isUseWASD() { return useWASD; }
    public static void setUseWASD(boolean v) { useWASD = v; }
    // ---------------------------

    @Override
    public void create() {
        batch        = new SpriteBatch();
        sceneManager = new SceneManager();

        EntityManager entityManager = new EntityManager();
        CollisionManager collisionManager = new CollisionManager(1280f, 720f);
        IOManager ioManager = new IOManager();
        MovementManager movementManager = new MovementManager();
        RenderManager renderManager = new RenderManager();

        gameEngine   = new Engine(entityManager, collisionManager, ioManager, movementManager, renderManager);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        uiButtonTexture = new Texture(pixmap);
        pixmap.dispose();

        IQuestionProvider questionProvider = new CsvQuestionProvider();

        audioManager = new AudioManager();
        audioManager.loadAssets();

        sceneManager.addScene("MENU",     new MenuScene    ("MENU",     sceneManager, uiButtonTexture));
        sceneManager.addScene("INSTRUCTIONS", new InstructionsScene("INSTRUCTIONS", sceneManager, uiButtonTexture));
        sceneManager.addScene("GAME",     new GameScene    ("GAME",     sceneManager, gameEngine, questionProvider, audioManager));
        
        // Pass audioManager to SettingsScene so sliders update music in real-time
        sceneManager.addScene("SETTINGS", new SettingsScene("SETTINGS", sceneManager, uiButtonTexture, audioManager));
        
        sceneManager.addScene("RESULT",   new ResultScene  ("RESULT",   sceneManager, uiButtonTexture));

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

        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}