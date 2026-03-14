package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class EnemyShip extends RectangleEntity {

    private final float baseY;
    private final float bobAmplitude;
    private final float bobSpeed;

    // New: 2 movement speeds
    private final float inactiveMoveSpeed;
    private final float activeMoveSpeed;

    private final float fireInterval;
    private float fireTimer = 0f;

    private final float bulletSpeed;
    private final float angleStepDegrees;
    private final int bulletsPerShot;
    private float currentAngleDeg = 180f;

    private float bobTime = 0f;
    private boolean waveActive = false;

    public EnemyShip(int id,
                     Vector2 position,
                     float inactiveMoveSpeed,
                     float activeMoveSpeed,
                     float fireInterval,
                     float bobAmplitude,
                     float bulletSpeed,
                     int bulletsPerShot,
                     float angleStepDegrees) {
        super(id, "EnemyShip", position, 58f, 34f, Color.CLEAR);

        this.baseY = position.y;
        this.bobAmplitude = bobAmplitude;
        this.bobSpeed = 2.3f;

        this.inactiveMoveSpeed = inactiveMoveSpeed;
        this.activeMoveSpeed = activeMoveSpeed;

        this.fireInterval = fireInterval;
        this.bulletSpeed = bulletSpeed;
        this.bulletsPerShot = bulletsPerShot;
        this.angleStepDegrees = angleStepDegrees;

        // Starts scrolling at normal pace
        getVelocity().x = -inactiveMoveSpeed;
        getVelocity().y = 0f;
    }

    public void setWaveActive(boolean active) {
        if (this.waveActive == active) return;

        this.waveActive = active;

        if (active) {
            getVelocity().x = -activeMoveSpeed;
            fireTimer = fireInterval; // fire almost immediately on activation
        } else {
            getVelocity().x = -inactiveMoveSpeed;
        }
    }

    public boolean isWaveActive() {
        return waveActive;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        bobTime += deltaTime;
        if (waveActive) {
            fireTimer += deltaTime;
        }

        getPosition().y = baseY + MathUtils.sin(bobTime * bobSpeed) * bobAmplitude;

        if (getPosition().x + getWidth() < -80f) {
            setActive(false);
        }
    }

    public boolean shouldFire() {
        if (!waveActive) return false;

        if (fireTimer >= fireInterval) {
            fireTimer = 0f;
            return true;
        }
        return false;
    }

    public Array<EnemyBullet> fireCircleBurst(int firstBulletId, int bulletLayer, int bulletMask) {
        Array<EnemyBullet> bullets = new Array<EnemyBullet>();

        float spawnX = getPosition().x - 6f;
        float spawnY = getPosition().y + getHeight() / 2f;

        float spacingWithinShot = 10f;
        float startOffset = -spacingWithinShot * (bulletsPerShot - 1) / 2f;

        for (int i = 0; i < bulletsPerShot; i++) {
            float angleDeg = currentAngleDeg + startOffset + (i * spacingWithinShot);
            float angleRad = angleDeg * MathUtils.degreesToRadians;

            float vx = MathUtils.cos(angleRad) * bulletSpeed;
            float vy = MathUtils.sin(angleRad) * bulletSpeed;

            EnemyBullet bullet = new EnemyBullet(
                    firstBulletId + i,
                    new Vector2(spawnX, spawnY),
                    vx,
                    vy
            );
            bullet.setCollisionLayer(bulletLayer);
            bullet.setCollisionMask(bulletMask);
            bullets.add(bullet);
        }

        currentAngleDeg += angleStepDegrees;

        while (currentAngleDeg >= 360f) currentAngleDeg -= 360f;
        while (currentAngleDeg < 0f) currentAngleDeg += 360f;

        return bullets;
    }

    @Override
    public void onCollision(Entity other) {
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        float x = getPosition().x;
        float y = getPosition().y;
        float w = getWidth();
        float h = getHeight();

        shapeRenderer.setColor(new Color(0.58f, 0.64f, 0.82f, 1f));
        shapeRenderer.rect(x + 10f, y + 8f, w - 20f, h - 16f);

        shapeRenderer.setColor(new Color(0.78f, 0.82f, 0.95f, 1f));
        shapeRenderer.triangle(
                x + 10f, y + 8f,
                x + 10f, y + h - 8f,
                x - 10f, y + h / 2f
        );

        shapeRenderer.setColor(new Color(0.34f, 0.42f, 0.68f, 1f));
        shapeRenderer.triangle(
                x + 26f, y + h - 4f,
                x + 42f, y + h - 4f,
                x + 34f, y + h + 10f
        );

        shapeRenderer.triangle(
                x + 26f, y + 4f,
                x + 42f, y + 4f,
                x + 34f, y - 10f
        );

        shapeRenderer.setColor(new Color(1f, 0.45f, 0.35f, 1f));
        shapeRenderer.rect(x + 24f, y + 13f, 12f, 8f);

        shapeRenderer.setColor(new Color(0.20f, 0.24f, 0.35f, 1f));
        shapeRenderer.rect(x + w - 10f, y + 12f, 8f, 10f);
    }
}