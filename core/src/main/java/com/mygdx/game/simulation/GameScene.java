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

/**
 * GameScene — main gameplay scene.
 *
 * SOLID compliance:
 *  SRP — WallGroup is private; spawning delegated to ContinuousLevelSpawner.
 *  OCP — new question types only require a new IQuestionProvider.
 *  DIP — depends on IQuestionProvider, ISceneNavigator, IGameEngine abstractions.
 *
 * APIE compliance:
 *  Encapsulation — PlayerCharacter state via query/consume methods only.
 *  Polymorphism  — Entity hierarchy; WallType enum.
 *  Inheritance   — extends Scene.
 *  Abstraction   — all dependencies are interfaces.
 */
public class GameScene extends Scene {

    // ── Collision layers ────────────────────────────────────────────────────
    public static final int LAYER_PLAYER = 1;
    public static final int LAYER_GATE   = 2;

    // ── World constants ──────────────────────────────────────────────────────
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
    private ContinuousLevelSpawner    levelSpawner;

    // ── Entities ────────────────────────────────────────────────────────────
    private PlayerCharacter player;
    private Texture         heartTexture;

    private final Array<RectangleEntity>  currentWalls  = new Array<>();
    private final Array<BreakableBarrier> barriers      = new Array<>();
    private final Array<PlayerBullet>     playerBullets = new Array<>();

    // ── State ────────────────────────────────────────────────────────────────
    private Color   currentBGColor     = new Color(0.08f, 0.10f, 0.18f, 1f);
    private int     nextPlayerBulletId = 1000;
    private float   shootCooldown      = 0f;
    private float   scrolledDistance   = 0f;
    private int     score              = 0;
    /** True while this scene is not the active scene (e.g. settings is open). */
    private boolean paused          = false;
    /** True when the next show() should restart the level from scratch. */
    private boolean pendingRestart  = true;

    // ── WallGroup ─────────────────────────────────────────────────────────────

    /**
     * WallGroup — private inner class, never exposed outside GameScene.
     * Holds the 3 gate walls for one question segment and their shuffled answers.
     * Encapsulates all position-checking and label-data-filling logic.
     */
    private static class WallGroup {
        private final int                    questionIndex;
        private final Array<RectangleEntity> walls           = new Array<>();
        private final String[]               shuffledAnswers;
        private boolean                      hudShown        = false;
        private boolean                      passed          = false;

        WallGroup(int questionIndex, String[] shuffledAnswers) {
            this.questionIndex   = questionIndex;
            this.shuffledAnswers = shuffledAnswers;
        }

        int     getQuestionIndex() { return questionIndex; }
        boolean isHudShown()       { return hudShown; }
        boolean isPassed()         { return passed; }
        void    markHudShown()     { hudShown = true; }
        void    addWall(RectangleEntity wall) { walls.add(wall); }

        float leadingWallX() {
            return walls.isEmpty() ? Float.MAX_VALUE : walls.first().getPosition().x;
        }

        void checkAndMarkPassed(float playerX) {
            if (passed || walls.isEmpty()) return;
            RectangleEntity first = walls.first();
            if (first.getPosition().x + first.getWidth() < playerX) passed = true;
        }

        int fillAnswerData(String[] texts, float[] xs, float[] ys,
                           float[] heights, int offset) {
            if (passed) return offset;
            for (int i = 0; i < walls.size && i < shuffledAnswers.length; i++) {
                RectangleEntity w = walls.get(i);
                texts  [offset] = shuffledAnswers[i];
                xs     [offset] = w.getPosition().x;
                ys     [offset] = w.getPosition().y;
                heights[offset] = w.getHeight();
                offset++;
            }
            return offset;
        }
    }

    private final Array<WallGroup> wallGroups = new Array<>();

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

