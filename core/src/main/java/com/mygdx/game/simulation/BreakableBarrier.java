package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

/**
 * BreakableBarrier — a destructible wall segment placed in front of each answer gate.
 *
 * SRP  : owns all HP/damage logic for bullet collisions; the bullet does NOT
 *        deactivate itself — BreakableBarrier is the single authority that decides
 *        whether the bullet is consumed (keeping collision logic in one place and
 *        preventing the race condition where the bullet deactivates itself before
 *        this handler runs, causing hits to be silently ignored).
 *
 * Encapsulation: hitPoints and broken state are private; read via getHitPoints()
 * and isBroken() only.
 */
public class BreakableBarrier extends RectangleEntity {

    private static final Color BARRIER_COLOR = new Color(0.45f, 0.55f, 0.75f, 1f);

    private int           hitPoints;
    private final boolean correctLane;
    private boolean       broken = false;

    public BreakableBarrier(int id, Vector2 position, float width, float height,
                             int hitPoints, boolean correctLane) {
        super(
                id,
                correctLane ? "CorrectBarrier" : "WrongBarrier",
                position,
                width,
                height,
                BARRIER_COLOR
        );
        this.hitPoints   = hitPoints;
        this.correctLane = correctLane;
    }

    /**
     * BreakableBarrier is the single authority for bullet-hit damage.
     * It consumes (deactivates) the bullet itself so the hit is always
     * registered regardless of collision-callback ordering.
     */
    @Override
    public void onCollision(Entity other) {
        if (broken || !isActive()) return;

        if (other.getName().equals("PlayerBullet") && other.isActive()) {
            hitPoints--;
            other.setActive(false); // consume the bullet here, not in PlayerBullet

            if (hitPoints <= 0) {
                broken = true;
                setActive(false);
            }
        }
    }

    public int     getHitPoints()  { return hitPoints; }
    public boolean isBroken()      { return broken; }
    public boolean isCorrectLane() { return correctLane; }
}