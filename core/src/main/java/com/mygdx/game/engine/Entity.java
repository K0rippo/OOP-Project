package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements ICollidable, IRenderable {
    private int id;
    private boolean isActive;
    private String name;
    private final Orientate orientate;

    private int collisionLayer = 1;
    private int collisionMask = -1;

    public Entity(int id, String name, Vector2 position) {
        this.id = id;
        this.name = name;
        this.orientate = new Orientate();
        this.orientate.getPosition().set(position);
        this.isActive = true;
    }

    public void update(float deltaTime) {}

    @Override
    public abstract Rectangle getBounds();
    
    @Override
    public abstract void onCollision(Entity other);
    
    @Override
    public abstract void render(SpriteBatch batch);

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {}

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Vector2 getPosition() { return getOrientate().getPosition(); }
    public void setPosition(Vector2 position) { orientate.setPosition(position); }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public Orientate getOrientate() { return orientate; }

    public int getCollisionLayer() { return collisionLayer; }
    public void setCollisionLayer(int layer) { this.collisionLayer = layer; }
    public int getCollisionMask() { return collisionMask; }
    public void setCollisionMask(int mask) { this.collisionMask = mask; }

    public boolean canCollideWith(Entity other) {
        return (this.collisionMask & other.getCollisionLayer()) != 0 &&
               (other.getCollisionMask() & this.collisionLayer) != 0;
    }

    @Override
    public String toString() { return "Entity{name='" + name + "'}"; }
}