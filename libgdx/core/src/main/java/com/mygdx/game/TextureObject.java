package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextureObject extends Entity {
    private Texture tex;
    private float width;
    private float height;

    public TextureObject(String path, float x, float y, float speed, float width, float height) {
        // Textures don't require a Color attribute in this lab
        super(x, y, null, speed); 
        this.tex = new Texture(Gdx.files.internal(path));
        this.width = width;
        this.height = height;
    }

    public void bucketMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            setX(getX() - getSpeed());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            setX(getX() + getSpeed());
        }
    }

    public void raindropMovement() {
        setY(getY() - getSpeed());
        if (getY() < -height) {
            setY(480);
            setX((float) Math.random() * (800 - width));
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(tex, getX(), getY(), width, height);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void dispose() {
        if (tex != null) tex.dispose();
    }
}