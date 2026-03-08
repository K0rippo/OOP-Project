package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class RectangleEntity extends MovableEntity {
    private float width;
    private float height;
    private Color color;

    public RectangleEntity(int id, String name, Vector2 position, float width, float height, Color color) {
        super(id, name, position);
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getPosition().x, getPosition().y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
    }

    @Override
    public void render(SpriteBatch batch) {
    }
    
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getPosition().x, getPosition().y, width, height);
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); 
    }
    
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}