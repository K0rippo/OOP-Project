package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public class Engine implements IGameEngine {
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;
    private final MovementManager movementManager;
    private final RenderManager renderManager;

    public Engine(EntityManager entityManager, CollisionManager collisionManager, IOManager ioManager, MovementManager movementManager, RenderManager renderManager) {
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
        this.movementManager = movementManager;
        this.renderManager = renderManager;

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

    @Override
    public void addEntity(Entity e) {
        entityManager.addEntity(e);
    }

    @Override
    public void removeEntity(Entity e) {
        entityManager.removeEntity(e);
    }

    @Override
    public List<Entity> getEntitiesByLayer(int layer) {
        return entityManager.getEntitiesByLayer(layer);
    }

    @Override
    public void bindKeyContinuous(int keycode, Runnable action) {
        ioManager.bindKeyContinuous(keycode, action);
    }

    @Override
    public void bindKeyJustPressed(int keycode, Runnable action) {
        ioManager.bindKeyJustPressed(keycode, action);
    }

    @Override
    public void setSpeedMultiplier(float multiplier) {
        movementManager.setSpeedMultiplier(multiplier);
    }

    public void dispose() {
        renderManager.dispose();
    }
}