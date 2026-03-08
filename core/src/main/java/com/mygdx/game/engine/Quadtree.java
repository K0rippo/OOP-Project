package com.mygdx.game.engine;

import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private final int MAX_OBJECTS = 5;
    private final int MAX_LEVELS = 5;

    private int level;
    private List<Entity> objects;
    private Rectangle bounds;
    private Quadtree[] nodes;

    public Quadtree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        bounds = pBounds;
        nodes = new Quadtree[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        float subWidth = bounds.getWidth() / 2;
        float subHeight = bounds.getHeight() / 2;
        float x = bounds.getX();
        float y = bounds.getY();

        nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Entity pEntity) {
        int index = -1;
        Rectangle pRect = pEntity.getBounds();
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Entity pEntity) {
        if (nodes[0] != null) {
            int index = getIndex(pEntity);
            if (index != -1) {
                nodes[index].insert(pEntity);
                return;
            }
        }

        objects.add(pEntity);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Entity> retrieve(List<Entity> returnObjects, Entity pEntity) {
        int index = getIndex(pEntity);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, pEntity);
        }

        returnObjects.addAll(objects);
        return returnObjects;
    }
}