package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ScrollingBackground {
    private final float worldWidth;
    private final float worldHeight;
    private final Texture pixel;
    private float totalDistance = 0f;
    private final Array<Vector2> stars = new Array<>();

    public ScrollingBackground(float width, float height) {
        this.worldWidth = width;
        this.worldHeight = height;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.pixel = new Texture(pixmap);
        pixmap.dispose();

        for(int i = 0; i < 100; i++) {
            stars.add(new Vector2(MathUtils.random(width), MathUtils.random(height)));
        }
    }

    public void update(float delta, float scrollSpeed) {
        totalDistance += scrollSpeed * delta;
    }

    public void render(SpriteBatch batch, Color skyColor) {
        batch.setColor(skyColor);
        batch.draw(pixel, 0, 0, worldWidth, worldHeight);

        batch.setColor(Color.WHITE);
        for (Vector2 star : stars) {
            float x = (star.x - (totalDistance * 0.2f)) % worldWidth;
            if (x < 0) x += worldWidth;
            batch.draw(pixel, x, star.y, 2, 2);
        }
    }

    public void dispose() { pixel.dispose(); }
}