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

/**
 * GameMaster — LibGDX application entry point.
 *
 * SRP  : responsible only for bootstrapping the engine, scene graph, and main loop.
 * DIP  : wires concrete implementations to abstractions (IGameEngine, IQuestionProvider).
 *
 * Encapsulation: isMuted is private; external code uses the static accessor pair
 * isMuted()/setMuted() instead of reading a raw public field.
 */
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch  batch;
    private SceneManager sceneManager;
    private IGameEngine  gameEngine;
    private Texture      uiButtonTexture;

    /** Global mute flag — private; accessed via {@link #isMuted()} and {@link #setMuted(boolean)}. */
    private static boolean muted = false;

    /** @return true when the game audio is muted. */
    public static boolean isMuted() { return muted; }

    /** Toggle or set the global mute state. */
    public static void setMuted(boolean value) { muted = value; }

    @Override
    public void create() {
        batch        = new SpriteBatch();
        sceneManager = new SceneManager();
        gameEngine   = new Engine();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        uiButtonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Load questions from CSV file (assets/questions.csv)
        // Falls back to default questions if CSV is not found
        IQuestionProvider questionProvider = new CsvQuestionProvider();

        sceneManager.addScene("MENU",     new MenuScene    ("MENU",     sceneManager, gameEngine, uiButtonTexture));
        sceneManager.addScene("GAME",     new GameScene    ("GAME",     sceneManager, gameEngine, questionProvider));
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