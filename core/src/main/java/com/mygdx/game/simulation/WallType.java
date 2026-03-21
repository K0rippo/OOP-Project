package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public enum WallType {
    //both lane types intentionally share the same color
    CORRECT("CorrectWall", new Color(0.30f, 0.45f, 0.70f, 0.45f)),

    WRONG  ("WrongWall",   new Color(0.30f, 0.45f, 0.70f, 0.45f));

    public final String name;
    public final Color  color;

    WallType(String name, Color color) {
        this.name  = name;
        this.color = color;
    }
}