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

    private static Texture fullWallTexture;
    private static Texture semiWallTexture;
    private static Texture badWallTexture;
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

        //load textures once on first barrier creation
        if (!texturesLoaded) {
            try {
                fullWallTexture = new Texture("fullwall.png");
                semiWallTexture = new Texture("semiwall.png");
                badWallTexture  = new Texture("badwall.png");
                texturesLoaded = true;
            } catch (Exception e) {
                texturesLoaded = false;
                System.err.println("failed to load barrier textures");
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
            other.setActive(false); // consume the bullet here

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

        Texture textureToUse;
        if (hitPoints >= 3) {
            textureToUse = fullWallTexture;
        } else if (hitPoints == 2) {
            textureToUse = semiWallTexture;
        } else {
            textureToUse = badWallTexture;
        }

        if (textureToUse != null) {
            batch.draw(textureToUse, getPosition().x, getPosition().y, getWidth(), getHeight());
        }
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
    }

    public static void disposeTextures() {
        if (fullWallTexture != null) fullWallTexture.dispose();
        if (semiWallTexture != null) semiWallTexture.dispose();
        if (badWallTexture != null)  badWallTexture.dispose();
        texturesLoaded = false;
    }
}