        initializeInput();
        startLevel();
    }

    // ── Level init ───────────────────────────────────────────────────────────

    private void startLevel() {
        gameState.resetState();
        scrolledDistance = 0f;
        score            = 0;

        clearDynamicEntities();
        wallGroups.clear();

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(PLAYER_X, WORLD_HEIGHT / 2f), 25f);
            player.setCollisionLayer(LAYER_PLAYER);
            player.setCollisionMask(LAYER_GATE);
            addEntity(player);
        } else {
            player.getPosition().set(PLAYER_X, WORLD_HEIGHT / 2f);
            player.getVelocity().set(0, 0);
            player.consumeDamage();
            player.consumeGoal();
        }

        levelSpawner = new ContinuousLevelSpawner(questionProvider, FIRST_SEGMENT_X, this::spawnSegment);
    }

    // ── Lifecycle show/hide ──────────────────────────────────────────────────

    /**
     * Called by MenuScene and ResultScene before navigating to "GAME" so that
     * show() knows to restart from scratch rather than just unpause.
     */
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



    private void spawnSegment(LevelSegment segment) {
        int qi     = segment.getQuestionIndex();
        Question q = questionProvider.getQuestion(qi);
        if (q == null) return;

        Array<String> shuffled = new Array<>(q.getAnswers());
        shuffled.shuffle();
        int      correctIndex = shuffled.indexOf(q.getAnswers()[0], false);
        String[] shuffledArr  = shuffled.toArray(String.class);

        float sectionH = WORLD_HEIGHT / 3f;

        WallGroup group = new WallGroup(qi, shuffledArr);
        spawnAnswerGates(segment.gateX(),    sectionH, correctIndex, qi, group);
        spawnBarriers   (segment.barrierX(), sectionH, correctIndex, qi);
        wallGroups.add(group);

        // Show the first segment's question immediately; later ones use distance-based switch.
        if (wallGroups.size == 1) {
            group.markHudShown();
            showQuestionOnHud(qi);
        }
    }

    // ── Spawning helpers ─────────────────────────────────────────────────────

    private void spawnAnswerGates(float spawnX, float sectionH, int correctIndex,
                                   int segId, WallGroup group) {
        for (int i = 0; i < 3; i++) {
            WallType type = (correctIndex == i) ? WallType.CORRECT : WallType.WRONG;
            RectangleEntity wall = obstacleFactory.createWall(
                    type, segId * 10 + i,
                    spawnX, sectionH * (2 - i), 70, sectionH);
            wall.getVelocity().x = -SCROLL_SPEED;
            wall.setCollisionLayer(LAYER_GATE);
            wall.setCollisionMask(LAYER_PLAYER);
            addEntity(wall);
            currentWalls.add(wall);
            group.addWall(wall);
        }
    }

    private void spawnBarriers(float spawnX, float sectionH, int correctIndex, int segId) {
        for (int i = 0; i < 3; i++) {
            BreakableBarrier barrier = new BreakableBarrier(
                    200 + segId * 10 + i,
                    new Vector2(spawnX, sectionH * (2 - i)),
                    12f, sectionH, 3, (i == correctIndex));
            barrier.getVelocity().x = -SCROLL_SPEED;
            barrier.setCollisionLayer(LAYER_GATE);
            barrier.setCollisionMask(LAYER_PLAYER);
            addEntity(barrier);
            barriers.add(barrier);
        }
    }

    // ── HUD helpers ──────────────────────────────────────────────────────────

    private void showQuestionOnHud(int qi) {
        Question q = questionProvider.getQuestion(qi);
        if (q == null) return;
        currentBGColor = q.getThemeColor();
        uiManager.updateQuestion(q);
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
        String[] texts   = new String[max];
        float[]  xs      = new float[max];
        float[]  ys      = new float[max];
        float[]  heights = new float[max];
        int count = 0;
        for (WallGroup group : wallGroups) {
            count = group.fillAnswerData(texts, xs, ys, heights, count);
        }
        uiManager.syncAnswerLabels(texts, xs, ys, heights, count);
    }

    // ── End-game condition ───────────────────────────────────────────────────

    private boolean allSegmentsCompleted() {
        if (!levelSpawner.allSegmentsSpawned()) return false;
        return wallGroups.isEmpty();
    }

    private void prunePassedGroups() {
        for (int i = wallGroups.size - 1; i >= 0; i--) {
            if (wallGroups.get(i).isPassed()) wallGroups.removeIndex(i);
        }
    }

    // ── Input ────────────────────────────────────────────────────────────────

    private void initializeInput() {
        engine.getIOManager().bindKeyContinuous(Input.Keys.UP, () -> {
            if (player != null && player.getPosition().y + player.getRadius() < WORLD_HEIGHT - 5)
                player.getVelocity().y = 250;
        });
        engine.getIOManager().bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (player != null && player.getPosition().y - player.getRadius() > 5)
                player.getVelocity().y = -250;
        });
        engine.getIOManager().bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) player.requestShoot();
        });
        // ESC opens settings without losing game state
        engine.getIOManager().bindKeyJustPressed(Input.Keys.ESCAPE, () -> {
            SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
            if (settings != null) settings.setPreviousScene("GAME");
            sceneNavigator.goToScene("SETTINGS");
        });
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    public void update(float deltaTime) {
        if (paused) return;

        scrolledDistance += SCROLL_SPEED * deltaTime;
        background.update(deltaTime, SCROLL_SPEED);

        levelSpawner.update(scrolledDistance + WORLD_WIDTH);

        updatePlayerShooting();

        if (player != null) {
            player.getVelocity().y *= 0.85f;

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

        for (WallGroup g : wallGroups) g.checkAndMarkPassed(PLAYER_X);
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
        if (shootCooldown > 0) shootCooldown -= deltaTime;
    }

    // ── Player shooting ───────────────────────────────────────────────────────

    private void updatePlayerShooting() {
        if (player != null && player.isShootRequested() && shootCooldown <= 0f) {
            PlayerBullet bullet = new PlayerBullet(
                    nextPlayerBulletId++,
                    new Vector2(player.getPosition().x + 22f, player.getPosition().y - 3f));
            bullet.setCollisionLayer(LAYER_PLAYER);
            bullet.setCollisionMask(LAYER_GATE);
            addEntity(bullet);
            playerBullets.add(bullet);
            player.consumeShoot();
            shootCooldown = SHOOT_INTERVAL;
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

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

    // ── Cleanup ──────────────────────────────────────────────────────────────

    private void clearDynamicEntities() {
        for (RectangleEntity  e : currentWalls)  removeEntity(e);
        for (BreakableBarrier e : barriers)       removeEntity(e);
        for (PlayerBullet     e : playerBullets)  removeEntity(e);
        currentWalls.clear();
        barriers.clear();
        playerBullets.clear();
    }

    private void cleanupInactive() {
        cleanupArray(currentWalls);
        cleanupArray(barriers);
        cleanupArray(playerBullets);
    }

    private <T extends Entity> void cleanupArray(Array<T> entities) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            if (!e.isActive() || e.getPosition().x < -100f) {
                removeEntity(e);
                entities.removeIndex(i);
            }
        }
    }

    // ── Result ───────────────────────────────────────────────────────────────

    private void transitionToResult() {
        ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
        if (result != null) result.setScore(score, gameState.getTotalQuestions());
        pendingRestart = true; // next show() will restart the level
        sceneNavigator.goToScene("RESULT");
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (heartTexture != null) heartTexture.dispose();
        if (player       != null) player.dispose();
        if (uiManager    != null) uiManager.dispose();
        background.dispose();
    }
}
