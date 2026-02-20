package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

public interface iMovable {

    void applyMovement(float deltaTime);

    Orientate getOrientate();

    Vector2 getVelocity();
    void setVelocity(Vector2 velocity);
}

