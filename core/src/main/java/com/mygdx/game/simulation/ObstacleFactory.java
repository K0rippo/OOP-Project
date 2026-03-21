package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.RectangleEntity;

public class ObstacleFactory {

    private Texture portalTexture;

    public ObstacleFactory() {
        // Load the portal texture safely
        try {
            portalTexture = new Texture("portal.png");
        } catch (Exception e) {
            System.err.println("Warning: portal.png missing! Using fallback colors.");
        }
    }

    public RectangleEntity createWall(WallType type, int id, float x, float y, float width, float height) {
        AnswerGate gate = new AnswerGate(id, type, new Vector2(x, y), width, height, type.color);
        
        // Apply the portal texture to the Answer Gates
        if (portalTexture != null) {
            gate.setTexture(portalTexture);
        }
        
        return gate;
    }

    public void dispose() {
        if (portalTexture != null) portalTexture.dispose();
    }
}