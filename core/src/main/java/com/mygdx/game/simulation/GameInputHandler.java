package com.mygdx.game.simulation;

import com.badlogic.gdx.Input;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.ISettingsScene;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class GameInputHandler {
    private final IGameEngine engine;
    private final ISceneNavigator sceneNavigator;
    private PlayerCharacter player;
    private final float worldHeight;
    private final float worldWidth = 1280f;

    public GameInputHandler(IGameEngine engine, ISceneNavigator sceneNavigator, float worldHeight) {
        this.engine = engine;
        this.sceneNavigator = sceneNavigator;
        this.worldHeight = worldHeight;
    }

    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    public void initializeInput() {
        // --- ARROW KEYS ---
        engine.bindKeyContinuous(Input.Keys.UP, () -> {
            if (!GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getY() + player.getRadius() < worldHeight - 5)
                player.setVelocityY(250f);
        });
        engine.bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (!GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getY() - player.getRadius() > 5)
                player.setVelocityY(-250f);
        });
        engine.bindKeyContinuous(Input.Keys.LEFT, () -> {
            if (!GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getX() - player.getRadius() > 5)
                player.setVelocityX(-250f);
        });
        engine.bindKeyContinuous(Input.Keys.RIGHT, () -> {
            if (!GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getX() + player.getRadius() < worldWidth - 5)
                player.setVelocityX(250f);
        });

        // --- WASD KEYS ---
        engine.bindKeyContinuous(Input.Keys.W, () -> {
            if (GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getY() + player.getRadius() < worldHeight - 5)
                player.setVelocityY(250f);
        });
        engine.bindKeyContinuous(Input.Keys.S, () -> {
            if (GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getY() - player.getRadius() > 5)
                player.setVelocityY(-250f);
        });
        engine.bindKeyContinuous(Input.Keys.A, () -> {
            if (GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getX() - player.getRadius() > 5)
                player.setVelocityX(-250f);
        });
        engine.bindKeyContinuous(Input.Keys.D, () -> {
            if (GameMaster.isUseWASD() && player != null && !player.isControlsLocked() && player.getX() + player.getRadius() < worldWidth - 5)
                player.setVelocityX(250f);
        });

        // --- MISC ---
        engine.bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) player.requestShoot();
        });

        engine.bindKeyJustPressed(Input.Keys.ESCAPE, () -> {
            Scene settingsScene = sceneNavigator.getScene("SETTINGS");
            if (settingsScene instanceof ISettingsScene) {
                ISettingsScene settings = (ISettingsScene) settingsScene;
                settings.setPreviousScene("GAME");
            }
            sceneNavigator.goToScene("SETTINGS");
        });
    }
}