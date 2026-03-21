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

    public void show() { this.isActive = true; }

    public void hide() { this.isActive = false; }

    public boolean isActive() { return isActive; }

    //each scene provides its own lifecycle behavior
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract void resize(int width, int height);
}