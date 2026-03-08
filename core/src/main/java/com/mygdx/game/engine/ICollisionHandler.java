package com.mygdx.game.engine;

/**
 * Interface for handling collision responses.
 * Supports open/closed principle: new collision handlers can be added without modifying Entity.
 * Supports interface segregation: entities only implement what they need.
 */
public interface ICollisionHandler {
    /**
     * Called when this entity collides with another.
     * @param other The entity this object collided with
     */
    void onCollision(Entity other);
}
