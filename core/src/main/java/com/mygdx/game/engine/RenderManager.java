package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.List;

public class RenderManager {
    private final ShapeRenderer shapeRenderer;

    public RenderManager() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, List<Entity> entities) {
        // Draw all sprites first
        for (Entity e : entities) {
            if (e.isActive()) {
                e.render(batch);
            }
        }

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw geometric shapes
        for (Entity e : entities) {
            if (e.isActive()) {
                e.renderShape(shapeRenderer);
            }
        }

        shapeRenderer.end();
        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}