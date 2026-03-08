package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

/**
 * Enum for wall types to support open/closed principle.
 * Adding new wall types doesn't require modifying ObstacleFactory.
 * Walls are transparent (Color.CLEAR) so quiz answers aren't revealed visually.
 */
public enum WallType {
    CORRECT("CorrectWall", Color.CLEAR),
    WRONG("WrongWall", Color.CLEAR);

    public final String name;
    public final Color color;

    WallType(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
