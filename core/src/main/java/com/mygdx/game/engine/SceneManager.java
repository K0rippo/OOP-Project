package com.mygdx.game.engine;

import com.badlogic.gdx.Gdx;
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
    }

    public Scene getScene(String id) {
        return scenes.get(id);
    }

    public void setActiveScene(String id) {
        if (scenes.containsKey(id)) {
            if (activeScene != null) activeScene.hide();
            activeScene = scenes.get(id);
            activeScene.show();
            // Force the new scene to instantly match the current window size
            activeScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            System.out.println("Scene " + id + " does not exist.");
        }
    }

    public void updateActiveScene(float deltaTime) {
        if (activeScene != null) activeScene.update(deltaTime);
    }

    public void renderActiveScene(SpriteBatch batch) {
        if (activeScene != null) {
            activeScene.render(batch);
        }

    }

    public void resize(int width, int height) {
        if (activeScene != null) {
            activeScene.resize(width, height);
        }
    }
}