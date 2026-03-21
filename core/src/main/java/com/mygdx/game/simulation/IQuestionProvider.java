package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

public interface IQuestionProvider {
    Array<Question> getQuestions();
    Question getQuestion(int index);
    int getTotalQuestions();

    default void shuffleForNewGame() {
        //optional hook for providers that support reshuffle
    }
}
