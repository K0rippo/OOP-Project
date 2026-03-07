package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    
    private int id;
    private boolean isActive;
    private String name;
    private final Orientate orientate;

    public Entity(int id, String name, Vector2 position) {
        this.id = id;
        this.name = name;
        this.orientate = new Orientate();
        this.orientate.getPosition().set(position);
        this.isActive = true;
    }

    public void update(float deltaTime) {
    }
    
    public abstract Rectangle getBounds();
    public abstract void onCollision(Entity other);

    public abstract void render(SpriteBatch batch);
    
    // New method for shape rendering
    public void renderShape(ShapeRenderer shapeRenderer) {
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Vector2 getPosition() { return getOrientate().getPosition(); }
    public void setPosition(Vector2 position) { orientate.setPosition(position); }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    public Orientate getOrientate() { return orientate; }
    
    @Override
    public String toString() { return "Entity{name='" + name + "'}"; }
}