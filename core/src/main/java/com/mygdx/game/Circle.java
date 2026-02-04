package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Circle extends Entity {
	
    private float radius;
    private ShapeRenderer shapeRenderer;

    public Circle(int id, Vector2 position, float radius)
    {
        super(id, position);
        this.radius = radius;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(SpriteBatch batch)
    {
        // End the batch before using ShapeRenderer
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(getPosition().x, getPosition().y, radius);
        shapeRenderer.end();

        // Resume the batch for other entities
        batch.begin();
    }

    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime); // keeps velocity-based movement
        // You can add extra behavior here (e.g., gravity)
    }
}