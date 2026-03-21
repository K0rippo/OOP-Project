package com.mygdx.game.simulation;

class GameProgressionService {

    ProgressionResult update(PlayerCharacter player, GameStateManager gameState, int score) {
        int updatedScore = score;
        boolean shouldTransitionToResult = false;

        if (player != null) {
            player.scaleVelocity(0.85f);

            if (player.hasTakenDamage()) {
                gameState.loseLife();
                player.consumeDamage();

                if (gameState.isGameOver()) {
                    shouldTransitionToResult = true;
                }
            }

            if (!shouldTransitionToResult && player.hasReachedGate()) {
                player.consumeGoal();
                updatedScore++;
                gameState.advanceQuestion();
            }
        }

        return new ProgressionResult(updatedScore, shouldTransitionToResult);
    }

    static class ProgressionResult {
        private final int score;
        private final boolean shouldTransitionToResult;

        ProgressionResult(int score, boolean shouldTransitionToResult) {
            this.score = score;
            this.shouldTransitionToResult = shouldTransitionToResult;
        }

        int getScore() {
            return score;
        }

        boolean shouldTransitionToResult() {
            return shouldTransitionToResult;
        }
    }
}
