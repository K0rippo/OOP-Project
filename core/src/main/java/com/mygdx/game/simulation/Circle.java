package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;

public class Circle extends Entity {
    
    private float radius;
    private Color color;
    private static ShapeRenderer shapeRenderer;
    
    // PHYSICS SETTINGS
    private static final float BALL_GRAVITY = 600f; 
    private static final float COIN_GRAVITY = 200f; 
    private static final float BOUNCE_DAMPING = 0.8f; 

    public Circle(int id, String name, Vector2 position, float radius, Color color) {
        super(id, name, position);
        this.radius = radius;
        this.color = color;
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void update(float deltaTime) {
        float gravity = getName().contains("Coin") ? COIN_GRAVITY : BALL_GRAVITY;
        getVelocity().y -= gravity * deltaTime;
        super.update(deltaTime);

        if (getName().contains("Ball")) {
            handleBallBounds();
        } else if (getName().contains("Coin")) {
            handleCoinBounds();
        }
    }

    private void handleBallBounds() {
        // Floor Logic with Resting Threshold
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; 
            
            // Stop completely if bounce is weak
            if (Math.abs(getVelocity().y) < 100) {
                getVelocity().y = 0;
            } else {
                getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
            }
        }
        
        // Right Edge
        if (getPosition().x + radius > Gdx.graphics.getWidth()) {
            getPosition().x = Gdx.graphics.getWidth() - radius;
            getVelocity().x *= -1;
        }
        // Left Edge
        if (getPosition().x - radius < 0) {
            getPosition().x = radius;
            getVelocity().x *= -1;
        }
    }

    private void handleCoinBounds() {
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; 
            if (Math.abs(getVelocity().y) < 50) {
                getVelocity().y = 0;
            } else {
                getVelocity().y = Math.abs(getVelocity().y) * 0.5f;
            }
            getVelocity().x *= 0.9f;
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getPosition().x - radius, getPosition().y - radius, radius * 2, radius * 2);
    }

    @Override
    public void onCollision(Entity other) {
        if (this.getName().contains("Ball")) {
            
            if (other.getName().contains("Wall") || other.getName().contains("Trampoline")) {
                
                // --- Console Log ---
                if (other.getName().contains("Wall")) {
                    System.out.println("Hitting the wall!");
                }
                // ------------------------

                // Collision Physics (Overlap Calculation)
                Rectangle myBounds = this.getBounds();
                Rectangle otherBounds = other.getBounds();
                
                float minX = Math.max(myBounds.x, otherBounds.x);
                float maxX = Math.min(myBounds.x + myBounds.width, otherBounds.x + otherBounds.width);
                float minY = Math.max(myBounds.y, otherBounds.y);
                float maxY = Math.min(myBounds.y + myBounds.height, otherBounds.y + otherBounds.height);
                
                float overlapWidth = maxX - minX;
                float overlapHeight = maxY - minY;

                // Hit Side (Flip X)
                if (overlapWidth < overlapHeight) {
                    getVelocity().x *= -1;
                    if (getPosition().x < other.getPosition().x) {
                        getPosition().x -= overlapWidth;
                    } else {
                        getPosition().x += overlapWidth;
                    }
                } 
                // Hit Top/Bottom (Flip Y)
                else {
                    getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
                    
                    if (getPosition().y > other.getPosition().y) {
                         getPosition().y += overlapHeight;
                    } else {
                         getPosition().y -= overlapHeight;
                    }

                    if (other.getName().contains("Trampoline")) {
                         getVelocity().y += 120;
                    }
                }
            }
            
            if (other.getName().contains("Coin")) {
                other.setActive(false);
                System.out.println("Coin Collected!");
            }
        }
        
        if (this.getName().contains("Coin") && other.getName().contains("Ball")) {
            this.setActive(false);
        }
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