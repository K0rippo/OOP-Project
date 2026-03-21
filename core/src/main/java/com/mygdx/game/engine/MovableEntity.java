package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

public abstract class MovableEntity extends Entity implements iMovable {

    private Vector2 velocity = new Vector2(0f, 0f);
    //default movement strategy updates position by velocity over time
    private MovementLogic movementLogic = (orientate, vel, dt) -> 
        orientate.getPosition().add(vel.x * dt, vel.y * dt);

    public MovableEntity(int id, String name, Vector2 position) {
        super(id, name, position);
    }

    @Override
    public Vector2 getVelocity() { return new Vector2(velocity); }

    @Override
    public void setVelocity(Vector2 velocity) {
        if (velocity != null) {
            this.velocity.set(velocity);
        }
    }

    public void setVelocityX(float x) { velocity.x = x; }
    public void setVelocityY(float y) { velocity.y = y; }
    public void scaleVelocity(float factor) { velocity.scl(factor); }

    @Override
    public void setMovementLogic(MovementLogic logic) {
        if (logic != null) this.movementLogic = logic;
    }

    @Override
    public void applyMovement(float deltaTime) {
        movementLogic.move(getOrientate(), velocity, deltaTime);
    }
}