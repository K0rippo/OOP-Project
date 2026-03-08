package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

/**
 * Interface for collidable entities.
 * Supports interface segregation: not all entities have collision bounds.
 */
public interface ICollidable {
    Rectangle getBounds();
    void onCollision(Entity other);
}
