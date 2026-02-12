package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;

public class Coin extends Circle {

    private static final float GRAVITY = 200f;
    private static final float BOUNCE_DAMPING = 0.5f; // Retain 50% velocity on bounce

    public Coin(int id, Vector2 position, float radius) {
        super(id, "Coin", position, radius, Color.GOLD);
    }

    @Override
    public void update(float deltaTime) {
        getVelocity().y -= GRAVITY * deltaTime;
        super.update(deltaTime);
        
        // Floor Collision with Bounce
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; // Snap to floor
            
            // If velocity is very low, stop bouncing to prevent micro-jitter
            if (Math.abs(getVelocity().y) < 20) {
                getVelocity().y = 0;
            } else {
                // Reverse velocity and reduce it by 50%
                getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
            }
            
            // Apply friction to sliding
            getVelocity().x *= 0.9f;
        }
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Ball) { 
            System.out.println("Coin Collected!");
            this.setActive(false);
        }
    }
}