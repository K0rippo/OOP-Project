package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    private List<Collider> colliders;

    public CollisionManager() {
        this.colliders = new ArrayList<>();
    }

    public void registerCollider(Collider c) {
        if (!colliders.contains(c)) {
            colliders.add(c);
        }
    }

    public void removeCollider(Collider c) {
        colliders.remove(c);
    }

    public void clear() {
        colliders.clear();
    }

    public void checkCollisions() {
        // Nested loop to check every collider against every other collider
        for (int i = 0; i < colliders.size(); i++) {
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider a = colliders.get(i);
                Collider b = colliders.get(j);

                if (a.intersects(b)) {
                    resolveCollision(a, b);
                }
            }
        }
    }

    public void update(float deltaTime) {
        // Typically called every frame to refresh collision states
        checkCollisions();
    }

    public void resolveCollision(Collider a, Collider b) {
        // Implementation for what happens when they hit
        // e.g., triggering events or pushing objects back
        System.out.println("Collision detected between " + a.getOwner() + " and " + b.getOwner());
    }

    public void setDebugMode(boolean enabled) {
        // Logic to draw bounding boxes visually
    }
}