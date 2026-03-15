package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;

/**
 * PlayerCharacter — the player-controlled entity.
 *
 * Encapsulation: all state flags are private; external code reads state via
 * query methods (hasTakenDamage, hasReachedGate, isShootRequested) and
 * consumes it via consume* methods. No public mutable fields are exposed.
 */
public class PlayerCharacter extends Circle {

    private static final float WORLD_HEIGHT        = 720f;
    private static final float WORLD_WIDTH         = 1280f;
    private static final float INVULNERABILITY_TIME = 3.0f;
    private static final float GATE_COOLDOWN_TIME   = 2.0f;
    private static final float BOUNCE_BACK_SPEED    = -140f;
    private static final float BOUNCE_RECOVERY      = 220f;

    private final Texture texture;
    private float invulnerabilityTimer = 0f;
    private float gateCooldown         = 0f;

    // ── Private state flags (encapsulated) ──────────────────────────────────
    private boolean tookDamage      = false;
    private boolean reachedGate     = false;
    private boolean shootRequested  = false;

    public PlayerCharacter(int id, Vector2 position, float radius) {
        super(id, "Player", position, radius, Color.CLEAR);
        this.texture = new Texture("player.png");
        getVelocity().x = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (invulnerabilityTimer > 0) invulnerabilityTimer -= deltaTime;
        if (gateCooldown > 0)         gateCooldown         -= deltaTime;

        float topLimit    = WORLD_HEIGHT - radius;
        float bottomLimit = radius;
        float rightLimit  = WORLD_WIDTH - radius;
        float leftLimit   = radius;

        if (getPosition().y > topLimit) {
            getPosition().y = topLimit;
            getVelocity().y = 0;
        } else if (getPosition().y < bottomLimit) {
            getPosition().y = bottomLimit;
            getVelocity().y = 0;
        }

        if (getPosition().x > rightLimit) {
            getPosition().x = rightLimit;
            getVelocity().x = 0;
        } else if (getPosition().x < leftLimit) {
            getPosition().x = leftLimit;
            getVelocity().x = 0;
        }

        // Smooth bounce-back recovery after damage
        if (getVelocity().x < 0) {
            getVelocity().x += BOUNCE_RECOVERY * deltaTime;
            if (getVelocity().x > 0) getVelocity().x = 0;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Blink while invulnerable
        if (invulnerabilityTimer > 0 && ((int) (invulnerabilityTimer * 12) % 2 == 0)) return;
        float size = radius * 2;
        batch.draw(texture, getPosition().x - radius, getPosition().y - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        String name = other.getName();

        // CorrectWall is only reachable once the CorrectBarrier in front of it
        // has been fully destroyed (inactive). It registers a successful answer.
        if (name.equals("CorrectWall")) {
            if (!reachedGate && gateCooldown <= 0f) {
                reachedGate  = true;
                gateCooldown = GATE_COOLDOWN_TIME;
            }
            return;
        }

        // Both barrier types are solid obstacles the player must shoot down.
        // Hitting either one while it is still standing costs a life —
        // this forces the player to break the correct barrier before passing.
        if (name.equals("CorrectBarrier") || name.equals("WrongBarrier")) {
            if (!isPlayerCentreInsideWall(other)) return;
            applyDamage();
            return;
        }

        // WrongWall behind a destroyed WrongBarrier — still penalises the player.
        if (name.equals("WrongWall")) {
            if (!isPlayerCentreInsideWall(other)) return;
            applyDamage();
            return;
        }

        // No other hazard types currently in use.
    }

    // ── State query methods (read-only access for GameScene) ─────────────────

    /** Returns true if the player was damaged since the last consumeDamage() call. */
    public boolean hasTakenDamage() { return tookDamage; }

    /** Returns true if the player reached a correct gate since the last consumeGoal() call. */
    public boolean hasReachedGate() { return reachedGate; }

    /** Returns true if the player has requested a shot since the last consumeShoot() call. */
    public boolean isShootRequested() { return shootRequested; }

    // ── State consume methods (reset flags after GameScene reads them) ────────

    public void consumeDamage() { tookDamage = false; }
    public void consumeGoal()   { reachedGate = false; }
    public void consumeShoot()  { shootRequested = false; }

    /** Called by input bindings to request firing a bullet. */
    public void requestShoot() { shootRequested = true; }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Returns true only if this player's Y centre is strictly inside the
     * middle 80% of the wall's vertical extent.
     * Using 80% (10% margin each side) prevents penalising a player whose
     * circle grazes the very edge of a wrong-lane wall while in the correct lane.
     */
    private boolean isPlayerCentreInsideWall(Entity other) {
        if (!(other instanceof com.mygdx.game.engine.RectangleEntity)) return true;
        com.mygdx.game.engine.RectangleEntity rect = (com.mygdx.game.engine.RectangleEntity) other;
        float playerY = getPosition().y;
        float bottom  = rect.getPosition().y;
        float height  = rect.getHeight();
        float margin  = height * 0.10f;
        return playerY >= (bottom + margin) && playerY <= (bottom + height - margin);
    }

    private void applyDamage() {
        if (invulnerabilityTimer <= 0f) {
            tookDamage         = true;
            invulnerabilityTimer = INVULNERABILITY_TIME;
            getVelocity().x    = BOUNCE_BACK_SPEED;
        }
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
