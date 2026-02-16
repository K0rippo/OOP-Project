package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;

public class Ball extends Circle {

    private static final float GRAVITY = 600f;
    
    // --- UPDATED BOUNCE SETTINGS ---
    private static final float FLOOR_DAMPING = 0.6f;       // Set to 0.6
    private static final float TRAMPOLINE_DAMPING = 0.7f;  // Set to 0.7
    
    private static final float MAX_VELOCITY = 450f; 
    private static final float TRAMPOLINE_BOOST = 80f; 

    public Ball(int id, Vector2 position, float radius, Color color) {
        super(id, "Ball", position, radius, color);
    }

    @Override
    public void update(float deltaTime) {
        // Gravity
        getVelocity().y -= GRAVITY * deltaTime;
        super.update(deltaTime);

        // --- FLOOR BOUNCE ---
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; // Snap to floor
            
            if (Math.abs(getVelocity().y) < 100) {
                getVelocity().y = 0; 
            } else {
                getVelocity().y = Math.abs(getVelocity().y) * FLOOR_DAMPING;
                
                if (getVelocity().y > MAX_VELOCITY) {
                    getVelocity().y = MAX_VELOCITY;
                }
            }
        }

        // Wall Bounce (Sides)
        if (getPosition().x + radius > Gdx.graphics.getWidth()) {
            getPosition().x = Gdx.graphics.getWidth() - radius;
            getVelocity().x *= -1;
        }
        if (getPosition().x - radius < 0) {
            getPosition().x = radius;
            getVelocity().x *= -1;
        }
    }

    @Override
    public void onCollision(Entity other) {
        if (other.getName().contains("Wall") || other.getName().contains("Trampoline")) {
            resolveCollision(other);
        }
    }

    private void resolveCollision(Entity other) {
        Rectangle myBounds = this.getBounds();
        Rectangle otherBounds = other.getBounds();
        
        float minX = Math.max(myBounds.x, otherBounds.x);
        float maxX = Math.min(myBounds.x + myBounds.width, otherBounds.x + otherBounds.width);
        float minY = Math.max(myBounds.y, otherBounds.y);
        float maxY = Math.min(myBounds.y + myBounds.height, otherBounds.y + otherBounds.height);
        
        float overlapWidth = maxX - minX;
        float overlapHeight = maxY - minY;

        if (overlapWidth < overlapHeight) {
            // Horizontal Collision
            getVelocity().x *= -1;
            if (getPosition().x < other.getPosition().x) getPosition().x -= overlapWidth;
            else getPosition().x += overlapWidth;
        } else {
            // Vertical Collision
            float otherCenterY = otherBounds.y + otherBounds.height / 2f;

            if (getPosition().y > otherCenterY) {
                // Hit the TOP
                getPosition().y += overlapHeight;
                
                if (other.getName().contains("Trampoline")) {
                    getVelocity().y = Math.abs(getVelocity().y) * TRAMPOLINE_DAMPING;
                    getVelocity().y += TRAMPOLINE_BOOST;
                } else {
                    getVelocity().y = Math.abs(getVelocity().y) * FLOOR_DAMPING;
                }

                if (getVelocity().y > MAX_VELOCITY) {
                    getVelocity().y = MAX_VELOCITY;
                }

            } else {
                // Hit the BOTTOM
                getPosition().y -= overlapHeight;
                getVelocity().y = -Math.abs(getVelocity().y) * FLOOR_DAMPING; 
            }
        }
    }
}