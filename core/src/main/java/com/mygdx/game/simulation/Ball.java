package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;

public class Ball extends Circle {

    private static final float GRAVITY = 600f;
    private static final float FLOOR_DAMPING = 0.3f;
    private static final float TRAMPOLINE_DAMPING = 1f;
    private static final float WALL_DAMPING = 0.0f; // zero activity wall
    private static final float MAX_VELOCITY = 800f; 
    private static final float TRAMPOLINE_BOOST = 20f; 

    private int jumpCount = 0;

    public Ball(int id, Vector2 position, float radius, Color color) {
        super(id, "Ball", position, radius, color);
    }

    @Override
    public void update(float deltaTime) {
        getVelocity().y -= GRAVITY * deltaTime;
        super.update(deltaTime);

        //Floor bounce logic
        if (getPosition().y - radius < 0) {
            getPosition().y = radius; 
            if (getVelocity().y < 0) {
                this.setJumpCount(0);
                getVelocity().y = Math.abs(getVelocity().y) * FLOOR_DAMPING;
            }
        }

        //Ceiling bounds
        if (getPosition().y + radius > Gdx.graphics.getHeight()) {
            getPosition().y = Gdx.graphics.getHeight() - radius;
            getVelocity().y = -Math.abs(getVelocity().y) * FLOOR_DAMPING;
        }

        //Normal wall Bounce
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
            getVelocity().x = (getPosition().x < other.getPosition().x ? -1 : 1) * Math.abs(getVelocity().x) * WALL_DAMPING;
            if (getPosition().x < other.getPosition().x) getPosition().x -= overlapWidth;
            else getPosition().x += overlapWidth;
        } else {
            float otherCenterY = otherBounds.y + otherBounds.height / 2f;

            if (getPosition().y > otherCenterY) {
                getPosition().y += overlapHeight;
                
                //reset jump if we are landing on the platform
                if (getVelocity().y < 0) {
                    this.jumpCount = 0;
                }

                if (other.getName().contains("Trampoline")) {
                    getVelocity().y = Math.abs(getVelocity().y) * TRAMPOLINE_DAMPING;
                    getVelocity().y += TRAMPOLINE_BOOST;
                    this.jumpCount = 1; 
                } else {
                    getVelocity().y = Math.abs(getVelocity().y) * FLOOR_DAMPING;
                }

                //Allows trampoline to go higher than normal floor
                float currentMax = other.getName().contains("Trampoline") ? 1200f : MAX_VELOCITY;
                if (getVelocity().y > currentMax) {
                    getVelocity().y = currentMax;
                }

            } else {
                getPosition().y -= overlapHeight;
                getVelocity().y = -Math.abs(getVelocity().y) * FLOOR_DAMPING; 
            }
        }
    }

    public int getJumpCount() { return jumpCount; }
    public void setJumpCount(int count) { this.jumpCount = count; }
}