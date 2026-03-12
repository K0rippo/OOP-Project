package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CollisionManager {
    private Quadtree quadtree;
    private HashSet<Long> processedPairs;

    public CollisionManager() {
        this.quadtree = new Quadtree(0, new Rectangle(0, 0, 1280, 720)); 
        this.processedPairs = new HashSet<>();
    }

    public void checkCollisions(List<Entity> entities) {
        quadtree.clear();
        processedPairs.clear();

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e.isActive() && e.getBounds() != null) {
                quadtree.insert(e);
            }
        }

        List<Entity> returnObjects = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!a.isActive() || a.getBounds() == null) continue;

            returnObjects.clear();
            quadtree.retrieve(returnObjects, a);

            for (int j = 0; j < returnObjects.size(); j++) {
                Entity b = returnObjects.get(j);
                
                if (!b.isActive() || a == b) continue;

                // Create a unique, order-independent ID for the collision pair
                long idA = System.identityHashCode(a);
                long idB = System.identityHashCode(b);
                long min = Math.min(idA, idB);
                long max = Math.max(idA, idB);
                long pairId = (min << 32) | (max & 0xFFFFFFFFL);

                // If this pair was already processed this frame, skip to prevent double-hits
                if (!processedPairs.add(pairId)) continue;

                if (a.canCollideWith(b) || b.canCollideWith(a)) {
                    if (a.getBounds().intersects(b.getBounds())) {
                        a.onCollision(b);
                        b.onCollision(a); 
                    }
                }
            }
        }
    }
}