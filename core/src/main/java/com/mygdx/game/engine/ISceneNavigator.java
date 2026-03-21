package com.mygdx.game.engine;

public interface ISceneNavigator {
    void goToScene(String sceneId);
    Scene getScene(String sceneId);
}
