package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;

public class RectangleEntity extends Entity {
    private float width;
    private float height;
    private Color color;
 
    private static ShapeRenderer shapeRenderer; 

    public RectangleEntity(int id, String name, Vector2 position, float width, float height, Color color) {
        super(id, name, position);
        this.width = width;
        this.height = height;
        this.color = color;
        
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
    }

    @Override
    public Rectangle getBounds() {
      
        return new Rectangle(getPosition().x, getPosition().y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
      
        if (getName().equals("Trampoline") && other.getName().equals("Ball")) {
            System.out.println("Trampoline BOING!");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
       
        if (batch.isDrawing()) batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getPosition().x, getPosition().y, width, height);
        shapeRenderer.end();
        
        batch.begin();
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); 
    }
    
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}