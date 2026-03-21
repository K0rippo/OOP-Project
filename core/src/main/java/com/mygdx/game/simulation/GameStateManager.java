package com.mygdx.game.simulation;

public class GameStateManager {
    private int currentQuestionIndex = 0;
    private int lives = 3;
    private final int initialLives = 3;
    private IQuestionProvider questionProvider;

    public GameStateManager(IQuestionProvider questionProvider) {
        this.questionProvider = questionProvider;
    }

    public void advanceQuestion() {
        currentQuestionIndex++;
    }

    public void loseLife() {
        lives--;
    }

    public void resetState() {
        currentQuestionIndex = 0;
        lives = initialLives;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public int getLives() {
        return lives;
    }

    public int getTotalQuestions() {
        return questionProvider.getTotalQuestions();
    }

}
