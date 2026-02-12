package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;
import com.mygdx.game.engine.MovableEntity;


public abstract class Circle extends MovableEntity {
    
    protected float radius; // Protected so subclasses (Ball/Coin) can use it
    protected Color color;
    private static ShapeRenderer shapeRenderer;

    public Circle(int id, String name, Vector2 position, float radius, Color color) {
        super(id, name, position);
        this.radius = radius;
        this.color = color;
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getPosition().x - radius, getPosition().y - radius, radius * 2, radius * 2);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (batch.isDrawing()) batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(getPosition().x, getPosition().y, radius);
        shapeRenderer.end();
        batch.begin();
    }
}