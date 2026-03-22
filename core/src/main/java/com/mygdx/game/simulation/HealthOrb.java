package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;

public class HealthOrb extends Circle {

    public HealthOrb(int id, Vector2 position) {
        // Bright green orb
        super(id, "HealthOrb", position, 15f, new Color(0.2f, 1f, 0.2f, 1f));
        // Slowly float to the left
        setVelocityX(-100f); 
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (getX() < -50f) {
            setActive(false);
        }
    }

    @Override
    public void onCollision(Entity other) {
        // The player handles the actual healing logic
        if (other.getName().equals("Player")) {
            setActive(false);
        }
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Draw the main green circle
        shapeRenderer.setColor(color);
        shapeRenderer.circle(getX(), getY(), radius);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        
        float thickness = radius * 0.35f; // How thick the plus lines are
        float length = radius * 1.3f;     // How long the plus lines are
        
        // Horizontal bar of the plus
        shapeRenderer.rect(getX() - length / 2, getY() - thickness / 2, length, thickness);
        // Vertical bar of the plus
        shapeRenderer.rect(getX() - thickness / 2, getY() - length / 2, thickness, length);
        // ------------------------------------
    }
}