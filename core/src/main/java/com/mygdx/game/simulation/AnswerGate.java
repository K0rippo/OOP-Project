package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;

/**
 * Typed gate entity to remove string-based gate logic from player collision handling.
 */
public class AnswerGate extends RectangleEntity {
    private final boolean correctLane;

    public AnswerGate(int id, WallType type, Vector2 position, float width, float height, Color color) {
        super(id, type.name, position, width, height, color);
        this.correctLane = (type == WallType.CORRECT);
    }

    public boolean isCorrectLane() {
        return correctLane;
    }
}
