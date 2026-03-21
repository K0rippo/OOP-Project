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
    
    private int hitPoints = 3;

    private Runnable onDamageCallback;
    private Runnable onDeathCallback;

    public void setOnDamageCallback(Runnable callback) {
        this.onDamageCallback = callback;
    }

    // --- NEW: Setter for the death callback ---
    public void setOnDeathCallback(Runnable callback) {
        this.onDeathCallback = callback;
    }

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

        setVelocityX(-inactiveMoveSpeed);
        setVelocityY(0f);
    }

    public void setWaveActive(boolean active) {
        waveActive = active;

        if (active) {
            setVelocityX(-activeMoveSpeed);
            fireTimer = 0f;
            shotsFired = 0;
            hasEnteredScreen = false;
        } else {
            setVelocityX(-inactiveMoveSpeed);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        bobTime += deltaTime;
        setY(baseY + MathUtils.sin(bobTime * bobSpeed) * bobAmplitude);

        if (!hasEnteredScreen && getX() < SCREEN_WIDTH) {
            hasEnteredScreen = true;
            fireTimer = 0f;
        }

        if (waveActive && hasEnteredScreen && shotsFired < MAX_SHOTS_PER_WAVE) {
            fireTimer += deltaTime;
        }

        if (getX() + getWidth() < -80f) {
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
        if (!isActive()) return;

        if (other instanceof PlayerBullet && other.isActive()) {
            hitPoints--;
            other.setActive(false); 
            
            if (onDamageCallback != null) {
                onDamageCallback.run();
            }

            if (hitPoints <= 0) {
                setActive(false); 
                
                if (onDeathCallback != null) {
                    onDeathCallback.run();
                }
                // --------------------------------------------
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive()) return;
        batch.draw(
                texture,
                getX(),
                getY(),
                getWidth(),
                getHeight()
        );
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
    }

    public void dispose() {
        texture.dispose();
    }
}