package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public class Question {
    private String text;
    //index 0 is always the correct answer
    private String[] answers;
    private Color themeColor;
    private float timeToReach;

    public Question(String text, String[] answers, Color themeColor, float timeToReach) {
        this.text = text;
        this.answers = answers;
        this.themeColor = themeColor;
        this.timeToReach = timeToReach;
    }

    public String getText() {
        return text;
    }

    public Color getThemeColor() {
        return themeColor;
    }

    public String[] getAnswers() {
        return answers;
    }
}
