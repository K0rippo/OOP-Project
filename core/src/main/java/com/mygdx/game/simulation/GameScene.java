package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
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

    public static final int LAYER_PLAYER = 1;
    public static final int LAYER_GATE   = 2;

    private static final float WORLD_WIDTH         = 1280f;
    private static final float WORLD_HEIGHT        = 720f;
    private static final float PLAYER_X            = 140f;
    private static final float SCROLL_SPEED        = 100f;
    private static final float FIRST_SEGMENT_X     = WORLD_WIDTH + 200f;
    private static final float SHOOT_INTERVAL      = 0.25f;
    private static final float HUD_SWITCH_DISTANCE = WORLD_WIDTH;

    private final ISceneNavigator     sceneNavigator;
    private final ObstacleFactory     obstacleFactory;
    private final GameStateManager    gameState;
    private final GameUIManager       uiManager;
    private final Stage               stage;
    private final ScrollingBackground background;
    private final IQuestionProvider   questionProvider;
    private ContinuousLevelSpawner    levelSpawner;
    private final GameInputHandler    inputHandler;

    private PlayerCharacter player;
    private Texture         heartTexture;

    private Color   currentBGColor     = new Color(0.08f, 0.10f, 0.18f, 1f);
    private int     nextPlayerBulletId = 1000;
    private float   shootCooldown      = 0f;
    private float   scrolledDistance   = 0f;
    private int     score              = 0;
    private boolean paused             = false;
    private boolean pendingRestart     = true;

    private static class WallGroup {
        private final int                    questionIndex;
        private final Array<RectangleEntity> walls           = new Array<>();
        private final String[]               shuffledAnswers;
        private boolean                      hudShown        = false;
        private boolean                      passed          = false;

        // Initializes the wall group with the question and answers
        WallGroup(int questionIndex, String[] shuffledAnswers) {
            this.questionIndex   = questionIndex;
            this.shuffledAnswers = shuffledAnswers;
        }

        // Retrieves the index of the question
        int getQuestionIndex() { return questionIndex; }

        // Checks if the HUD currently shows this question
        boolean isHudShown() { return hudShown; }

        // Checks if the player passed the wall group
        boolean isPassed() { return passed; }

        // Marks the HUD as shown for this group
        void markHudShown() { hudShown = true; }

        // Adds a wall entity to the group
        void addWall(RectangleEntity wall) { walls.add(wall); }

        // Gets the X position of the first wall
        float leadingWallX() {
            return walls.isEmpty() ? Float.MAX_VALUE : walls.first().getPosition().x;
        }

        // Checks the player position and marks the group as passed if cleared
        void checkAndMarkPassed(float playerX) {
            if (passed || walls.isEmpty()) return;
            RectangleEntity first = walls.first();
            if (first.getPosition().x + first.getWidth() < playerX) passed = true;
        }

        // Populates arrays with answer data for rendering
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

    // Initializes the main gameplay scene
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
        this.inputHandler     = new GameInputHandler(engine, sceneNavigator, WORLD_HEIGHT);

        this.inputHandler.initializeInput();
        startLevel();
    }

    // Resets the level state and clears entities
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

        inputHandler.setPlayer(player);
        levelSpawner = new ContinuousLevelSpawner(questionProvider, FIRST_SEGMENT_X, this::spawnSegment);
    }

    // Flags the scene to restart on the next show
    public void requestRestart() {
        pendingRestart = true;
    }

    // Restores speed and starts level when the scene becomes active
    @Override
    public void show() {
        paused = false;
        engine.setSpeedMultiplier(1f);
        if (pendingRestart) {
            pendingRestart = false;
            startLevel();
        }
    }

    // Pauses game elements when the scene becomes inactive
    @Override
    public void hide() {
        paused = true;
        engine.setSpeedMultiplier(0f);
    }

    // Creates a new segment with questions, gates, and barriers
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

        if (wallGroups.size == 1) {
            group.markHudShown();
            showQuestionOnHud(qi);
        }
    }

    // Generates the answer gate walls
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
            group.addWall(wall);
        }
    }

    // Generates breakable barriers in front of the gates
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
        }
    }

    // Updates the UI with the specified question and theme
    private void showQuestionOnHud(int qi) {
        Question q = questionProvider.getQuestion(qi);
        if (q == null) return;
        currentBGColor = q.getThemeColor();
        uiManager.updateQuestion(q);
    }

    // Checks distance and updates the HUD for upcoming questions
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

    // Synchronizes physical wall positions with UI labels
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

    // Determines if all spawned level segments are passed
    private boolean allSegmentsCompleted() {
        if (!levelSpawner.allSegmentsSpawned()) return false;
        return wallGroups.isEmpty();
    }

    // Removes wall groups that the player has bypassed
    private void prunePassedGroups() {
        for (int i = wallGroups.size - 1; i >= 0; i--) {
            if (wallGroups.get(i).isPassed()) wallGroups.removeIndex(i);
        }
    }

    // Processes logic for each frame
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
        cleanupOffScreen();

        for (WallGroup g : wallGroups) g.checkAndMarkPassed(PLAYER_X);
        prunePassedGroups();

        if (allSegmentsCompleted()) {
            transitionToResult();
            return;
        }

        updateHudForApproachingSegments();
        syncAnswerLabelsToUI();
        
        Array<BreakableBarrier> activeBarriers = new Array<>();
        for (Entity e : engine.getEntitiesByLayer(LAYER_GATE)) {
            if (e instanceof BreakableBarrier) {
                activeBarriers.add((BreakableBarrier) e);
            }
        }
        uiManager.syncBarrierHp(activeBarriers);
        
        uiManager.act(deltaTime);

        if (shootCooldown > 0) shootCooldown -= deltaTime;
    }

    // Removes entities that have completely scrolled off the screen
    private void cleanupOffScreen() {
        for (Entity e : engine.getEntitiesByLayer(LAYER_GATE)) {
            if (e.getPosition().x < -200f) {
                removeEntity(e);
            }
        }
        for (Entity e : engine.getEntitiesByLayer(LAYER_PLAYER)) {
            if (e != player && e.getPosition().x > WORLD_WIDTH + 200f) {
                removeEntity(e);
            }
        }
    }

    // Triggers bullet creation based on player input
    private void updatePlayerShooting() {
        if (player != null && player.isShootRequested() && shootCooldown <= 0f) {
            PlayerBullet bullet = new PlayerBullet(
                    nextPlayerBulletId++,
                    new Vector2(player.getPosition().x + 22f, player.getPosition().y - 3f));
            bullet.setCollisionLayer(LAYER_PLAYER);
            bullet.setCollisionMask(LAYER_GATE);
            addEntity(bullet);
            player.consumeShoot();
            shootCooldown = SHOOT_INTERVAL;
        }
    }

    // Draws the background and game stage
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

    // Draws the player lives
    private void renderUI(SpriteBatch batch) {
        for (int i = 0; i < gameState.getLives(); i++) {
            batch.draw(heartTexture, WORLD_WIDTH - 50 - (i * 40), WORLD_HEIGHT - 50, 30, 30);
        }
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }

    // Removes specific entities from the engine
    private void clearDynamicEntities() {
        for (Entity e : engine.getEntitiesByLayer(LAYER_GATE)) {
            removeEntity(e);
        }
        for (Entity e : engine.getEntitiesByLayer(LAYER_PLAYER)) {
            if (e != player) {
                removeEntity(e);
            }
        }
    }

    // Switches view to the result scene
    private void transitionToResult() {
        ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
        if (result != null) result.setScore(score, gameState.getTotalQuestions());
        pendingRestart = true; 
        sceneNavigator.goToScene("RESULT");
    }

    // Adjusts viewport elements to screen size
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Frees memory
    public void dispose() {
        if (heartTexture != null) heartTexture.dispose();
        if (player       != null) player.dispose();
        if (uiManager    != null) uiManager.dispose();
        background.dispose();
    }
}