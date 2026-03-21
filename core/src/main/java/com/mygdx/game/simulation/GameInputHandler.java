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
        //vertical movement with world bounds checks
        engine.bindKeyContinuous(Input.Keys.UP, () -> {
            if (player != null && !player.isControlsLocked() && player.getY() + player.getRadius() < worldHeight - 5)
                player.setVelocityY(250f);
        });
        
        engine.bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (player != null && !player.isControlsLocked() && player.getY() - player.getRadius() > 5)
                player.setVelocityY(-250f);
        });

        //horizontal movement with world bounds checks
        engine.bindKeyContinuous(Input.Keys.LEFT, () -> {
            if (player != null && !player.isControlsLocked() && player.getX() - player.getRadius() > 5)
                player.setVelocityX(-250f);
        });
        
        engine.bindKeyContinuous(Input.Keys.RIGHT, () -> {
            if (player != null && !player.isControlsLocked() && player.getX() + player.getRadius() < worldWidth - 5)
                player.setVelocityX(250f);
        });

        engine.bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) player.requestShoot();
        });

        //switch to settings and remember the return scene
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