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

    public void render(SpriteBatch batch) {
        if (!isActive) return;
        // rendered entities would go here
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