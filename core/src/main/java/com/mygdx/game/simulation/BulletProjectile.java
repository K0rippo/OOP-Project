package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class BulletProjectile extends RectangleEntity {

    public BulletProjectile(int id, Vector2 position, float speedX) {
        super(id, "Bullet", position, 18f, 8f, new Color(1f, 0.75f, 0.2f, 1f));
        getVelocity().x = speedX;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (getPosition().x + getWidth() < -30f) {
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
