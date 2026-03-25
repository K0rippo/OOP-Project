package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class EliteBullet extends RectangleEntity {

    private static final float SPEED = 225f;
    private static final float LIFETIME = 3.0f;
    
    private final PlayerCharacter target;
    private float lifeTimer = 0f;

    public EliteBullet(int id, Vector2 position, PlayerCharacter target) {
        // Distinct purple color for elite missiles
        super(id, "EliteBullet", position, 16f, 16f, new Color(0.8f, 0.2f, 0.8f, 1f));
        this.target = target;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        lifeTimer += deltaTime;
        if (lifeTimer >= LIFETIME) {
            setActive(false);
            return;
        }

        if (target != null && target.isActive()) {
            // Calculate direction to the player
            Vector2 direction = new Vector2(
                target.getX() - getX(),
                target.getY() - getY()
            ).nor();

            // Move towards the player
            setVelocityX(direction.x * SPEED);
            setVelocityY(direction.y * SPEED);
        }

        // Cull if off screen
        if (getX() < -50f || getX() > 1400f || getY() < -50f || getY() > 800f) {
            setActive(false);
        }
    }

    @Override
    public void onCollision(Entity other) {
        if (other.getName().equals("Player")) {
            setActive(false);
        }
    }
}