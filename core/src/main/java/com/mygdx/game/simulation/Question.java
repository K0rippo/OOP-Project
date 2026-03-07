package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public class Question {
    public String text;
    public String[] answers; // index 0 is always correct
    public Color themeColor;
    public float timeToReach; // How many seconds the player has

    public Question(String text, String[] answers, Color themeColor, float timeToReach) {
        this.text = text;
        this.answers = answers;
        this.themeColor = themeColor;
        this.timeToReach = timeToReach;
    }
}