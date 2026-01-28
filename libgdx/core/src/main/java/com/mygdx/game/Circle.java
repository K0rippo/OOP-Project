package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Circle extends Entity {
    private float radius;

    public Circle() { super(); this.radius = 0; }

    public Circle(float x, float y, float radius, Color color, float speed) {
        super(x, y, color, speed);
        this.radius = radius;
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

    @Override
    public void movement() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) setY(getY() + getSpeed());
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) setY(getY() - getSpeed());
    }

    @Override
    public void draw(ShapeRenderer shape) {
        super.draw(shape);
        shape.circle(getX(), getY(), radius);
    }
}