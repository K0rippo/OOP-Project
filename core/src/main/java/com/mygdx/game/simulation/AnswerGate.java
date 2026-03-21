package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;

public class AnswerGate extends RectangleEntity {
    private final boolean correctLane;
    
    private Texture texture;
    private float stateTime = 0f;

    public AnswerGate(int id, WallType type, Vector2 position, float width, float height, Color color) {
        super(id, type.name, position, width, height, color);
        this.correctLane = (type == WallType.CORRECT);
    }

    public boolean isCorrectLane() {
        return correctLane;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive()) return;

        if (texture != null) {
            // Keep track of time to animate the portal
            stateTime += Gdx.graphics.getDeltaTime();
            
            // Math equation to create a smooth, glowing "breathing" effect
            float pulse = 0.8f + 0.2f * MathUtils.sin(stateTime * 5f);
            
            // Apply the glowing pulse effect to the batch's alpha channel
            batch.setColor(1f, 1f, 1f, pulse);
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
            
            // Reset the batch color so we don't accidentally make the whole game pulse!
            batch.setColor(1f, 1f, 1f, 1f); 
        }
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        // Only draw the old colored rectangle if the images fail to load
        if (texture == null) {
            super.renderShape(shapeRenderer);
        }
    }
    // ------------------------------------------------------------------------------------
}