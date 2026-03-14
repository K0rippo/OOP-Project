package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final Texture texture;

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
        super(id, "EnemyShip", position, 51f, 51f, Color.CLEAR);

        this.baseY = position.y;
        this.bobAmplitude = bobAmplitude;
        this.bobSpeed = 2.3f;

        this.inactiveMoveSpeed = inactiveMoveSpeed;
        this.activeMoveSpeed = activeMoveSpeed;

        this.firstShotDelay = firstShotDelay;
        this.fireInterval = fireInterval;
        this.bulletPattern = bulletPattern;

        this.texture = new Texture("enemyspaceship.png");

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
    public void render(SpriteBatch batch) {
        batch.draw(
                texture,
                getPosition().x,
                getPosition().y,
                getWidth(),
                getHeight()
        );
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // leave empty so the old rectangle ship is not drawn
    }

    public void dispose() {
        texture.dispose();
    }
}