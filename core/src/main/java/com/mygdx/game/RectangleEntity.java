package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class RectangleEntity extends Entity {
    private float width;
    private float height;
    private ShapeRenderer shapeRenderer;
    private Color color;

    public RectangleEntity(int id, String name, Vector2 position, float width, float height, Color color) {
        super(id, name, position);
        this.width = width;
        this.height = height;
        this.color = color;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(SpriteBatch batch, EntityManager entityManager) {
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getPosition().x, getPosition().y, width, height);
        shapeRenderer.end();
        batch.begin();
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}