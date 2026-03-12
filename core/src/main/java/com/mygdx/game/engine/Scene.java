package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    private final String id;
    protected final IGameEngine engine; 
    private boolean isActive;

    public Scene(String id, IGameEngine engine) {
        this.id = id;
        this.engine = engine;
        this.isActive = true;
    }

    public void update(float deltaTime) {
        if (!isActive) return;
        engine.update(deltaTime);
    }

    public void render(SpriteBatch batch) {
        if (!isActive) return;
        engine.render(batch);
    }

    // Adds an entity to the engine
    public void addEntity(Entity e) { engine.addEntity(e); }
    
    // Removes an entity from the engine
    public void removeEntity(Entity e) { engine.removeEntity(e); }

    public String getId() { return id; }
    
    // Sets scene to active state
    public void show() { this.isActive = true; }
    
    // Sets scene to inactive state
    public void hide() { this.isActive = false; }
    
    public abstract void resize(int width, int height);
}