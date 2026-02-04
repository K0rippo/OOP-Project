package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	
    private int id;
    private Vector2 position;
    private Vector2 velocity;
    private String state;
    private boolean isActive;

    //Constructor 
    public Entity(int id, Vector2 position)
    {
        this.id = id;
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.state = "default";
        this.isActive = true;
    }

    public void update(float deltaTime)
    {
        //Basic movement logic
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    public abstract void render(SpriteBatch batch);

    //Gett setters
    public int getId() { return id; }
    
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }
    
    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}

public class Entity implements Movable {

    private final Transform transform;
    private Vector2 velocity;

    public Entity() {
        transform = new Transform();
        velocity = new Vector2(0f, 0f);
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    @Override
    public void applyMovement(float deltaTime) {
        Vector2 delta = velocity.cpy().scl(deltaTime);
        transform.getPosition().add(delta);
    }

    public void rotate(float angleDegrees) {
        transform.setRotationDegrees(
            transform.getRotationDegrees() + angleDegrees
        );
    }

    public void lookAt(Vector2 target) {
        Vector2 dir = target.cpy().sub(transform.getPosition());
        float angle = (float) Math.toDegrees(Math.atan2(dir.y, dir.x));
        transform.setRotationDegrees(angle);
    }

    public void moveTowards(Vector2 target) {
        Vector2 dir = target.cpy()
                            .sub(transform.getPosition())
                            .nor();

        float speed = velocity.len();
        velocity = dir.scl(speed);
    }
}

