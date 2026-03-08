package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;

public class Question {
    private String text;
    private String[] answers; // index 0 is always correct
    private Color themeColor;
    private float timeToReach; // How many seconds the player has

    
    
    
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




	public void setThemeColor(Color themeColor) {
		this.themeColor = themeColor;
	}




	public String[] getAnswers() {
		return answers;
	}




	public void setAnswers(String[] answers) {
		this.answers = answers;
	}




	public float getTimeToReach() {
		return timeToReach;
	}




	public void setTimeToReach(float timeToReach) {
		this.timeToReach = timeToReach;
	}





}