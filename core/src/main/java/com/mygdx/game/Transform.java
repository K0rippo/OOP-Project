package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Transform {

    private Vector2 position;
    private float rotationDegrees;

    public Transform() {
        this.position = new Vector2(0f, 0f);
        this.rotationDegrees = 0f;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(float rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }
}
