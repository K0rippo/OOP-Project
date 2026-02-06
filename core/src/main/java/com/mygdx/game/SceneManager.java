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
    }

    public void setActiveScene(String id) {
        if (scenes.containsKey(id)) {
            // 1. If there is an old scene, hide it
            if (activeScene != null) {
                activeScene.hide();
            }
            
            // 2. Switch to new scene
            activeScene = scenes.get(id);
            
            // 3. Show the new scene (This will turn on the buttons!)
            activeScene.show(); 
            
        } else {
            System.out.println("Scene " + id + " does not exist.");
        }
    }

    // ... rest of your code (updateActiveScene, renderActiveScene, etc) ...
    public void updateActiveScene(float deltaTime) {
        if (activeScene != null) activeScene.update(deltaTime);
    }

    public void renderActiveScene(SpriteBatch batch) {
        if (activeScene != null) activeScene.render(batch);
    }
}