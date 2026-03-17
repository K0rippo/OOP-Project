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
    
    // Textures for different health states - loaded once statically
    private static Texture fullWallTexture;    // 3 HP (full health)
    private static Texture semiWallTexture;    // 2 HP (semi-broken)
    private static Texture badWallTexture;     // 1 HP (critical)
    private static boolean texturesLoaded = false;

    private int           hitPoints;
    private final boolean correctLane;
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
        this.correctLane    = correctLane;
        
        // Load textures only once on first barrier creation
        if (!texturesLoaded) {
            try {
                fullWallTexture = new Texture("fullwall.png");
                semiWallTexture = new Texture("semiwall.png");
                badWallTexture  = new Texture("badwall.png");
                texturesLoaded = true;
                System.out.println("✓ Barrier textures loaded successfully");
            } catch (Exception e) {
                System.err.println("✗ Error loading barrier textures: " + e.getMessage());
                System.err.println("Working directory: " + System.getProperty("user.dir"));
                texturesLoaded = false;
            }
        }
    }

    // Method to assign the break callback
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
                
                // Trigger the callback when destroyed
                if (onBreakCallback != null) {
                    onBreakCallback.run();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive() || broken) return;
        
        // Select texture based on remaining hit points
        Texture textureToUse;
        if (hitPoints >= 3) {
            textureToUse = fullWallTexture;
        } else if (hitPoints == 2) {
            textureToUse = semiWallTexture;
        } else {
            textureToUse = badWallTexture;
        }
        
        // Draw the texture at the barrier's position and size
        if (textureToUse != null) {
            batch.draw(textureToUse, getPosition().x, getPosition().y, getWidth(), getHeight());
        }
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Empty implementation - prevents colored rectangle overlay
    }

    public int     getHitPoints()  { return hitPoints; }
    public boolean isBroken()      { return broken; }
    public boolean isCorrectLane() { return correctLane; }
    
    public static void disposeTextures() {
        if (fullWallTexture != null) fullWallTexture.dispose();
        if (semiWallTexture != null) semiWallTexture.dispose();
        if (badWallTexture != null)  badWallTexture.dispose();
        texturesLoaded = false;
    }
}