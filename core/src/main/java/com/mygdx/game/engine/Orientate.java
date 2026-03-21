package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

public class Orientate {

    private Vector2 position;

    public Orientate() {
        this.position = new Vector2(0f, 0f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        //copy vector to avoid external mutation of internal state
        this.position = new Vector2(position);
    }
}
