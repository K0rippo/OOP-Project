package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    
    private final List<Entity> entities;
    private MovementManager movementManager;

    public EntityManager() {
        this.entities = new ArrayList<>();
    }

    public void linkManagers(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    public void addEntity(Entity e) { 
        if (!entities.contains(e)) {
            entities.add(e); 
            if (movementManager != null && e instanceof iMovable) {
                movementManager.registerMovable((iMovable) e);
            }
        }
    }

    public void removeEntity(Entity e) { 
        if (entities.remove(e)) {
            if (movementManager != null && e instanceof iMovable) {
                movementManager.unregisterMovable((iMovable) e);
            }
        }
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void updateAll(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {    
            Entity e = entities.get(i);
            if (e.isActive()) {
                e.update(deltaTime);
            }
        }
    }

    public void clear() {
        if (movementManager != null) {
            for (Entity e : entities) {
                if (e instanceof iMovable) movementManager.unregisterMovable((iMovable) e);
            }
        }
        entities.clear();
    }
    
    public void dispose() {
        // Resources moved to RenderManager
    }
}