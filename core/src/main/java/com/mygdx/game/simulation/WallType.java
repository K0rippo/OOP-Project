package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public enum WallType {
    CORRECT("CorrectWall"),
    WRONG  ("WrongWall");

    public final String name;

    WallType(String name) {
        this.name = name;
    }
}