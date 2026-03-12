package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManager {
    
    private final List<Entity> entities;
    private MovementManager movementManager;

    public EntityManager() {
        this.entities = new ArrayList<>();
    }

    // Assigns movement manager link
    public void linkManagers(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    // Registers entity in active list and movement systems
    public void addEntity(Entity e) { 
        if (!entities.contains(e)) {
            entities.add(e); 
            if (movementManager != null && e instanceof iMovable) {
                movementManager.registerMovable((iMovable) e);
            }
        }
    }

    // Removes entity from active list and movement systems
    public void removeEntity(Entity e) { 
        if (entities.remove(e)) {
            if (movementManager != null && e instanceof iMovable) {
                movementManager.unregisterMovable((iMovable) e);
            }
        }
    }

    // Retrieves an unmodifiable list of active entities
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    // Filters and returns entities matching a specific collision layer
    public List<Entity> getEntitiesByLayer(int layer) {
        return entities.stream()
            .filter(e -> e.getCollisionLayer() == layer)
            .collect(Collectors.toList());
    }

    // Iterates through active entities to trigger frame updates
    public void updateAll(float deltaTime) {
        for (int i = entities.size() - 1; i >= 0; i--) {    
            Entity e = entities.get(i);
            if (e.isActive()) {
                e.update(deltaTime);
            } else {
                removeEntity(e); 
            }
        }
    }

    // Purges all entities and unregisters them from subsystems
    public void clear() {
        if (movementManager != null) {
            for (Entity e : entities) {
                if (e instanceof iMovable) movementManager.unregisterMovable((iMovable) e);
            }
        }
        entities.clear();
    }
}