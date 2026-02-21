package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    
    // --- REQUIRED ABSTRACT METHODS ---
    public abstract Rectangle getBounds();     // "Where am I?"
    public abstract void onCollision(Entity other); // "What happens when I hit something?"

    public abstract void render(SpriteBatch batch);

    // --- Getters & Setters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // added
    public Vector2 getPosition() { return getOrientate().getPosition(); }
    public void setPosition(Vector2 position) { orientate.setPosition(position); }

    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    public Orientate getOrientate() { return orientate; }
    

}