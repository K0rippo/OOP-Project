package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    
    private final List<Entity> entities;
    private final ShapeRenderer shapeRenderer;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void addEntity(Entity e) { entities.add(e); }
    public void removeEntity(Entity e) { entities.remove(e); }

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

    public void renderAll(SpriteBatch batch) {
        // Loop 1: Draw all sprites
        for (Entity e : entities) {
            if (e.isActive()) {
                e.render(batch); 
            }
        }

        // Pause batch, prepare shape renderer
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Loop 2: Draw all shapes
        for (Entity e : entities) {
            if (e.isActive()) {
                e.renderShape(shapeRenderer);
            }
        }

        // End shapes, resume batch
        shapeRenderer.end();
        batch.begin();
    }

    public void clear() {
        entities.clear();
    }
    
    public void dispose() {
        shapeRenderer.dispose();
    }
}