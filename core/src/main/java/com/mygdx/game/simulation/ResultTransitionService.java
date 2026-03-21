package com.mygdx.game.simulation;

import com.mygdx.game.engine.ISceneNavigator;

class ResultTransitionService {
    private final ISceneNavigator sceneNavigator;

    ResultTransitionService(ISceneNavigator sceneNavigator) {
        this.sceneNavigator = sceneNavigator;
    }

    void transition(int score, int totalQuestions, boolean isSuccess) {
        ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
        if (result != null) {
            result.setScore(score, totalQuestions);
            result.setMissionStatus(isSuccess); // Update the visual status
        }
        sceneNavigator.goToScene("RESULT");
    }
}