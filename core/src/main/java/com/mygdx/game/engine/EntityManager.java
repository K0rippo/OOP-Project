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

    public List<Entity> getEntitiesByLayer(int layer) {
        return entities.stream()
            .filter(e -> e.getCollisionLayer() == layer)
            .collect(Collectors.toList());
    }

    public void updateAll(float deltaTime) {
        //iterate backwards so inactive removals are safe
        for (int i = entities.size() - 1; i >= 0; i--) {    
            Entity e = entities.get(i);
            if (e.isActive()) {
                e.update(deltaTime);
            } else {
                removeEntity(e); 
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
}