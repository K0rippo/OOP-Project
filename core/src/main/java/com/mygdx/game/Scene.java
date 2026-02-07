package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String id;
    private List<Entity> sceneEntities; // Local list
    private boolean isActive;

    public Scene(String id) {
        this.id = id;
        this.sceneEntities = new ArrayList<>();
        this.isActive = true;
    }
    
    public void show() {}
    public void hide() {}

    public void addEntity(Entity e) {
        sceneEntities.add(e);
    }

    public void removeEntity(Entity e) {
        sceneEntities.remove(e);
    }

    public List<Entity> getEntities() {
        return sceneEntities;
    }

    public void update(float deltaTime) {
        if (!isActive) return;
        
        // <--- NEW: Update ONLY local entities
        for (Entity e : sceneEntities) {
            if (e.isActive()) e.update(deltaTime);
        }
    }

    public void render(SpriteBatch batch, EntityManager entityManager) {
        // <--- NEW: Render ONLY local entities (Fixes invisible player bug)
        for (Entity e : sceneEntities) {
            e.render(batch, entityManager);
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getId() {
        return id;
    }
}