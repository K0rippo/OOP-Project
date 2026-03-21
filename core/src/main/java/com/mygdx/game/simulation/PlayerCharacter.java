package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;

public class PlayerCharacter extends Circle {

    public boolean reachedGate = false;
    public boolean tookDamage  = false;
    public boolean shootRequested = false;

    private static final float WORLD_HEIGHT       = 720f;
    private static final float DAMAGE_COOLDOWN    = 0.5f; // per-entity re-hit delay
    private static final float GATE_COOLDOWN      = 0.8f; // prevent gate re-trigger
    private static final float GLOBAL_INVULN      = 1.0f; // post-damage grace period

    private Texture texture;
    private float globalInvulnTimer = 0f;
    private float gateCooldown      = 0f;

    /** Tracks when each hazard entity last damaged the player (keyed by entity id). */
    private final ObjectFloatMap<Integer> hitCooldowns = new ObjectFloatMap<>();

    public PlayerCharacter(int id, Vector2 position, float radius) {
        super(id, "Player", position, radius, Color.CLEAR);
        this.texture = new Texture("player.png");
        getVelocity().x = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (globalInvulnTimer > 0) globalInvulnTimer -= deltaTime;
        if (gateCooldown      > 0) gateCooldown      -= deltaTime;

        // Tick down all per-entity hit cooldowns
        for (ObjectFloatMap.Entry<Integer> e : hitCooldowns) {
            hitCooldowns.put(e.key, e.value - deltaTime);
        }

        // Boundary clamping
        float topLimit    = WORLD_HEIGHT - radius;
        float bottomLimit = radius;
        if (getPosition().y > topLimit)    { getPosition().y = topLimit;    getVelocity().y = 0; }
        if (getPosition().y < bottomLimit) { getPosition().y = bottomLimit; getVelocity().y = 0; }

        // Bounce-back recovery after damage
        if (getVelocity().x < 0) {
            getVelocity().x += 220f * deltaTime;
            if (getVelocity().x > 0) getVelocity().x = 0;
        } else {
            getVelocity().x = 0;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Blink while globally invulnerable
        if (globalInvulnTimer > 0 && ((int)(globalInvulnTimer * 12) % 2 == 0)) return;
        float size = radius * 2;
        batch.draw(texture, getPosition().x - radius, getPosition().y - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        String name = other.getName();

        if (name.equals("CorrectWall")) {
            if (!reachedGate && gateCooldown <= 0f) {
                reachedGate  = true;
                gateCooldown = GATE_COOLDOWN;
            }
            return;
        }

        boolean isHazard = name.equals("WrongWall")    || name.equals("Bullet")
                        || name.equals("Cannon")        || name.equals("WrongBarrier")
                        || name.equals("CorrectBarrier");

        if (isHazard && globalInvulnTimer <= 0f) {
            int    entityId      = other.getId();
            float  lastHit       = hitCooldowns.get(entityId, -1f);
            if (lastHit <= 0f) {
                hitCooldowns.put(entityId, DAMAGE_COOLDOWN);
                tookDamage        = true;
                globalInvulnTimer = GLOBAL_INVULN;
                getVelocity().x   = -140f;
            }
        }
    }

    public void consumeDamage() { tookDamage    = false; }
    public void consumeGoal()   { reachedGate   = false; }
    public void requestShoot()  { shootRequested = true;  }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}