package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

/**
 * Default implementation of IQuestionProvider.
 * Encapsulates question bank creation logic, supporting single responsibility principle.
 */
public class DefaultQuestionProvider implements IQuestionProvider {
    private Array<Question> questions;

    public DefaultQuestionProvider() {
        this.questions = new Array<>();
        createQuestionBank();
    }

    private void createQuestionBank() {
        // Shuffle so question order is different every game
        questions.add(new Question(
            "What is the probability of flipping tails on a fair coin?", 
            new String[]{"1/2", "1/3", "1/4"}, 
            new Color(0.1f, 0.1f, 0.2f, 1f), 
            5f
        ));
        questions.add(new Question(
            "What is the probability of rolling a 4 on a 6-sided die?", 
            new String[]{"1/6", "1/2", "1/4"}, 
            new Color(0.1f, 0.2f, 0.1f, 1f), 
            5f
        ));
        questions.add(new Question(
            "What is the probability of drawing an Ace from a standard deck?", 
            new String[]{"1/13", "1/4", "1/52"}, 
            new Color(0.2f, 0.1f, 0.1f, 1f), 
            4f
        ));
        questions.add(new Question(
            "What is the probability of flipping two heads in a row?", 
            new String[]{"1/4", "1/2", "3/4"}, 
            new Color(0.2f, 0.2f, 0.1f, 1f), 
            4f
        ));
        questions.add(new Question(
            "What is the probability of rolling a sum of 7 with two dice?", 
            new String[]{"1/6", "1/12", "1/36"}, 
            new Color(0.1f, 0.1f, 0.1f, 1f), 
            3.5f
        ));
        // Randomize order each game
        questions.shuffle();
    }

    @Override
    public Array<Question> getQuestions() {
        return questions;
    }

    @Override
    public Question getQuestion(int index) {
        if (index >= 0 && index < questions.size) {
            return questions.get(index);
        }
        return null;
    }

    @Override
    public int getTotalQuestions() {
        return questions.size;
    }
}
