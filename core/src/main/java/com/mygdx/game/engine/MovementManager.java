package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.List;

public class MovementManager {

    private float globalSpeedMultiplier;
    private final List<iMovable> movables;

    public MovementManager() {
        globalSpeedMultiplier = 1.0f;
        movables = new ArrayList<>();
    }

    public void registerMovable(iMovable m) {
        if (m != null && !movables.contains(m)) {
            movables.add(m);
        }
    }

    public void unregisterMovable(iMovable m) {
        movables.remove(m);
    }

    // ONLY loops and calls entity movement
    public void update(float deltaTime) {
        float scaledDelta = deltaTime * globalSpeedMultiplier;

        for (iMovable m : movables) {
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

