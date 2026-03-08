package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

/**
 * Functional interface for movement logic to satisfy strategy injection
 */
@FunctionalInterface
interface MovementLogic {
    void move(Orientate orientate, Vector2 velocity, float deltaTime);
}

public interface iMovable {
    void applyMovement(float deltaTime);
    Orientate getOrientate();
    Vector2 getVelocity();
    void setVelocity(Vector2 velocity);
    // Allow injecting logic as a lambda
    void setMovementLogic(MovementLogic logic);
}