package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    private Quadtree quadtree;

    public CollisionManager() {
        // Initialize with default world bounds; these can be adjusted via a setup method if needed
        this.quadtree = new Quadtree(0, new Rectangle(0, 0, 800, 600));
    }

    public void checkCollisions(List<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!a.isActive()) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (!b.isActive()) continue;

                if (!a.canCollideWith(b)) continue;

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