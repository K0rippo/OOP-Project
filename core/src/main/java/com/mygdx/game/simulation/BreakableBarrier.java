package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class BreakableBarrier extends RectangleEntity {

    private static final Color BARRIER_COLOR = new Color(0.45f, 0.55f, 0.75f, 1f);

    private int hitPoints;
    private final boolean correctLane;
    private boolean broken = false;

    public BreakableBarrier(int id, Vector2 position, float width, float height, int hitPoints, boolean correctLane) {
        super(
                id,
                correctLane ? "CorrectBarrier" : "WrongBarrier",
                position,
                width,
                height,
                BARRIER_COLOR
        );
        this.hitPoints = hitPoints;
        this.correctLane = correctLane;
    }

    @Override
    public void onCollision(Entity other) {
        if (broken || !isActive()) return;

        if (other.getName().equals("PlayerBullet")) {
            if (!other.isActive()) return;

            other.setActive(false);
            hitPoints--;

            if (hitPoints <= 0) {
                broken = true;
                setActive(false);
            }
        }
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public boolean isBroken() {
        return broken;
    }

    public boolean isCorrectLane() {
        return correctLane;
    }
}