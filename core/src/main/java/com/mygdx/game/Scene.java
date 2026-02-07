package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String id;
    private List<Entity> sceneEntities;
    private boolean isActive;

    public Scene(String id) {
        this.id = id;
        this.sceneEntities = new ArrayList<>();
        this.isActive = true;
    }
    
    public void show() {
        // This method is called when the scene becomes active
    }
    
    public void hide() {
        // This method is called when the scene is removed/switched away
    }

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
        // updated entities would go here
    }

    public void render(SpriteBatch batch, EntityManager entityManager) {
        for (Entity e : entityManager.getEntities()) {
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