package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public enum WallType {
    CORRECT("CorrectWall", new Color(0.20f, 0.60f, 0.30f, 0.28f)),
    WRONG("WrongWall", new Color(0.20f, 0.60f, 0.30f, 0.28f));

    public final String name;
    public final Color color;

    WallType(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
