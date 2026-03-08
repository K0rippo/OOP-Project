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

    public void addEntity(Entity e) { engine.getEntityManager().addEntity(e); }
    public void removeEntity(Entity e) { engine.getEntityManager().removeEntity(e); }

    public String getId() { return id; }
    public void show() { this.isActive = true; }
    public void hide() { this.isActive = false; }
    public abstract void resize(int width, int height);
}