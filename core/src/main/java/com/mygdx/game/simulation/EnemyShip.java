package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class EnemyShip extends RectangleEntity {

    private static final float SCREEN_WIDTH = 1280f;
    private static final int MAX_SHOTS_PER_WAVE = 2;

    private final float baseY;
    private final float bobAmplitude;
    private final float bobSpeed;

    private final float inactiveMoveSpeed;
    private final float activeMoveSpeed;

    private final float firstShotDelay;
    private final float fireInterval;
    private float fireTimer = 0f;

    private final BulletPattern bulletPattern;

    private float bobTime = 0f;
    private boolean waveActive = false;
    private boolean hasEnteredScreen = false;
    private int shotsFired = 0;

    public EnemyShip(int id,
                     Vector2 position,
                     float inactiveMoveSpeed,
                     float activeMoveSpeed,
                     float firstShotDelay,
                     float fireInterval,
                     float bobAmplitude,
                     BulletPattern bulletPattern) {
        super(id, "EnemyShip", position, 58f, 34f, Color.CLEAR);

        this.baseY = position.y;
        this.bobAmplitude = bobAmplitude;
        this.bobSpeed = 2.3f;

        this.inactiveMoveSpeed = inactiveMoveSpeed;
        this.activeMoveSpeed = activeMoveSpeed;

        this.firstShotDelay = firstShotDelay;
        this.fireInterval = fireInterval;
        this.bulletPattern = bulletPattern;

        getVelocity().x = -inactiveMoveSpeed;
        getVelocity().y = 0f;
    }

    public void setWaveActive(boolean active) {
        waveActive = active;

        if (active) {
            getVelocity().x = -activeMoveSpeed;
            fireTimer = 0f;
            shotsFired = 0;
            hasEnteredScreen = false;
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
        getPosition().y = baseY + MathUtils.sin(bobTime * bobSpeed) * bobAmplitude;

        if (!hasEnteredScreen && getPosition().x < SCREEN_WIDTH) {
            hasEnteredScreen = true;
            fireTimer = 0f;
        }

        if (waveActive && hasEnteredScreen && shotsFired < MAX_SHOTS_PER_WAVE) {
            fireTimer += deltaTime;
        }

        if (getPosition().x + getWidth() < -80f) {
            setActive(false);
        }
    }

    public boolean shouldFire() {
        if (!waveActive || !hasEnteredScreen || shotsFired >= MAX_SHOTS_PER_WAVE) {
            return false;
        }

        if (shotsFired == 0) {
            return fireTimer >= firstShotDelay;
        }

        return fireTimer >= fireInterval;
    }

    public Array<EnemyBullet> fire(int firstBulletId, int bulletLayer, int bulletMask) {
        fireTimer = 0f;
        shotsFired++;
        return bulletPattern.fire(this, firstBulletId, bulletLayer, bulletMask);
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