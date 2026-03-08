package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Engine implements IGameEngine {
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;
    private final MovementManager movementManager;
    private final RenderManager renderManager;

    public Engine() {
        this.entityManager = new EntityManager();
        this.collisionManager = new CollisionManager();
        this.ioManager = new IOManager();
        this.movementManager = new MovementManager();
        this.renderManager = new RenderManager();
        
        this.entityManager.linkManagers(this.movementManager);
    }

    public void update(float deltaTime) {
        ioManager.handleInput();
        entityManager.updateAll(deltaTime);
        movementManager.update(deltaTime);
        collisionManager.checkCollisions(entityManager.getEntities());
    }

    public void render(SpriteBatch batch) {
        // Delegate rendering to the specialized manager
        renderManager.render(batch, entityManager.getEntities());
    }

    public EntityManager getEntityManager() { return entityManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIOManager() { return ioManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public RenderManager getRenderManager() { return renderManager; }

    public void dispose() {
        entityManager.dispose();
        renderManager.dispose();
    }
}