package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;




public class Trampoline extends RectangleEntity {

    private float speed = 260f;
    private float leftBound = 0f;
    private float rightBound;
    private int dir = 1; // +1 = right, -1 = left

    public Trampoline(int id, Vector2 position, float width, float height, Color color) {
        super(id, "Trampoline", position, width, height, color);
        this.rightBound = Gdx.graphics.getWidth() - width;
    }

  
    public void setPatrolBounds(float left, float right) {
        this.leftBound = left;
        this.rightBound = Math.max(left, right - getWidth());
    }

    public void setSpeed(float speed) {
        this.speed = Math.max(0f, speed);
    }

    @Override
    public void update(float dt) {
        getVelocity().x = dir * speed;

        super.update(dt);

        if (getPosition().x <= leftBound) {
            getPosition().x = leftBound;
            dir = 1;
        } else if (getPosition().x >= rightBound) {
            getPosition().x = rightBound;
            dir = -1;
        }

        getPosition().x = MathUtils.clamp(getPosition().x, leftBound, rightBound);
    }
}
