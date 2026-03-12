package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public class Engine implements IGameEngine {
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;
    private final MovementManager movementManager;
    private final RenderManager renderManager;

    // Initializes the engine with injected manager dependencies
    public Engine(EntityManager entityManager, CollisionManager collisionManager, IOManager ioManager, MovementManager movementManager, RenderManager renderManager) {
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
        this.movementManager = movementManager;
        this.renderManager = renderManager;
        
        this.entityManager.linkManagers(this.movementManager);
    }

    // Updates all engine systems every frame
    public void update(float deltaTime) {
        ioManager.handleInput();
        entityManager.updateAll(deltaTime);
        movementManager.update(deltaTime);
        collisionManager.checkCollisions(entityManager.getEntities());
    }

    // Renders all entities in the simulation
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

    // Retrieves entities belonging to a specific collision layer
    @Override
    public List<Entity> getEntitiesByLayer(int layer) {
        return entityManager.getEntitiesByLayer(layer);
    }

    // Binds an action to a continuously pressed key
    @Override
    public void bindKeyContinuous(int keycode, Runnable action) {
        ioManager.bindKeyContinuous(keycode, action);
    }

    // Binds an action to a single key press event
    @Override
    public void bindKeyJustPressed(int keycode, Runnable action) {
        ioManager.bindKeyJustPressed(keycode, action);
    }

    // Sets the global movement speed multiplier for the simulation
    @Override
    public void setSpeedMultiplier(float multiplier) {
        movementManager.setSpeedMultiplier(multiplier);
    }

    // Frees system resources
    public void dispose() {
        renderManager.dispose();
    }
}