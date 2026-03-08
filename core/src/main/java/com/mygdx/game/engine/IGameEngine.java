package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Abstraction for the game engine to support dependency inversion.
 * Scenes depend on this interface, not the concrete Engine class.
 */
public interface IGameEngine {
    void update(float deltaTime);
    void render(SpriteBatch batch);
    void dispose();
    
    EntityManager getEntityManager();
    CollisionManager getCollisionManager();
    IOManager getIOManager();
    MovementManager getMovementManager();
    RenderManager getRenderManager();
}
