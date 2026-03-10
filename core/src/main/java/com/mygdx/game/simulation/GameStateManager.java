package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

/**
 * Manages game state and level progression.
 * Extracted from GameScene to support single responsibility principle.
 */
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

    public boolean isLastQuestion() {
        return currentQuestionIndex >= questionProvider.getTotalQuestions();
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getLives() {
        return lives;
    }

    public Question getCurrentQuestion() {
        return questionProvider.getQuestion(currentQuestionIndex);
    }

    public int getTotalQuestions() {
        return questionProvider.getTotalQuestions();
    }
    
    
    
}
