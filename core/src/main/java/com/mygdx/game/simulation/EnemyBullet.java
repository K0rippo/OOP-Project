package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class EnemyBullet extends RectangleEntity {

    private static final float WORLD_MIN_X = -80f;
    private static final float WORLD_MAX_X = 1400f;
    private static final float WORLD_MIN_Y = -80f;
    private static final float WORLD_MAX_Y = 800f;

    private static final float BULLET_LIFETIME = 9.0f; // seconds
    private float lifeTimer = 0f;

    public EnemyBullet(int id,
                       Vector2 position,
                       float velocityX,
                       float velocityY) {
        super(id, "EnemyBullet", position, 12f, 12f, new Color(1f, 0.35f, 0.25f, 1f));
        getVelocity().x = velocityX;
        getVelocity().y = velocityY;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        lifeTimer += deltaTime;

        if (lifeTimer >= BULLET_LIFETIME) {
            setActive(false);
            return;
        }

        if (getPosition().x < WORLD_MIN_X ||
            getPosition().x > WORLD_MAX_X ||
            getPosition().y < WORLD_MIN_Y ||
            getPosition().y > WORLD_MAX_Y) {
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