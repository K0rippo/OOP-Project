package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class CannonObstacle extends RectangleEntity {

    private float shootTimer = 0f;
    private final float shootInterval;
    private final float bulletSpeed;

    public CannonObstacle(int id, Vector2 position, float shootInterval, float bulletSpeed) {
        super(id, "Cannon", position, 36f, 36f, new Color(0.20f, 0.20f, 0.24f, 1f));
        this.shootInterval = shootInterval;
        this.bulletSpeed = bulletSpeed;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (getPosition().x + getWidth() < -40f) {
            setActive(false);
        }
    }

    public boolean shouldFire(float deltaTime) {
        shootTimer += deltaTime;
        if (shootTimer >= shootInterval) {
            shootTimer = 0f;
            return true;
        }
        return false;
    }

    public BulletProjectile fire(int bulletId) {
        return new BulletProjectile(
                bulletId,
                new Vector2(getPosition().x - 10f, getPosition().y + getHeight() / 2f - 4f),
                bulletSpeed
        );
    }

    @Override
    public void onCollision(Entity other) {
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(new Color(0.22f, 0.22f, 0.28f, 1f));
        shapeRenderer.rect(getPosition().x, getPosition().y, getWidth(), getHeight());

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(getPosition().x - 14f, getPosition().y + getHeight() / 2f - 4f, 16f, 8f);
    }
}