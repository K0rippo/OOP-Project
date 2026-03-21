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

    public static final int LAYER_PLAYER       = 1;
    public static final int LAYER_GATE         = 2;
    public static final int LAYER_ENEMY        = 4;
    public static final int LAYER_ENEMY_BULLET = 8;

    private static final float WORLD_WIDTH         = 1280f;
    private static final float WORLD_HEIGHT        = 720f;
    private static final float PLAYER_X            = 140f;
    private static final float SCROLL_SPEED        = 100f;
    private static final float FIRST_SEGMENT_X     = WORLD_WIDTH + 200f;
    private static final float SHOOT_INTERVAL      = 0.25f;
    private static final float HUD_SWITCH_DISTANCE = WORLD_WIDTH;

    private final IGameEngine         engine; 
    private final GameStateManager    gameState;
    private final GameUIManager       uiManager;
    private final Stage               stage;
    private final ScrollingBackground background;
    private final IQuestionProvider   questionProvider;
    private ContinuousLevelSpawner    levelSpawner;
    private final GameInputHandler    inputHandler;
    private final AudioManager        audioManager;
    private final CombatDirector      combatDirector;
    private final EntityCullingService cullingService;
    private final ResultTransitionService resultTransitionService;
    private final WallHudCoordinator wallHudCoordinator;
    private final SegmentSpawnService segmentSpawnService;
    private final GameProgressionService progressionService;

    private PlayerCharacter player;
    private Texture         heartTexture;

    private final Array<EnemyWave> enemyWaves = new Array<>();

    private Color currentBGColor = new Color(0.08f, 0.10f, 0.18f, 1f);

    private int nextPlayerBulletId = 1000;
    private int nextEnemyBulletId  = 3000;

    private float shootCooldown    = 0f;
    private float scrolledDistance = 0f;
    private int   score            = 0;

    private boolean paused         = false;
    private boolean pendingRestart = true;

    public GameScene(String id, ISceneNavigator sceneNavigator, IGameEngine engine,
                     IQuestionProvider questionProvider, AudioManager audioManager) {
        super(id);
        this.engine           = engine;
        this.questionProvider = questionProvider;
        this.audioManager     = audioManager;
        ObstacleFactory obstacleFactory = new ObstacleFactory();
        this.gameState        = new GameStateManager(questionProvider);
        this.stage            = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.uiManager        = new GameUIManager(stage, WORLD_HEIGHT);
        this.heartTexture     = new Texture("heart.png");
        this.background       = new ScrollingBackground(WORLD_WIDTH, WORLD_HEIGHT);
        this.inputHandler     = new GameInputHandler(engine, sceneNavigator, WORLD_HEIGHT);
        EnemyWaveFactory enemyWaveFactory = new EnemyWaveFactory();
        this.combatDirector   = new CombatDirector(engine, audioManager, SHOOT_INTERVAL,
            LAYER_PLAYER, LAYER_GATE, LAYER_ENEMY, LAYER_ENEMY_BULLET);
        this.cullingService   = new EntityCullingService(engine, WORLD_WIDTH,
            LAYER_PLAYER, LAYER_GATE, LAYER_ENEMY, LAYER_ENEMY_BULLET);
        this.resultTransitionService = new ResultTransitionService(sceneNavigator);
        this.wallHudCoordinator = new WallHudCoordinator();
        this.segmentSpawnService = new SegmentSpawnService(
                engine,
                questionProvider,
                obstacleFactory,
                enemyWaveFactory,
                audioManager,
                wallHudCoordinator,
                enemyWaves,
                this::showQuestionOnHud,
                WORLD_HEIGHT,
                SCROLL_SPEED,
                LAYER_PLAYER,
                LAYER_GATE,
                LAYER_ENEMY
        );
        this.progressionService = new GameProgressionService();

        this.inputHandler.initializeInput();
        startLevel();
    }

    private void startLevel() {
        gameState.resetState();
        scrolledDistance = 0f;
        score            = 0;

        clearDynamicEntities();
        wallHudCoordinator.clear();
        enemyWaves.clear();

        nextPlayerBulletId = 1000;
        nextEnemyBulletId  = 3000;
        segmentSpawnService.reset();

        questionProvider.shuffleForNewGame();

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(PLAYER_X, WORLD_HEIGHT / 2f), 25f);
            
            // --- NEW: Attach the sound callbacks! ---
            player.setSoundCallbacks(
                () -> { if (audioManager != null) audioManager.playShipDamageSound(); },
                () -> { if (audioManager != null) audioManager.playCorrectGateSound(); }
            );
            // ----------------------------------------
            
            engine.addEntity(player);
        } else {
            player.setPosition(PLAYER_X, WORLD_HEIGHT / 2f);
            player.setVelocity(new Vector2(0f, 0f));
            player.consumeDamage();
            player.consumeGoal();
            player.consumeShoot();
        }

        configurePlayerCollision();

        inputHandler.setPlayer(player);
        levelSpawner = new ContinuousLevelSpawner(questionProvider, FIRST_SEGMENT_X, segmentSpawnService::spawnSegment);
    }

    private void configurePlayerCollision() {
        player.setCollisionLayer(LAYER_PLAYER);
        player.setCollisionMask(LAYER_GATE | LAYER_ENEMY_BULLET | LAYER_ENEMY);
    }

    public void requestRestart() {
        pendingRestart = true;
    }

    @Override
    public void show() {
        super.show();
        paused = false;
        engine.setSpeedMultiplier(1f);

        if (audioManager != null) {
            audioManager.playMusic();
        }

        if (pendingRestart) {
            pendingRestart = false;
            startLevel();
        }
    }

    @Override
    public void hide() {
        super.hide();
        paused = true;
        engine.setSpeedMultiplier(0f);

        if (audioManager != null) {
            audioManager.pauseMusic();
        }
    }

    private void showQuestionOnHud(int questionIndex) {
        Question question = questionProvider.getQuestion(questionIndex);
        if (question == null) return;

        currentBGColor = question.getThemeColor();
        uiManager.updateQuestion(question);
    }

    private boolean allSegmentsCompleted() {
        if (!levelSpawner.allSegmentsSpawned()) return false;
        return wallHudCoordinator.isEmpty();
    }

    @Override
    public void update(float deltaTime) {
        if (!isActive() || paused) return;

        scrolledDistance += SCROLL_SPEED * deltaTime;
        background.update(deltaTime, SCROLL_SPEED);

        levelSpawner.update(scrolledDistance + WORLD_WIDTH);

        CombatDirector.CombatState combatState = combatDirector.update(
            player,
            shootCooldown,
            deltaTime,
            nextPlayerBulletId,
            nextEnemyBulletId
        );
        shootCooldown = combatState.getShootCooldown();
        nextPlayerBulletId = combatState.getNextPlayerBulletId();
        nextEnemyBulletId = combatState.getNextEnemyBulletId();

        int oldScore = score;
        
        GameProgressionService.ProgressionResult progressionResult =
                progressionService.update(player, gameState, score);
        score = progressionResult.getScore();
        if (progressionResult.shouldTransitionToResult()) {
            transitionToResult();
            return;
        }

        if (score > oldScore) {
            for (Entity e : engine.getEntitiesByLayer(LAYER_GATE)) {
                if (e.getPosition().x < player.getPosition().x + 600f) {
                    e.getPosition().x = -2000f;
                }
            }
        }

        engine.update(deltaTime);
        cleanupOffScreen();

        wallHudCoordinator.markPassedGroups(PLAYER_X);
        wallHudCoordinator.triggerUpcomingWaves(enemyWaves);
        wallHudCoordinator.prunePassedGroups();

        if (allSegmentsCompleted()) {
            transitionToResult();
            return;
        }

        wallHudCoordinator.updateHudForApproachingSegments(PLAYER_X, HUD_SWITCH_DISTANCE, this::showQuestionOnHud);
        wallHudCoordinator.syncAnswerLabelsToUI(uiManager);

        uiManager.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive()) return;
        
        Gdx.gl.glClearColor(0.03f, 0.04f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);

        background.render(batch, currentBGColor);

        engine.render(batch);

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

    private void clearDynamicEntities() {
        cullingService.clearDynamicEntities(player);
    }

    private void cleanupOffScreen() {
        cullingService.cleanupOffScreen(player);
    }

    private void transitionToResult() {
        resultTransitionService.transition(score, gameState.getTotalQuestions());
        pendingRestart = true;
    }

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