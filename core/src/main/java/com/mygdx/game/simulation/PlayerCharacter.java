package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Circle;
import com.mygdx.game.engine.Entity;

public class PlayerCharacter extends Circle {

    public boolean reachedGate = false;
    public boolean tookDamage = false;

    private final float WORLD_HEIGHT = 600f;
    private Texture texture;
    private float invulnerabilityTimer = 0f;
    private float gateCooldown = 0f;  // prevents correct wall re-triggering after passing
    
    public boolean shootRequested = false;

    public PlayerCharacter(int id, Vector2 position, float radius) {
        super(id, "Player", position, radius, Color.CLEAR);
        this.texture = new Texture("player.png");
        getVelocity().x = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (invulnerabilityTimer > 0) invulnerabilityTimer -= deltaTime;
        if (gateCooldown > 0)         gateCooldown -= deltaTime;

        float topLimit = WORLD_HEIGHT - radius;
        float bottomLimit = radius;

        if (getPosition().y > topLimit) {
            getPosition().y = topLimit;
            getVelocity().y = 0;
        } else if (getPosition().y < bottomLimit) {
            getPosition().y = bottomLimit;
            getVelocity().y = 0;
        }

        // Small bounce-back recovery after damage
        if (getVelocity().x < 0) {
            getVelocity().x += 220f * deltaTime;
            if (getVelocity().x > 0) {
                getVelocity().x = 0;
            }
        } else {
            getVelocity().x = 0;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Blink while invulnerable
        if (invulnerabilityTimer > 0 && ((int)(invulnerabilityTimer * 12) % 2 == 0)) {
            return;
        }

        float size = radius * 2;
        batch.draw(texture, getPosition().x - radius, getPosition().y - radius, size, size);
    }

    @Override
    public void onCollision(Entity other) {
        String name = other.getName();

        if (name.equals("CorrectWall") || name.equals("CorrectBarrier")) {
            if (!reachedGate && gateCooldown <= 0f) {
                reachedGate = true;
                gateCooldown = 2.0f;  // block re-trigger for 2s while wall scrolls off
            }
            return;
        }

        // ---- wrong lane walls/barriers ----
        // Guard 1: if we already hit the correct wall this frame, ignore all
        //          wrong walls — the engine fires every overlap in one pass.
        // Guard 2: only damage if the player's centre is strictly inside this
        //          wall's vertical band, so adjacent-lane walls don't trigger
        //          when the player's circle merely grazes their edge.
        if (name.equals("WrongWall") || name.equals("WrongBarrier")) {
            if (reachedGate) return;
            if (!isPlayerCentreInsideWall(other)) return;
            applyDamage();
            return;
        }

        // ---- hazards always damage ----
        if (name.equals("Bullet") || name.equals("Cannon")) {
            applyDamage();
        }
    }

    /**
     * Returns true only if this player's Y centre is strictly inside the
     * middle 80 % of the wall's vertical extent.
     *
     * Using 80 % (10 % margin each side) instead of the full height means a
     * player whose circle grazes the very edge of a wrong-lane wall — while
     * actually travelling through the correct lane — will not be penalised.
     */
    private boolean isPlayerCentreInsideWall(Entity other) {
        if (!(other instanceof com.mygdx.game.engine.RectangleEntity)) return true;
        com.mygdx.game.engine.RectangleEntity rect = (com.mygdx.game.engine.RectangleEntity) other;
        float playerY  = getPosition().y;
        float bottom   = rect.getPosition().y;
        float height   = rect.getHeight();
        float margin   = height * 0.10f;
        return playerY >= (bottom + margin) && playerY <= (bottom + height - margin);
    }

    private void applyDamage() {
        if (invulnerabilityTimer <= 0f) {
            tookDamage = true;
            invulnerabilityTimer = 1.0f;
            getVelocity().x = -140f;
        }
    }

    public void consumeDamage() {
        tookDamage = false;
    }

    public void consumeGoal() {
        reachedGate = false;
    }
    
    public void requestShoot() {
        shootRequested = true;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}