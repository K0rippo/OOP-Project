package com.mygdx.game.simulation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.RectangleEntity;
import java.util.function.IntConsumer;

class SegmentSpawnService {
    private final IGameEngine engine;
    private final IQuestionProvider questionProvider;
    private final ObstacleFactory obstacleFactory;
    private final EnemyWaveFactory enemyWaveFactory;
    private final AudioManager audioManager;
    private final WallHudCoordinator wallHudCoordinator;
    private final Array<EnemyWave> enemyWaves;
    private final IntConsumer showQuestion;
    private final float worldHeight;
    private final float scrollSpeed;
    private final int playerLayer;
    private final int gateLayer;
    private final int enemyLayer;

    private int nextEnemyShipId = 2000;

    SegmentSpawnService(IGameEngine engine,
                        IQuestionProvider questionProvider,
                        ObstacleFactory obstacleFactory,
                        EnemyWaveFactory enemyWaveFactory,
                        AudioManager audioManager,
                        WallHudCoordinator wallHudCoordinator,
                        Array<EnemyWave> enemyWaves,
                        IntConsumer showQuestion,
                        float worldHeight,
                        float scrollSpeed,
                        int playerLayer,
                        int gateLayer,
                        int enemyLayer) {
        this.engine = engine;
        this.questionProvider = questionProvider;
        this.obstacleFactory = obstacleFactory;
        this.enemyWaveFactory = enemyWaveFactory;
        this.audioManager = audioManager;
        this.wallHudCoordinator = wallHudCoordinator;
        this.enemyWaves = enemyWaves;
        this.showQuestion = showQuestion;
        this.worldHeight = worldHeight;
        this.scrollSpeed = scrollSpeed;
        this.playerLayer = playerLayer;
        this.gateLayer = gateLayer;
        this.enemyLayer = enemyLayer;
    }

    void reset() {
        nextEnemyShipId = 2000;
    }

    void spawnSegment(LevelSegment segment) {
        int questionIndex = segment.getQuestionIndex();
        Question question = questionProvider.getQuestion(questionIndex);
        //skip this segment if question data is missing
        if (question == null) return;

        Array<String> shuffled = new Array<>(question.getAnswers());
        shuffled.shuffle();

        //the first answer in the source data is treated as correct
        int correctIndex = shuffled.indexOf(question.getAnswers()[0], false);
        String[] shuffledArr = shuffled.toArray(String.class);

        float sectionHeight = worldHeight / 3f;

        //spawn wave first, then gates and barriers for the same segment
        spawnEnemyWave(segment);

        WallHudCoordinator.WallGroup group = wallHudCoordinator.createGroup(questionIndex, shuffledArr);
        spawnAnswerGates(segment.gateX(), sectionHeight, correctIndex, questionIndex, group);
        spawnBarriers(segment.barrierX(), sectionHeight, correctIndex, questionIndex);

        if (wallHudCoordinator.isFirstGroup(group)) {
            group.markHudShown();
            showQuestion.accept(questionIndex);
        }
    }

    private void spawnEnemyWave(LevelSegment segment) {
        EnemyWave wave = enemyWaveFactory.createDefaultWave(
                segment.getQuestionIndex(),
                segment.getStartX(),
                worldHeight,
                scrollSpeed,
                nextEnemyShipId
        );

        nextEnemyShipId += wave.getShips().size;

        for (EnemyShip ship : wave.getShips()) {
            //ships stay non-collidable until their wave gets activated
            ship.setCollisionLayer(enemyLayer);
            ship.setCollisionMask(0);
            engine.addEntity(ship);
        }

        enemyWaves.add(wave);
    }

    private void spawnAnswerGates(float spawnX,
                                  float sectionHeight,
                                  int correctIndex,
                                  int segmentId,
                                  WallHudCoordinator.WallGroup group) {
        for (int i = 0; i < 3; i++) {
            WallType type = (correctIndex == i) ? WallType.CORRECT : WallType.WRONG;
            Color slotColor = LaneStyle.getSlotColor(i);

            RectangleEntity wall = obstacleFactory.createWall(
                    type,
                    slotColor,
                    segmentId * 10 + i,
                    spawnX,
                    sectionHeight * (2 - i),
                    70,
                    sectionHeight
            );
            wall.setVelocityX(-scrollSpeed);
            wall.setCollisionLayer(gateLayer);
            wall.setCollisionMask(playerLayer);

            engine.addEntity(wall);
            group.addWall(wall);
        }
    }

    private void spawnBarriers(float spawnX, float sectionHeight, int correctIndex, int segmentId) {
        for (int i = 0; i < 3; i++) {
            BreakableBarrier barrier = new BreakableBarrier(
                    200 + segmentId * 10 + i,
                    new Vector2(spawnX, sectionHeight * (2 - i)),
                    20f,
                    sectionHeight,
                    3,
                    (i == correctIndex)
            );
            barrier.setVelocityX(-scrollSpeed);
            barrier.setCollisionLayer(gateLayer);
            barrier.setCollisionMask(playerLayer);

            //play break feedback when a barrier is destroyed
            barrier.setOnBreakCallback(() -> {
                if (audioManager != null) {
                    audioManager.playBreakSound();
                }
            });

            engine.addEntity(barrier);
        }
    }
}
