package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    private final String id;
    private boolean isActive;

    public Scene(String id) {
        this.id = id;
        this.isActive = true;
    }

    public String getId() { return id; }
    
    // Sets scene to active state
    public void show() { this.isActive = true; }
    
    // Sets scene to inactive state
    public void hide() { this.isActive = false; }
    
    public boolean isActive() { return isActive; }

    // Every scene MUST define how it updates, renders, and resizes itself
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract void resize(int width, int height);
}