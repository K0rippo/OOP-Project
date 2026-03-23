package com.mygdx.game.simulation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;

public class ObstacleFactory {

	public RectangleEntity createWall(WallType type, Color color, int id, float x, float y, float width, float height) {
		return new AnswerGate(id, type, new Vector2(x, y), width, height, color);
    }
}