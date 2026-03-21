package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class BreakableBarrier extends RectangleEntity {

    private static final Color BARRIER_COLOR = new Color(0.45f, 0.55f, 0.75f, 1f);

    private static Texture lockedGateTexture;
    private static boolean texturesLoaded = false;

    private int           hitPoints;
    private boolean       broken = false;

    private Runnable      onBreakCallback;

    public BreakableBarrier(int id, Vector2 position, float width, float height,
                             int hitPoints, boolean correctLane) {
        super(
                id,
                correctLane ? "CorrectBarrier" : "WrongBarrier",
                position,
                width,
                height,
                BARRIER_COLOR
        );
        this.hitPoints      = hitPoints;

        if (!texturesLoaded) {
            try {
                lockedGateTexture = new Texture("locked_gate.png");
                texturesLoaded = true;
            } catch (Exception e) {
                texturesLoaded = false;
                System.err.println("failed to load locked_gate.png");
            }
        }
    }

    public void setOnBreakCallback(Runnable callback) {
        this.onBreakCallback = callback;
    }

    @Override
    public void onCollision(Entity other) {
        if (broken || !isActive()) return;

        if (other.getName().equals("PlayerBullet") && other.isActive()) {
            hitPoints--;
            other.setActive(false); 

            if (hitPoints <= 0) {
                broken = true;
                setActive(false);

                if (onBreakCallback != null) {
                    onBreakCallback.run();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive() || broken) return;

        if (lockedGateTexture != null) {
            if (hitPoints == 3) {
                batch.setColor(1f, 1f, 1f, 1f); // Normal
            } else if (hitPoints == 2) {
                batch.setColor(1f, 0.6f, 0.6f, 1f); // Slightly red
            } else {
                batch.setColor(1f, 0.3f, 0.3f, 1f); // Very red
            }
            
            batch.draw(lockedGateTexture, getPosition().x, getPosition().y, getWidth(), getHeight());
            
            // Reset color so it doesn't tint everything else
            batch.setColor(1f, 1f, 1f, 1f); 
            // ------------------------------------------------------------
        }
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Only draw primitive fallback if texture failed to load
        if (lockedGateTexture == null) {
            shapeRenderer.setColor(hitPoints == 3 ? BARRIER_COLOR : Color.RED);
            shapeRenderer.rect(getPosition().x, getPosition().y, getWidth(), getHeight());
        }
    }

    public static void disposeTextures() {
        if (lockedGateTexture != null) lockedGateTexture.dispose();
        texturesLoaded = false;
    }
}