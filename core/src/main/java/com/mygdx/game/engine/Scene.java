package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public class Scene {
    private final String id;
    private final EntityManager entityManager; 
    private final CollisionManager collisionManager; 
    private boolean isActive;

    public Scene(String id) {
        this.id = id;
        this.entityManager = new EntityManager();
        this.collisionManager = new CollisionManager(); 
        this.isActive = true;
    }

    public void show() { this.isActive = true; }
    public void hide() { this.isActive = false; }

    public void addEntity(Entity e) { entityManager.addEntity(e); }
    public void removeEntity(Entity e) { entityManager.removeEntity(e); }
    public List<Entity> getEntities() { return entityManager.getEntities(); }

    public void update(float deltaTime) {
        if (!isActive) return;
        entityManager.updateAll(deltaTime);
        collisionManager.checkCollisions(entityManager.getEntities());
    }

    public void render(SpriteBatch batch) {
        if (!isActive) return;
        entityManager.renderAll(batch);
    }

    public void resize(int width, int height) {}

    public String getId() { return id; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}