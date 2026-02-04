package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public interface Movable {

    void applyMovement(float deltaTime);

    Transform getTransform();

    Vector2 getVelocity();
    void setVelocity(Vector2 velocity);
}

