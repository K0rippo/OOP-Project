package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Interface for renderable entities.
 * Supports interface segregation: not all entities need to render.
 */
public interface IRenderable {
    void render(SpriteBatch batch);
    void renderShape(ShapeRenderer shapeRenderer);
}
