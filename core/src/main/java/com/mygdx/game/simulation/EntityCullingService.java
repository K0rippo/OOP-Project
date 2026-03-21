package com.mygdx.game.simulation;

import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.IGameEngine;

class EntityCullingService {
    private final IGameEngine engine;
    private final float worldWidth;
    private final int playerLayer;
    private final int gateLayer;
    private final int enemyLayer;
    private final int enemyBulletLayer;

    EntityCullingService(IGameEngine engine,
                         float worldWidth,
                         int playerLayer,
                         int gateLayer,
                         int enemyLayer,
                         int enemyBulletLayer) {
        this.engine = engine;
        this.worldWidth = worldWidth;
        this.playerLayer = playerLayer;
        this.gateLayer = gateLayer;
        this.enemyLayer = enemyLayer;
        this.enemyBulletLayer = enemyBulletLayer;
    }

    void clearDynamicEntities(Entity persistentPlayer) {
        clearLayer(gateLayer, null);
        clearLayer(enemyLayer, null);
        clearLayer(enemyBulletLayer, null);
        clearLayer(playerLayer, persistentPlayer);
    }

    void cleanupOffScreen(Entity persistentPlayer) {
        //remove entities that have moved far outside view bounds
        for (Entity entity : engine.getEntitiesByLayer(gateLayer)) {
            if (entity.getX() < -200f) engine.removeEntity(entity);
        }
        for (Entity entity : engine.getEntitiesByLayer(enemyLayer)) {
            if (entity.getX() < -200f) engine.removeEntity(entity);
        }
        for (Entity entity : engine.getEntitiesByLayer(enemyBulletLayer)) {
            if (entity.getX() < -200f) engine.removeEntity(entity);
        }
        for (Entity entity : engine.getEntitiesByLayer(playerLayer)) {
            if (entity != persistentPlayer && entity.getX() > worldWidth + 200f) {
                engine.removeEntity(entity);
            }
        }
    }

    private void clearLayer(int layer, Entity except) {
        for (Entity entity : engine.getEntitiesByLayer(layer)) {
            if (entity != except) {
                engine.removeEntity(entity);
            }
        }
    }
}
