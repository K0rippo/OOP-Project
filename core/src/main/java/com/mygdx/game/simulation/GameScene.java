package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.*;

public class GameScene extends Scene {

    // ── Collision layers ────────────────────────────────────────────────────
    public static final int LAYER_PLAYER       = 1;
    public static final int LAYER_GATE         = 2;
    public static final int LAYER_ENEMY        = 4;
    public static final int LAYER_ENEMY_BULLET = 8;

    // ── World constants ─────────────────────────────────────────────────────
    private static final float WORLD_WIDTH         = 1280f;
    private static final float WORLD_HEIGHT        = 720f;
    private static final float PLAYER_X            = 140f;
    private static final float SCROLL_SPEED        = 100f;
    private static final float FIRST_SEGMENT_X     = WORLD_WIDTH + 200f;
    private static final float SHOOT_INTERVAL      = 0.25f;
    private static final float HUD_SWITCH_DISTANCE = WORLD_WIDTH;

    // ── Dependencies ────────────────────────────────────────────────────────
    private final ISceneNavigator     sceneNavigator;
    private final ObstacleFactory     obstacleFactory;
    private final GameStateManager    gameState;
    private final GameUIManager       uiManager;
    private final Stage               stage;
    private final ScrollingBackground background;
    private final IQuestionProvider   questionProvider;
    private final EnemyWaveFactory    enemyWaveFactory;
    private ContinuousLevelSpawner    levelSpawner;

    // ── Entities ────────────────────────────────────────────────────────────
    private PlayerCharacter player;
    private Texture         heartTexture;

    private final Array<RectangleEntity>  currentWalls  = new Array<RectangleEntity>();
    private final Array<BreakableBarrier> barriers      = new Array<BreakableBarrier>();
    private final Array<PlayerBullet>     playerBullets = new Array<PlayerBullet>();
    private final Array<EnemyShip>        enemyShips    = new Array<EnemyShip>();
    private final Array<EnemyBullet>      enemyBullets  = new Array<EnemyBullet>();
    private final Array<EnemyWave>        enemyWaves    = new Array<EnemyWave>();

    // ── State ───────────────────────────────────────────────────────────────
    private Color currentBGColor = new Color(0.08f, 0.10f, 0.18f, 1f);

    private int nextPlayerBulletId = 1000;
    private int nextEnemyShipId    = 2000;
    private int nextEnemyBulletId  = 3000;

    private float shootCooldown    = 0f;
    private float scrolledDistance = 0f;
    private int   score            = 0;

    private boolean paused         = false;
    private boolean pendingRestart = true;

    // ── WallGroup ───────────────────────────────────────────────────────────

    private static class WallGroup {
        private final int                    questionIndex;
        private final Array<RectangleEntity> walls           = new Array<RectangleEntity>();
        private final String[]               shuffledAnswers;
        private boolean                      hudShown         = false;
        private boolean                      passed           = false;
        private boolean                      nextWaveTriggered = false;

        WallGroup(int questionIndex, String[] shuffledAnswers) {
            this.questionIndex   = questionIndex;
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
            return walls.isEmpty() ? Float.MAX_VALUE : walls.first().getPosition().x;
        }

        void checkAndMarkPassed(float playerX) {
            if (passed || walls.isEmpty()) return;

            RectangleEntity first = walls.first();
            if (first.getPosition().x + first.getWidth() < playerX) {
                passed = true;
            }
        }

        int fillAnswerData(String[] texts, float[] xs, float[] ys, float[] heights, int offset) {
            if (passed) return offset;

            for (int i = 0; i < walls.size && i < shuffledAnswers.length; i++) {
                RectangleEntity wall = walls.get(i);
                texts[offset]   = shuffledAnswers[i];
                xs[offset]      = wall.getPosition().x;
                ys[offset]      = wall.getPosition().y;
                heights[offset] = wall.getHeight();
                offset++;
            }
            return offset;
        }
    }

    private final Array<WallGroup> wallGroups = new Array<WallGroup>();

    // ── Constructor ─────────────────────────────────────────────────────────

