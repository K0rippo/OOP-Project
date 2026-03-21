package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class PlayerCharacter extends Circle {

    private static final float WORLD_HEIGHT         = 720f;
    private static final float WORLD_WIDTH          = 1280f;
    private static final float INVULNERABILITY_TIME = 3.0f;
    private static final float GATE_COOLDOWN_TIME   = 2.0f;
    
    private static final float BOUNCE_LOCK_TIME     = 0.2f;

    private static final float BOUNCE_BACK_SPEED    = -1200f;

    private final Texture texture;

    private float invulnerabilityTimer = 0f;
    private float gateCooldown         = 0f;
    private float controlLockTimer     = 0f;

    private boolean tookDamage     = false;
    private boolean reachedGate    = false;
    private boolean shootRequested = false;

    // --- NEW SOUND CALLBACKS ---
    private Runnable onDamageCallback;
    private Runnable onGateCallback;

    public void setSoundCallbacks(Runnable onDamage, Runnable onGate) {
        this.onDamageCallback = onDamage;
        this.onGateCallback = onGate;
    }
    // ---------------------------

    public PlayerCharacter(int id, Vector2 position, float radius) {
        super(id, "Player", position, radius, Color.CLEAR);
        this.texture = new Texture("spaceship.png");
        setVelocityX(0f);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (invulnerabilityTimer > 0) invulnerabilityTimer -= deltaTime;
        if (gateCooldown > 0) gateCooldown -= deltaTime;
        if (controlLockTimer > 0) controlLockTimer -= deltaTime;

        float topLimit    = WORLD_HEIGHT - radius;
        float bottomLimit = radius;
        float rightLimit  = WORLD_WIDTH - radius;
        float leftLimit   = radius;

        //clamp player inside world bounds
        if (getY() > topLimit) {
            setY(topLimit);
            setVelocityY(0f);
        } else if (getY() < bottomLimit) {
            setY(bottomLimit);
            setVelocityY(0f);
        }

        if (getX() > rightLimit) {
            setX(rightLimit);
            setVelocityX(0f);
        } else if (getX() < leftLimit) {
            setX(leftLimit);
            setVelocityX(0f);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (invulnerabilityTimer > 0 && ((int) (invulnerabilityTimer * 12) % 2 == 0)) return;

        float size = radius * 2f;
        batch.draw(texture, getX() - radius, getY() - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof AnswerGate) {
            AnswerGate gate = (AnswerGate) other;
            if (!reachedGate && gateCooldown <= 0f) {
                if (gate.isCorrectLane()) {
                    reachedGate  = true;
                    gateCooldown = GATE_COOLDOWN_TIME;
                    if (onGateCallback != null) onGateCallback.run(); // Play gate sound!
                } else if (isPlayerCentreInsideWall(gate)) {
                    applyWallBounce(gate);
                    applyDamage();
                }
            }
            return;
        }

        if (other instanceof BreakableBarrier) {
            if (!isPlayerCentreInsideWall(other)) return;
            applyWallBounce((RectangleEntity) other);
            applyDamage();
            return;
        }

        if (other instanceof EnemyBullet || other instanceof EnemyShip || other instanceof BulletProjectile) {
            applyDamage();
        }
    }

    public boolean hasTakenDamage() {
        return tookDamage;
    }

    public boolean hasReachedGate() {
        return reachedGate;
    }

    public boolean isShootRequested() {
        return shootRequested;
    }

    public boolean isControlsLocked() {
        return controlLockTimer > 0f;
    }

    public void consumeDamage() {
        tookDamage = false;
    }

    public void consumeGoal() {
        reachedGate = false;
    }

    public void consumeShoot() {
        shootRequested = false;
    }

    public void requestShoot() {
        shootRequested = true;
    }

    private boolean isPlayerCentreInsideWall(Entity other) {
        if (!(other instanceof RectangleEntity)) return true;
        RectangleEntity rect = (RectangleEntity) other;
        float playerY = getY();
        float bottom  = rect.getY();
        float height  = rect.getHeight();
        float margin  = height * 0.10f;

        return playerY >= (bottom + margin) && playerY <= (bottom + height - margin);
    }

    private void applyDamage() {
        if (invulnerabilityTimer <= 0f) {
            tookDamage           = true;
            invulnerabilityTimer = INVULNERABILITY_TIME;
            applyKnockback();
            if (onDamageCallback != null) onDamageCallback.run(); // Play damage sound!
        }
    }

    private void applyWallBounce(RectangleEntity wall) {
        float maxAllowedX = wall.getX() - radius - 1f;
        if (getX() > maxAllowedX) {
            setX(maxAllowedX);
        }

        applyKnockback();
    }

    private void applyKnockback() {
        if (controlLockTimer < BOUNCE_LOCK_TIME) {
            controlLockTimer = BOUNCE_LOCK_TIME;
        }
        setVelocityX(BOUNCE_BACK_SPEED);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}