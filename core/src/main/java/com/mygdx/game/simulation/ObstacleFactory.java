package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;
public class ObstacleFactory {

    public RectangleEntity createWrongWall(int id, float x, float y, float width, float height) {
        return new RectangleEntity(id, "WrongWall", new Vector2(x, y), width, height, Color.LIGHT_GRAY);
    }

    public RectangleEntity createCorrectWall(int id, float x, float y, float width, float height) {
        return new RectangleEntity(id, "CorrectWall", new Vector2(x, y), width, height, Color.LIGHT_GRAY);
    }
}