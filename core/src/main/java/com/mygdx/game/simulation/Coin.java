package com.mygdx.game.simulation;

import com.badlogic.gdx.audio.Sound; // Import added
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;

public class Coin extends Circle {

    private static final float GRAVITY = 200f;
    private static final float BOUNCE_DAMPING = 0.3f; 
    
    // 1. Add a field for the sound
    private Sound collectSound;

    // 2. Update constructor to accept the Sound
    public Coin(int id, Vector2 position, float radius, Sound collectSound) {
        super(id, "Coin", position, radius, Color.GOLD);
        this.collectSound = collectSound;
    }

    @Override
    public void update(float deltaTime) {
        getVelocity().y -= GRAVITY * deltaTime;
        super.update(deltaTime);
        
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; 
            if (Math.abs(getVelocity().y) < 20) {
                getVelocity().y = 0;
            } else {
                getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
            }
            getVelocity().x *= 0.9f;
        }
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Ball) { 
            // 3. Play the sound when collected
            // Only play if sound is not null and game is not muted
            if (collectSound != null && !GameMaster.isMuted) {
                collectSound.play(0.5f); // 0.5f is 50% volume
            }
            
            System.out.println("Coin Collected!");
            this.setActive(false);
        }
    }
}