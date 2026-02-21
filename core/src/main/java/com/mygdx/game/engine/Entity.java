package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    
    private int id;
    //private Vector2 position;
    //private Vector2 velocity;
    private boolean isActive;
    private String name;
    private final Orientate orientate;

    public Entity(int id, String name, Vector2 position) {
        this.id = id;
        this.name = name;
        //this.position = position;
        //this.velocity = new Vector2(0, 0);
        this.orientate = new Orientate();
        this.orientate.getPosition().set(position);
        this.isActive = true;
    }

    public void update(float deltaTime) {
        //position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        //transform.setPosition(position);
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
    //public Vector2 getPosition() { return position; }
    //public void setPosition(Vector2 position) { this.position = position; }
    //public Vector2 getVelocity() { return velocity; }
    //public void setVelocity(Vector2 velocity) { this.velocity = velocity; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    public Orientate getOrientate() { return orientate; }
    
    //@Override
    //public void applyMovement(float deltaTime) {
        //Vector2 delta = velocity.cpy().scl(deltaTime);
        //transform.getPosition().add(delta);
    //}

}