package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Circle extends MovableEntity {
    
    protected float radius; 
    protected Color color;

    public Circle(int id, String name, Vector2 position, float radius, Color color) {
        super(id, name, position);
        this.radius = radius;
        this.color = color;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getPosition().x - radius, getPosition().y - radius, radius * 2, radius * 2);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Keep empty. PlayerCharacter overrides this to draw its sprite.
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(getPosition().x, getPosition().y, radius);
    }
}