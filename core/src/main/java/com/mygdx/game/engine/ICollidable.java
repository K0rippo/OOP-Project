package com.mygdx.game.engine;

public interface ICollidable {
    Rectangle getBounds();
    void onCollision(Entity other);
}
