package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

/**
 * Abstraction for providing questions to the game.
 * Supports dependency inversion: GameScene depends on interface, not hardcoded questions.
 */
public interface IQuestionProvider {
    Array<Question> getQuestions();
    Question getQuestion(int index);
    int getTotalQuestions();
}
