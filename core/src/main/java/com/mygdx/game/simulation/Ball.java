package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.Rectangle;

public class Ball extends Circle {

    private static final float GRAVITY = 600f;
    private static final float BOUNCE_DAMPING = 0.8f;

    public Ball(int id, Vector2 position, float radius, Color color) {
        super(id, "Ball", position, radius, color);
    }

    @Override
    public void update(float deltaTime) {
        // Gravity
        getVelocity().y -= GRAVITY * deltaTime;
        super.update(deltaTime);

        // Floor Bounce
        if (getPosition().y - radius < 0) {
            getPosition().y = radius;
            if (Math.abs(getVelocity().y) < 100) {
                getVelocity().y = 0;
            } else {
                getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
            }
        }

        // Wall Bounce
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
            getVelocity().x *= -1;
            if (getPosition().x < other.getPosition().x) getPosition().x -= overlapWidth;
            else getPosition().x += overlapWidth;
        } else {
            getVelocity().y = Math.abs(getVelocity().y) * BOUNCE_DAMPING;
            if (getPosition().y > other.getPosition().y) getPosition().y += overlapHeight;
            else getPosition().y -= overlapHeight;

            if (other.getName().contains("Trampoline")) getVelocity().y += 120;
        }
    }
}