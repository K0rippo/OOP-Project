package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.List;

public class MovementManager {

    private float globalSpeedMultiplier = 1.0f;
    private final List<iMovable> movables = new ArrayList<>();

    public void registerMovable(iMovable m) {
        if (m != null && !movables.contains(m)) {
            movables.add(m);
        }
    }

    public void unregisterMovable(iMovable m) {
        movables.remove(m);
    }

    public void update(float deltaTime) {
        float scaledDelta = deltaTime * globalSpeedMultiplier;

        for (iMovable m : movables) {
            m.applyMovement(scaledDelta);
        }
    }

    public void setSpeedMultiplier(float value) {
        globalSpeedMultiplier = Math.max(0f, value);
    }

    public float getSpeedMultiplier() {
        return globalSpeedMultiplier;
    }
}
