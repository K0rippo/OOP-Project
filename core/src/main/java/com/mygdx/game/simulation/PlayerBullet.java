package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

/**
 * PlayerBullet — projectile fired rightward by the player.
 *
 * Encapsulation: deactivation on barrier hit is handled exclusively by
 * BreakableBarrier.onCollision so that damage is always applied regardless
 * of collision-callback ordering. PlayerBullet only deactivates itself when
 * it travels out of the visible world (in update()).
 */
public class PlayerBullet extends RectangleEntity {

    private static final float MAX_X = 1400f; // just past the right edge of the world

    public PlayerBullet(int id, Vector2 position) {
        super(id, "PlayerBullet", position, 14f, 6f, new Color(1f, 0.9f, 0.2f, 1f));
        getVelocity().x = 420f;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (getPosition().x > MAX_X) {
            setActive(false);
        }
    }

    /**
     * PlayerBullet does not self-deactivate on collision — BreakableBarrier
     * is the authority that consumes the bullet when a hit is registered.
     * This prevents the race condition where self-deactivation happens before
     * BreakableBarrier reads the bullet's active state.
     */
    @Override
    public void onCollision(Entity other) {
        // intentionally empty — damage authority belongs to BreakableBarrier
    }
}