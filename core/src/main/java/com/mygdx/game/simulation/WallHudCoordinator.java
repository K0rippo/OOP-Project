package com.mygdx.game.simulation;

import com.mygdx.game.engine.RectangleEntity;
import com.badlogic.gdx.utils.Array;
import java.util.function.IntConsumer;

/**
 * Owns wall-group state and HUD synchronization behavior.
 */
class WallHudCoordinator {
    static class WallGroup {
        private final int questionIndex;
        private final Array<RectangleEntity> walls = new Array<>();
        private final String[] shuffledAnswers;
        private boolean hudShown = false;
        private boolean passed = false;
        private boolean nextWaveTriggered = false;

        WallGroup(int questionIndex, String[] shuffledAnswers) {
            this.questionIndex = questionIndex;
            this.shuffledAnswers = shuffledAnswers;
        }

        int getQuestionIndex() {
            return questionIndex;
        }

        boolean isHudShown() {
            return hudShown;
        }

        boolean isPassed() {
            return passed;
        }

        void markHudShown() {
            hudShown = true;
        }

        void addWall(RectangleEntity wall) {
            walls.add(wall);
        }

        boolean isNextWaveTriggered() {
            return nextWaveTriggered;
        }

        void markNextWaveTriggered() {
            nextWaveTriggered = true;
        }

        float leadingWallX() {
            return walls.isEmpty() ? Float.MAX_VALUE : walls.first().getX();
        }

        void checkAndMarkPassed(float playerX) {
            if (passed || walls.isEmpty()) return;
            RectangleEntity first = walls.first();
            if (first.getX() + first.getWidth() < playerX) {
                passed = true;
            }
        }

        int fillAnswerData(String[] texts, float[] xs, float[] ys, float[] heights, int offset) {
            if (passed) return offset;
            for (int i = 0; i < walls.size && i < shuffledAnswers.length; i++) {
                RectangleEntity wall = walls.get(i);
                texts[offset] = shuffledAnswers[i];
                xs[offset] = wall.getX();
                ys[offset] = wall.getY();
                heights[offset] = wall.getHeight();
                offset++;
            }
            return offset;
        }
    }

    private final Array<WallGroup> wallGroups = new Array<>();

    void clear() {
        wallGroups.clear();
    }

    WallGroup createGroup(int questionIndex, String[] shuffledAnswers) {
        WallGroup group = new WallGroup(questionIndex, shuffledAnswers);
        wallGroups.add(group);
        return group;
    }

    boolean isFirstGroup(WallGroup group) {
        return wallGroups.size == 1 && wallGroups.first() == group;
    }

    void markPassedGroups(float playerX) {
        for (WallGroup group : wallGroups) {
            group.checkAndMarkPassed(playerX);
        }
    }

    void triggerUpcomingWaves(Array<EnemyWave> enemyWaves) {
        for (WallGroup group : wallGroups) {
            if (group.isPassed() && !group.isNextWaveTriggered()) {
                group.markNextWaveTriggered();
                activateWaveForQuestion(enemyWaves, group.getQuestionIndex() + 1);
            }
        }
    }

    void prunePassedGroups() {
        for (int i = wallGroups.size - 1; i >= 0; i--) {
            if (wallGroups.get(i).isPassed()) {
                wallGroups.removeIndex(i);
            }
        }
    }

    boolean isEmpty() {
        return wallGroups.isEmpty();
    }

    void updateHudForApproachingSegments(float playerX, float hudSwitchDistance, IntConsumer showQuestion) {
        for (WallGroup group : wallGroups) {
            if (!group.isHudShown() && group.leadingWallX() - playerX <= hudSwitchDistance) {
                group.markHudShown();
                showQuestion.accept(group.getQuestionIndex());
            }
        }
    }

    void syncAnswerLabelsToUI(GameUIManager uiManager) {
        int max = wallGroups.size * 3;

        if (max == 0) {
            uiManager.syncAnswerLabels(new String[0], new float[0], new float[0], new float[0], 0);
            return;
        }

        String[] texts = new String[max];
        float[] xs = new float[max];
        float[] ys = new float[max];
        float[] heights = new float[max];

        int count = 0;
        for (WallGroup group : wallGroups) {
            count = group.fillAnswerData(texts, xs, ys, heights, count);
        }

        uiManager.syncAnswerLabels(texts, xs, ys, heights, count);
    }

    private void activateWaveForQuestion(Array<EnemyWave> enemyWaves, int questionIndex) {
        for (EnemyWave wave : enemyWaves) {
            if (wave.getTriggerQuestionIndex() == questionIndex && !wave.isActivated()) {
                wave.activate();
                return;
            }
        }
    }
}
