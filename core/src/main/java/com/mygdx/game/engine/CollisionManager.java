package com.mygdx.game.engine;

import java.util.List;

public class CollisionManager {

    public void checkCollisions(List<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);

                if (!a.isActive() || !b.isActive()) continue;

                Rectangle rectA = a.getBounds();
                Rectangle rectB = b.getBounds();

                if (rectA != null && rectB != null && rectA.intersects(rectB)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }
}