package com.mygdx.game.simulation;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;

/**
 * Factory for creating wall obstacles.
 * Updated to use WallType enum, supporting open/closed principle.
 */
public class ObstacleFactory {

    public RectangleEntity createWall(WallType type, int id, float x, float y, float width, float height) {
        return new RectangleEntity(id, type.name, new Vector2(x, y), width, height, type.color);
    }

    public RectangleEntity createCorrectWall(int id, float x, float y, float width, float height) {
        return createWall(WallType.CORRECT, id, x, y, width, height);
    }

    public RectangleEntity createWrongWall(int id, float x, float y, float width, float height) {
        return createWall(WallType.WRONG, id, x, y, width, height);
    }
}