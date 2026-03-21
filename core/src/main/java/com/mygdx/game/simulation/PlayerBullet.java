package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class PlayerBullet extends RectangleEntity {

    private static final float MAX_X = 1400f;

    public PlayerBullet(int id, Vector2 position) {
        super(id, "PlayerBullet", position, 14f, 6f, new Color(1f, 0.9f, 0.2f, 1f));
        setVelocityX(420f);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (getX() > MAX_X) {
            setActive(false);
        }
    }

    @Override
    public void onCollision(Entity other) {
        //intentionally empty because barrier handles bullet consumption
    }
}