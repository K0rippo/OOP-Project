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

    private static final float BULLET_LIFETIME = 9.0f;
    private float lifeTimer = 0f;

    public EnemyBullet(int id,
                       Vector2 position,
                       float velocityX,
                       float velocityY) {
        super(id, "EnemyBullet", position, 12f, 12f, new Color(1f, 0.35f, 0.25f, 1f));
        setVelocityX(velocityX);
        setVelocityY(velocityY);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //expire bullets even if they stay inside bounds
        lifeTimer += deltaTime;

        if (lifeTimer >= BULLET_LIFETIME) {
            setActive(false);
            return;
        }

        if (getX() < WORLD_MIN_X ||
            getX() > WORLD_MAX_X ||
            getY() < WORLD_MIN_Y ||
            getY() > WORLD_MAX_Y) {
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