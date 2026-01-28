package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Triangle extends Entity {
    
    public Triangle() { super(); }

    public Triangle(float x, float y, Color color, float speed) {
        super(x, y, color, speed);
    }

    @Override
    public void movement() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) setX(getX() - getSpeed());
        if (Gdx.input.isKeyPressed(Input.Keys.D)) setX(getX() + getSpeed());
    }

    @Override
    public void draw(ShapeRenderer shape) {
        super.draw(shape);
        float offset = 50; 
        shape.triangle(
            getX() - offset, getY() - offset,
            getX() + offset, getY() - offset,
            getX(), getY() + offset
        );
    }
}