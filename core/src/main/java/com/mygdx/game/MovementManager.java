package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class MovementManager {

    private float globalSpeedMultiplier;
    private final List<Movable> movables;

    public MovementManager() {
        globalSpeedMultiplier = 1.0f;
        movables = new ArrayList<>();
    }

    public void registerMovable(Movable m) {
        if (m != null && !movables.contains(m)) {
            movables.add(m);
        }
    }

    public void unregisterMovable(Movable m) {
        movables.remove(m);
    }

    // ONLY loops and calls entity movement
    public void update(float deltaTime) {
        float scaledDelta = deltaTime * globalSpeedMultiplier;

        for (Movable m : movables) {
            m.applyMovement(scaledDelta);
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

