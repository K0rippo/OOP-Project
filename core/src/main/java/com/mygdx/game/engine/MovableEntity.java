package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

public abstract class MovableEntity extends Entity implements iMovable {
    
    private Vector2 velocity = new Vector2(0f, 0f);
    // Default strategy defined as a lambda to save creating extra files
    private MovementLogic movementLogic = (orientate, vel, dt) -> 
        orientate.getPosition().add(vel.x * dt, vel.y * dt);
    
    public MovableEntity(int id, String name, Vector2 position) {
        super(id, name, position);
    }
    
    @Override
    public Vector2 getVelocity() { return velocity; }
    
    @Override
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }

    /**
     * Injects new movement logic at runtime using a lambda or method reference
     */
    @Override
    public void setMovementLogic(MovementLogic logic) {
        if (logic != null) this.movementLogic = logic;
    }
    
    @Override
    public void applyMovement(float deltaTime) {
        movementLogic.move(getOrientate(), velocity, deltaTime);
    }
}