    public GameScene(String id, ISceneNavigator sceneNavigator, IGameEngine engine,
                     IQuestionProvider questionProvider) {
        super(id, engine);
        this.sceneNavigator   = sceneNavigator;
        this.questionProvider = questionProvider;
        this.obstacleFactory  = new ObstacleFactory();
        this.gameState        = new GameStateManager(questionProvider);
        this.stage            = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.uiManager        = new GameUIManager(stage, WORLD_HEIGHT);
        this.heartTexture     = new Texture("heart.png");
        this.background       = new ScrollingBackground(WORLD_WIDTH, WORLD_HEIGHT);
        this.enemyWaveFactory = new EnemyWaveFactory();

        initializeInput();
        startLevel();
    }

    // ── Level init ──────────────────────────────────────────────────────────

    private void startLevel() {
        gameState.resetState();
        scrolledDistance = 0f;
        score            = 0;

        clearDynamicEntities();
        wallGroups.clear();
        enemyWaves.clear();

        nextPlayerBulletId = 1000;
        nextEnemyShipId    = 2000;
        nextEnemyBulletId  = 3000;

        // Shuffle questions for a fresh game experience
        if (questionProvider instanceof CsvQuestionProvider) {
            ((CsvQuestionProvider) questionProvider).shuffleForNewGame();
        }

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(PLAYER_X, WORLD_HEIGHT / 2f), 25f);
            player.setCollisionLayer(LAYER_PLAYER);
            player.setCollisionMask(LAYER_GATE | LAYER_ENEMY_BULLET);
            addEntity(player);
        } else {
            player.getPosition().set(PLAYER_X, WORLD_HEIGHT / 2f);
            player.getVelocity().set(0, 0);
            player.consumeDamage();
            player.consumeGoal();
            player.consumeShoot();
            player.setCollisionLayer(LAYER_PLAYER);
            player.setCollisionMask(LAYER_GATE | LAYER_ENEMY_BULLET);
        }

        levelSpawner = new ContinuousLevelSpawner(questionProvider, FIRST_SEGMENT_X, this::spawnSegment);
    }

    public void requestRestart() {
        pendingRestart = true;
    }

    @Override
    public void show() {
        paused = false;
        engine.getMovementManager().setSpeedMultiplier(1f);

        if (pendingRestart) {
            pendingRestart = false;
            startLevel();
        }
    }

    @Override
    public void hide() {
        paused = true;
        engine.getMovementManager().setSpeedMultiplier(0f);
    }

    // ── Segment spawning ────────────────────────────────────────────────────

    private void spawnSegment(LevelSegment segment) {
        int questionIndex = segment.getQuestionIndex();
        Question question = questionProvider.getQuestion(questionIndex);
        if (question == null) return;

        Array<String> shuffled = new Array<String>(question.getAnswers());
        shuffled.shuffle();

        int correctIndex = shuffled.indexOf(question.getAnswers()[0], false);
        String[] shuffledArr = shuffled.toArray(String.class);

        float sectionHeight = WORLD_HEIGHT / 3f;

        spawnEnemyWave(segment);

        WallGroup group = new WallGroup(questionIndex, shuffledArr);
        spawnAnswerGates(segment.gateX(), sectionHeight, correctIndex, questionIndex, group);
        spawnBarriers(segment.barrierX(), sectionHeight, correctIndex, questionIndex);
        wallGroups.add(group);

        if (wallGroups.size == 1) {
            group.markHudShown();
            showQuestionOnHud(questionIndex);
        }
    }

    private void spawnEnemyWave(LevelSegment segment) {
        EnemyWave wave = enemyWaveFactory.createDefaultWave(
                segment.getQuestionIndex(),
                segment.getStartX(),
                WORLD_HEIGHT,
                SCROLL_SPEED,
                nextEnemyShipId
        );

        nextEnemyShipId += wave.getShips().size;

        for (EnemyShip ship : wave.getShips()) {
            ship.setCollisionLayer(LAYER_ENEMY);
            ship.setCollisionMask(0);

            addEntity(ship);
            enemyShips.add(ship);
        }

        enemyWaves.add(wave);
    }

    private void spawnAnswerGates(float spawnX, float sectionHeight, int correctIndex,
                                  int segmentId, WallGroup group) {
        for (int i = 0; i < 3; i++) {
            WallType type = (correctIndex == i) ? WallType.CORRECT : WallType.WRONG;
            RectangleEntity wall = obstacleFactory.createWall(
                    type,
                    segmentId * 10 + i,
                    spawnX,
                    sectionHeight * (2 - i),
                    70,
                    sectionHeight
            );
            wall.getVelocity().x = -SCROLL_SPEED;
            wall.setCollisionLayer(LAYER_GATE);
            wall.setCollisionMask(LAYER_PLAYER);

            addEntity(wall);
            currentWalls.add(wall);
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
            barrier.getVelocity().x = -SCROLL_SPEED;
            barrier.setCollisionLayer(LAYER_GATE);
            barrier.setCollisionMask(LAYER_PLAYER);

            addEntity(barrier);
            barriers.add(barrier);
        }
    }

    // ── HUD helpers ─────────────────────────────────────────────────────────

    private void showQuestionOnHud(int questionIndex) {
        Question question = questionProvider.getQuestion(questionIndex);
        if (question == null) return;

        currentBGColor = question.getThemeColor();
        uiManager.updateQuestion(question);
    }

    private void updateHudForApproachingSegments() {
        for (WallGroup group : wallGroups) {
            if (!group.isHudShown()) {
                if (group.leadingWallX() - PLAYER_X <= HUD_SWITCH_DISTANCE) {
                    group.markHudShown();
                    showQuestionOnHud(group.getQuestionIndex());
                }
            }
        }
    }

    private void syncAnswerLabelsToUI() {
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

    // ── Wave activation ─────────────────────────────────────────────────────

    private void activateWaveForQuestion(int questionIndex) {
        for (EnemyWave wave : enemyWaves) {
            if (wave.getTriggerQuestionIndex() == questionIndex && !wave.isActivated()) {
                wave.activate();
                return;
            }
        }
    }

    private void triggerUpcomingWavesFromPassedGroups() {
        for (WallGroup group : wallGroups) {
            if (group.isPassed() && !group.isNextWaveTriggered()) {
                group.markNextWaveTriggered();
                activateWaveForQuestion(group.getQuestionIndex() + 1);
            }
        }
    }

    // ── Level completion ────────────────────────────────────────────────────

    private boolean allSegmentsCompleted() {
        if (!levelSpawner.allSegmentsSpawned()) return false;
        return wallGroups.isEmpty();
    }

    private void prunePassedGroups() {
        for (int i = wallGroups.size - 1; i >= 0; i--) {
            if (wallGroups.get(i).isPassed()) {
                wallGroups.removeIndex(i);
            }
        }
    }

    // ── Input ───────────────────────────────────────────────────────────────

    private void initializeInput() {
        engine.getIOManager().bindKeyContinuous(Input.Keys.UP, () -> {
            if (player != null && player.getPosition().y + player.getRadius() < WORLD_HEIGHT - 5f) {
                player.getVelocity().y = 250f;
            }
        });

        engine.getIOManager().bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (player != null && player.getPosition().y - player.getRadius() > 5f) {
                player.getVelocity().y = -250f;
            }
        });

        engine.getIOManager().bindKeyContinuous(Input.Keys.LEFT, () -> {
            if (player != null && player.getPosition().x - player.getRadius() > 5f) {
                player.getVelocity().x = -250f;
            }
        });

        engine.getIOManager().bindKeyContinuous(Input.Keys.RIGHT, () -> {
            if (player != null && player.getPosition().x + player.getRadius() < WORLD_WIDTH - 5f) {
                player.getVelocity().x = 250f;
            }
        });

        engine.getIOManager().bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) {
                player.requestShoot();
            }
        });

        engine.getIOManager().bindKeyJustPressed(Input.Keys.ESCAPE, () -> {
            SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
            if (settings != null) {
                settings.setPreviousScene("GAME");
            }
            sceneNavigator.goToScene("SETTINGS");
        });
    }

    // ── Update ──────────────────────────────────────────────────────────────

    @Override
    public void update(float deltaTime) {
        if (paused) return;

        scrolledDistance += SCROLL_SPEED * deltaTime;
        background.update(deltaTime, SCROLL_SPEED);

        levelSpawner.update(scrolledDistance + WORLD_WIDTH);

        updatePlayerShooting();

        if (player != null) {
            player.getVelocity().y *= 0.85f;
            player.getVelocity().x *= 0.85f;

            if (player.hasTakenDamage()) {
                gameState.loseLife();
                player.consumeDamage();

                if (gameState.isGameOver()) {
                    transitionToResult();
                    return;
                }
            }

            if (player.hasReachedGate()) {
                player.consumeGoal();
                score++;
                gameState.advanceQuestion();
            }
        }

        super.update(deltaTime);
        updateEnemyShooting();

        for (WallGroup group : wallGroups) {
            group.checkAndMarkPassed(PLAYER_X);
        }

        triggerUpcomingWavesFromPassedGroups();
        prunePassedGroups();

        if (allSegmentsCompleted()) {
            transitionToResult();
            return;
        }

        updateHudForApproachingSegments();
        syncAnswerLabelsToUI();
        uiManager.syncBarrierHp(barriers);
        uiManager.act(deltaTime);

        cleanupInactive();

        if (shootCooldown > 0f) {
            shootCooldown -= deltaTime;
        }
    }
    // ── Shooting ────────────────────────────────────────────────────────────

    private void updatePlayerShooting() {
        if (player != null && player.isShootRequested() && shootCooldown <= 0f) {
            PlayerBullet bullet = new PlayerBullet(
                    nextPlayerBulletId++,
                    new Vector2(player.getPosition().x + 22f, player.getPosition().y - 3f)
            );
            bullet.setCollisionLayer(LAYER_PLAYER);
            bullet.setCollisionMask(LAYER_GATE);

            addEntity(bullet);
            playerBullets.add(bullet);

            player.consumeShoot();
            shootCooldown = SHOOT_INTERVAL;
        }
    }

    private void updateEnemyShooting() {
        for (EnemyShip ship : enemyShips) {
            if (!ship.isActive()) continue;

            if (ship.shouldFire()) {
                Array<EnemyBullet> burst = ship.fire(
                        nextEnemyBulletId,
                        LAYER_ENEMY_BULLET,
                        LAYER_PLAYER
                );

                nextEnemyBulletId += burst.size;

                for (EnemyBullet bullet : burst) {
                    addEntity(bullet);
                    enemyBullets.add(bullet);
                }
            }
        }
    }
    // ── Render ──────────────────────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.03f, 0.04f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);

        background.render(batch, currentBGColor);
        super.render(batch);
        renderUI(batch);
    }

    private void renderUI(SpriteBatch batch) {
        for (int i = 0; i < gameState.getLives(); i++) {
            batch.draw(heartTexture, WORLD_WIDTH - 50 - (i * 40), WORLD_HEIGHT - 50, 30, 30);
        }

        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }

    // ── Cleanup ─────────────────────────────────────────────────────────────

    private void clearDynamicEntities() {
        for (RectangleEntity e : currentWalls) {
            removeEntity(e);
        }
        for (BreakableBarrier e : barriers) {
            removeEntity(e);
        }
        for (PlayerBullet e : playerBullets) {
            removeEntity(e);
        }
        for (EnemyShip e : enemyShips) {
            removeEntity(e);
        }
        for (EnemyBullet e : enemyBullets) {
            removeEntity(e);
        }

        currentWalls.clear();
        barriers.clear();
        playerBullets.clear();
        enemyShips.clear();
        enemyBullets.clear();
        enemyWaves.clear();
    }

    private void cleanupInactive() {
        cleanupArray(currentWalls);
        cleanupBarriers();
        cleanupArray(playerBullets);
        cleanupArray(enemyShips);
        cleanupArray(enemyBullets);
    }

    private void cleanupBarriers() {
        for (int i = barriers.size - 1; i >= 0; i--) {
            BreakableBarrier e = barriers.get(i);
            if (!e.isActive() || e.getPosition().x < -100f) {
                removeEntity(e);
                barriers.removeIndex(i);
            }
        }
    }

    private <T extends Entity> void cleanupArray(Array<T> entities) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            if (!entity.isActive() || entity.getPosition().x < -120f) {
                removeEntity(entity);
                entities.removeIndex(i);
            }
        }
    }

    // ── Result ──────────────────────────────────────────────────────────────

    private void transitionToResult() {
        ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
        if (result != null) {
            result.setScore(score, gameState.getTotalQuestions());
        }

        pendingRestart = true;
        sceneNavigator.goToScene("RESULT");
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (heartTexture != null) heartTexture.dispose();
        if (player != null) player.dispose();
        if (uiManager != null) uiManager.dispose();
        background.dispose();
        BreakableBarrier.disposeTextures();
    }
}