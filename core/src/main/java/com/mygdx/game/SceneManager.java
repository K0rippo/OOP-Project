package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Map<String, Scene> scenes;
    private Scene activeScene;

    public SceneManager() {
        this.scenes = new HashMap<>();
    }

    public void addScene(String id, Scene scene) {
        scenes.put(id, scene);
        if (activeScene == null) {
            activeScene = scene;
        }
    }

    public void removeScene(String id) {
        scenes.remove(id);
    }

    public void setActiveScene(String id) {
        if (scenes.containsKey(id)) {
            activeScene = scenes.get(id);
        } else {
            System.out.println("Scene " + id + " does not exist.");
        }
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void updateActiveScene(float deltaTime) {
        if (activeScene != null) {
            activeScene.update(deltaTime);
        }
    }

    public void renderActiveScene(SpriteBatch batch, EntityManager entityManager) {
        if (activeScene != null) {
            activeScene.render(batch, entityManager);
        }
    }
}