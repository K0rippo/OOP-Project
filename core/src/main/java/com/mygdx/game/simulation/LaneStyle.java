package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public class LaneStyle {
    private static Color[] slotColors;

    public static Color getSlotColor(int slotIndex) {
        if (slotColors == null) {
            slotColors = new Color[]{
                new Color(0.25f, 0.55f, 0.90f, 0.7f),  // slot 0 — blue
                new Color(0.85f, 0.55f, 0.15f, 0.7f),  // slot 1 — orange
                new Color(0.25f, 0.75f, 0.40f, 0.7f),  // slot 2 — green
            };
        }
        return slotColors[slotIndex];
    }
}