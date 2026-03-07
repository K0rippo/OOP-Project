package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;
public class PlayerCharacter extends Circle {

    public boolean isLevelComplete = false;
    public boolean hitWrongWall = false; 
    private float defaultSpeedX;
    private final float WORLD_HEIGHT = 600f; 
    private Texture texture;

    public PlayerCharacter(int id, Vector2 position, float radius, float speedX) {
        super(id, "Player", position, radius, Color.CLEAR); // Make base shape invisible
        this.defaultSpeedX = speedX;
        getVelocity().x = speedX;
        
        // Load the character image
        this.texture = new Texture("player.png");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (getPosition().y + radius > WORLD_HEIGHT) {
            getPosition().y = WORLD_HEIGHT - radius;
            getVelocity().y = 0;
        } else if (getPosition().y - radius < 0) {
            getPosition().y = radius;
            getVelocity().y = 0;
        }

        if (getVelocity().x < defaultSpeedX) {
            getVelocity().x += 150f * deltaTime; 
            if (getVelocity().x > defaultSpeedX) {
                getVelocity().x = defaultSpeedX;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Draw the PNG centered on the collision radius
        float size = radius * 2;
        batch.draw(texture, getPosition().x - radius, getPosition().y - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        if (other.getName().equals("WrongWall")) {
            getVelocity().x = -150f; 
            hitWrongWall = true; 
        } else if (other.getName().equals("CorrectWall")) {
            isLevelComplete = true; 
        }
    }

    // Free memory when destroyed
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}