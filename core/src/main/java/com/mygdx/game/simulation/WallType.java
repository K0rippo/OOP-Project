package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

/**
 * WallType — enumerates gate wall variants.
 *
 * OCP: adding a new wall type only requires adding an enum constant here;
 * ObstacleFactory stays closed for modification.
 *
 * Both CORRECT and WRONG share an identical neutral colour intentionally —
 * the player must read the HUD question and choose the correct lane, not
 * identify it visually by wall colour.
 */
public enum WallType {
    /** The lane that matches the correct answer. */
    CORRECT("CorrectWall", new Color(0.30f, 0.45f, 0.70f, 0.45f)),

    /** Any lane that holds a wrong answer. */
    WRONG  ("WrongWall",   new Color(0.30f, 0.45f, 0.70f, 0.45f));

    public final String name;
    public final Color  color;

    WallType(String name, Color color) {
        this.name  = name;
        this.color = color;
    }
}