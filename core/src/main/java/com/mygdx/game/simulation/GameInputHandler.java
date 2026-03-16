package com.mygdx.game.simulation;

import com.badlogic.gdx.Input;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.ISceneNavigator;

public class GameInputHandler {
    private final IGameEngine engine;
    private final ISceneNavigator sceneNavigator;
    private PlayerCharacter player;
    private final float worldHeight;
    private final float worldWidth = 1280f; // Added world width for right-side boundaries

    // Initializes the input handler with engine and navigation dependencies
    public GameInputHandler(IGameEngine engine, ISceneNavigator sceneNavigator, float worldHeight) {
        this.engine = engine;
        this.sceneNavigator = sceneNavigator;
        this.worldHeight = worldHeight;
    }

    // Sets the active player character for input bindings
    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    // Binds keyboard input to game actions
    public void initializeInput() {
        engine.bindKeyContinuous(Input.Keys.UP, () -> {
            if (player != null && player.getPosition().y + player.getRadius() < worldHeight - 5)
                player.getVelocity().y = 250;
        });
        
        engine.bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (player != null && player.getPosition().y - player.getRadius() > 5)
                player.getVelocity().y = -250;
        });
        
        // --- ADDED LEFT AND RIGHT CONTROLS ---
        engine.bindKeyContinuous(Input.Keys.LEFT, () -> {
            if (player != null && player.getPosition().x - player.getRadius() > 5)
                player.getVelocity().x = -250;
        });
        
        engine.bindKeyContinuous(Input.Keys.RIGHT, () -> {
            if (player != null && player.getPosition().x + player.getRadius() < worldWidth - 5)
                player.getVelocity().x = 250;
        });
        // -------------------------------------

        engine.bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) player.requestShoot();
        });
        
        engine.bindKeyJustPressed(Input.Keys.ESCAPE, () -> {
            SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
            if (settings != null) settings.setPreviousScene("GAME");
            sceneNavigator.goToScene("SETTINGS");
        });
    }
}