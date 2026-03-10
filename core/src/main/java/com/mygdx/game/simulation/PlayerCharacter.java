package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;

public class PlayerCharacter extends Circle {

    public boolean reachedGate = false;
    public boolean tookDamage = false;

    private final float WORLD_HEIGHT = 600f;
    private Texture texture;
    private float invulnerabilityTimer = 0f;
    
    public boolean shootRequested = false;

    public PlayerCharacter(int id, Vector2 position, float radius) {
        super(id, "Player", position, radius, Color.CLEAR);
        this.texture = new Texture("player.png");
        getVelocity().x = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
        }

        float topLimit = WORLD_HEIGHT - radius;
        float bottomLimit = radius;

        if (getPosition().y > topLimit) {
            getPosition().y = topLimit;
            getVelocity().y = 0;
        } else if (getPosition().y < bottomLimit) {
            getPosition().y = bottomLimit;
            getVelocity().y = 0;
        }

        // Small bounce-back recovery after damage
        if (getVelocity().x < 0) {
            getVelocity().x += 220f * deltaTime;
            if (getVelocity().x > 0) {
                getVelocity().x = 0;
            }
        } else {
            getVelocity().x = 0;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Blink while invulnerable
        if (invulnerabilityTimer > 0 && ((int)(invulnerabilityTimer * 12) % 2 == 0)) {
            return;
        }

        float size = radius * 2;
        batch.draw(texture, getPosition().x - radius, getPosition().y - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        String name = other.getName();

        if (name.equals("CorrectWall")) {
            reachedGate = true;
            return;
        }

        if (name.equals("WrongWall") || name.equals("Bullet") || name.equals("Cannon")
                || name.equals("WrongBarrier") || name.equals("CorrectBarrier")) {
            if (invulnerabilityTimer <= 0f) {
                tookDamage = true;
                invulnerabilityTimer = 1.0f;
                getVelocity().x = -140f;
            }
        }
    }

    public void consumeDamage() {
        tookDamage = false;
    }

    public void consumeGoal() {
        reachedGate = false;
    }
    
    public void requestShoot() {
        shootRequested = true;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}