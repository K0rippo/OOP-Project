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

    private final Array<Vector2> farStars = new Array<>();
    private final Array<Vector2> nearStars = new Array<>();

    private float farOffset = 0f;
    private float nearOffset = 0f;

    public ScrollingBackground(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        for (int i = 0; i < 28; i++) {
            farStars.add(new Vector2(
                    MathUtils.random(0f, worldWidth),
                    MathUtils.random(worldHeight - 220f, worldHeight - 20f)
            ));
        }

        for (int i = 0; i < 18; i++) {
            nearStars.add(new Vector2(
                    MathUtils.random(0f, worldWidth),
                    MathUtils.random(worldHeight - 200f, worldHeight - 30f)
            ));
        }
    }

    public void update(float delta, float scrollSpeed) {
        farOffset = (farOffset + scrollSpeed * 0.18f * delta) % worldWidth;
        nearOffset = (nearOffset + scrollSpeed * 0.42f * delta) % worldWidth;
    }

    public void render(SpriteBatch batch, Color skyColor) {
        Color old = new Color(batch.getColor());

        batch.setColor(skyColor);
        batch.draw(pixel, 0, 0, worldWidth, worldHeight);

        batch.setColor(0.05f, 0.07f, 0.13f, 1f);
        batch.draw(pixel, 0, 0, worldWidth, 95f);

        batch.setColor(0.10f, 0.14f, 0.24f, 1f);
        batch.draw(pixel, 0, 95f, worldWidth, 18f);

        drawStars(batch, farStars, farOffset, 2f, 0.55f);
        drawStars(batch, nearStars, nearOffset, 3f, 0.9f);

        batch.setColor(0.16f, 0.20f, 0.32f, 0.65f);
        for (int i = -1; i < 4; i++) {
            float x = i * 300f - (nearOffset % 300f);
            batch.draw(pixel, x, 95f, 150f, 65f);
            batch.draw(pixel, x + 120f, 95f, 110f, 100f);
        }

        batch.setColor(old);
    }

    private void drawStars(SpriteBatch batch, Array<Vector2> stars, float offset, float size, float alpha) {
        batch.setColor(1f, 1f, 1f, alpha);

        for (Vector2 star : stars) {
            float x = star.x - offset;
            if (x < -size) x += worldWidth;
            batch.draw(pixel, x, star.y, size, size);
        }
    }

    public Texture getPixel() {
        return pixel;
    }

    public void dispose() {
        pixel.dispose();
    }
}