package com.mygdx.game.engine;

/**
 * Interface for scene navigation to support dependency inversion.
 * Scenes depend on this abstraction, not the concrete SceneManager.
 */
public interface ISceneNavigator {
    void goToScene(String sceneId);
    Scene getScene(String sceneId);
}
