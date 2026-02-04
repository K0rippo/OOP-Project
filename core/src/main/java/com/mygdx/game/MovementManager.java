package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class MovementManager {

    private float globalSpeedMultiplier;
    private final List<Entity> movables;

    public MovementManager() {
        globalSpeedMultiplier = 1.0f;
        movables = new ArrayList<>();
    }

    public void registerMovable(Entity e) {
        if (e != null && !movables.contains(e)) {
            movables.add(e);
        }
    }

    public void unregisterMovable(Entity e) {
        movables.remove(e);
    }

    // ONLY loops and calls entity movement
    public void update(float deltaTime) {
        float scaledDelta = deltaTime * globalSpeedMultiplier;

        for (Entity e : movables) {
            e.applyMovement(scaledDelta);
        }
    }

    public void setSpeedMultiplier(float value) {
        if (value < 0f) value = 0f;
        globalSpeedMultiplier = value;
    }

    public float getSpeedMultiplier() {
        return globalSpeedMultiplier;
    }

}

