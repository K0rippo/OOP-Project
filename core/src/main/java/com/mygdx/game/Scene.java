package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public class Scene {
    private final String id;
    private final EntityManager entityManager; // The "Source of Truth"
    private boolean isActive;

    public Scene(String id) {
        this.id = id;
        this.entityManager = new EntityManager(); // Composition: Scene owns its manager
        this.isActive = true;
    }

    // --- Lifecycle Methods ---
    
    public void show() {
        this.isActive = true;
        // Logic for when the scene starts (e.g., play music)
    }

    public void hide() {
        this.isActive = false;
        // Logic for when the scene is swapped (e.g., stop music, clear memory)
    }

    // --- Entity Management (Delegated to EntityManager) ---

    public void addEntity(Entity e) {
        entityManager.addEntity(e);
    }

    public void removeEntity(Entity e) {
        entityManager.removeEntity(e);
    }

    /**
     * Accessor for external systems (like a Physics engine).
     * Now correctly retrieves the list from the manager.
     */
    public List<Entity> getEntities() {
        return entityManager.getEntities();
    }

    // --- Loop Methods ---

    public void update(float deltaTime) {
        if (!isActive) return;
        
        // High-level delegation: Let the manager handle the logic loop
        entityManager.updateAll(deltaTime);
    }

    public void render(SpriteBatch batch) {
        if (!isActive) return;

        // High-level delegation: Let the manager handle the drawing loop
        entityManager.renderAll(batch);
    }

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}