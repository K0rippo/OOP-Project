package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

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
        renderManager.render(batch, entityManager.getEntities());
    }

    // Adds an entity to the simulation
    @Override
    public void addEntity(Entity e) {
        entityManager.addEntity(e);
    }

    // Removes an entity from the simulation
    @Override
    public void removeEntity(Entity e) {
        entityManager.removeEntity(e);
    }

    // Retrieves entities belonging to a specific layer
    @Override
    public List<Entity> getEntitiesByLayer(int layer) {
        return entityManager.getEntitiesByLayer(layer);
    }

    // Binds an action to a continuously pressed key
    @Override
    public void bindKeyContinuous(int keycode, Runnable action) {
        ioManager.bindKeyContinuous(keycode, action);
    }

    // Binds an action to a key press event
    @Override
    public void bindKeyJustPressed(int keycode, Runnable action) {
        ioManager.bindKeyJustPressed(keycode, action);
    }

    // Sets the global movement speed multiplier
    @Override
    public void setSpeedMultiplier(float multiplier) {
        movementManager.setSpeedMultiplier(multiplier);
    }

    public void dispose() {
        renderManager.dispose();
    }
}