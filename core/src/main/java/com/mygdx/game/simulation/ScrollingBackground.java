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
        
        // Create a 1x1 white pixel texture for drawing shapes
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.pixel = new Texture(pixmap);
        pixmap.dispose();

        // Generate random stars
        for(int i = 0; i < 100; i++) {
            stars.add(new Vector2(MathUtils.random(width), MathUtils.random(height)));
        }
    }

    public void update(float delta, float scrollSpeed) {
        totalDistance += scrollSpeed * delta;
    }

    public void render(SpriteBatch batch, Color skyColor) {
        // Draw the sky
        batch.setColor(skyColor);
        batch.draw(pixel, 0, 0, worldWidth, worldHeight);

        // Draw stars with parallax (looping)
        batch.setColor(Color.WHITE);
        for (Vector2 star : stars) {
            // This math makes stars loop infinitely while moving slower than the player
            float x = (star.x - (totalDistance * 0.2f)) % worldWidth;
            if (x < 0) x += worldWidth;
            batch.draw(pixel, x, star.y, 2, 2);
        }
    }

    public float getTotalDistance() { return totalDistance; }
    public Texture getPixel() { return pixel; }
    public void dispose() { pixel.dispose(); }
}