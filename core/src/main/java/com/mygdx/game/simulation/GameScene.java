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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.engine.*;

public class GameScene extends Scene {

    public static final int LAYER_PLAYER = 1;
    public static final int LAYER_GATE = 2;
    public static final int LAYER_HAZARD = 4;

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;
    private static final float PLAYER_X = 140f;
    private static final float SCROLL_SPEED = 180f;
    private static final float GATE_START_X = WORLD_WIDTH + 120f;

    private final ISceneNavigator sceneNavigator;
    private final ObstacleFactory obstacleFactory;
    private final GameStateManager gameState;
    private final GameUIManager uiManager;
    private final Stage stage;
    private final ScrollingBackground background;

    private PlayerCharacter player;
    private Texture heartTexture;

    private final Array<RectangleEntity> currentWalls = new Array<>();
    private final Array<CannonObstacle> cannons = new Array<>();
    private final Array<BulletProjectile> bullets = new Array<>();
    
    private final Array<BreakableBarrier> barriers = new Array<>();
    private final Array<PlayerBullet> playerBullets = new Array<>();
    private int nextPlayerBulletId = 1000;
    private float shootCooldown = 0f;
    private static final float SHOOT_INTERVAL = 0.25f;

    private Color currentBGColor = new Color(0.08f, 0.10f, 0.18f, 1f);
    private int nextBulletId = 500;

    public GameScene(String id, ISceneNavigator sceneNavigator, IGameEngine engine, IQuestionProvider questionProvider) {
        super(id, engine);
        this.sceneNavigator = sceneNavigator;
        this.obstacleFactory = new ObstacleFactory();
        this.gameState = new GameStateManager(questionProvider);

        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.uiManager = new GameUIManager(stage, WORLD_HEIGHT);
        this.heartTexture = new Texture("heart.png");
        this.background = new ScrollingBackground(WORLD_WIDTH, WORLD_HEIGHT);

        initializeInput();
        loadLevel();
    }

    private void loadLevel() {
        if (gameState.isLastQuestion()) {
            ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
            if (result != null) {
                result.setScore(gameState.getTotalQuestions(), gameState.getTotalQuestions());
            }
            sceneNavigator.goToScene("RESULT");
            gameState.resetState();
            return;
        }

        clearDynamicEntities();

        Question q = gameState.getCurrentQuestion();
        if (q == null) return;

        currentBGColor = q.getThemeColor();
        uiManager.updateQuestion(q);

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(PLAYER_X, WORLD_HEIGHT / 2f), 25f);
            player.setCollisionLayer(LAYER_PLAYER);
            player.setCollisionMask(LAYER_GATE | LAYER_HAZARD);
            addEntity(player);
        } else {
            player.getPosition().set(PLAYER_X, WORLD_HEIGHT / 2f);
            player.getVelocity().set(0, 0);
            player.consumeDamage();
            player.consumeGoal();
        }

        Array<String> shuffledAnswers = new Array<>(q.getAnswers());
        shuffledAnswers.shuffle();
        int correctIndex = shuffledAnswers.indexOf(q.getAnswers()[0], false);

        float sectionH = WORLD_HEIGHT / 3f;

        uiManager.updateAnswerTexts(shuffledAnswers.toArray(String.class));
        spawnAnswerGates(sectionH, correctIndex);
        spawnBarriers(sectionH, correctIndex);
        spawnCannons(sectionH, correctIndex);
    }

    private void spawnAnswerGates(float sectionH, int correctIndex) {
        for (int i = 0; i < 3; i++) {
            WallType wallType = (correctIndex == i) ? WallType.CORRECT : WallType.WRONG;

            RectangleEntity wall = obstacleFactory.createWall(
                    wallType,
                    i + 2,
                    GATE_START_X,
                    sectionH * (2 - i),
                    70,
                    sectionH
            );

            wall.getVelocity().x = -SCROLL_SPEED;
            wall.setCollisionLayer(LAYER_GATE);
            wall.setCollisionMask(LAYER_PLAYER);

            addEntity(wall);
            currentWalls.add(wall);
        }
    }

    private void spawnCannons(float sectionH, int correctIndex) {
        for (int lane = 0; lane < 3; lane++) {
            if (lane == correctIndex) continue;

            if (Math.random() < 0.55f) {
                CannonObstacle cannon = new CannonObstacle(
                        100 + lane,
                        new Vector2(WORLD_WIDTH + 320f + (lane * 90f), sectionH * (2 - lane) + 35f),
                        1.6f,
                        -340f
                );

                cannon.getVelocity().x = -SCROLL_SPEED;
                cannon.setCollisionLayer(LAYER_HAZARD);
                cannon.setCollisionMask(LAYER_PLAYER);

                addEntity(cannon);
                cannons.add(cannon);
            }
        }
    }
    
    private void spawnBarriers(float sectionH, int correctIndex) {
        float barrierWidth = 12f;

        for (int i = 0; i < 3; i++) {
            boolean isCorrect = (i == correctIndex);

            BreakableBarrier barrier = new BreakableBarrier(
                    200 + i,
                    new Vector2(GATE_START_X - barrierWidth, sectionH * (2 - i)),
                    barrierWidth,
                    sectionH,
                    3,
                    isCorrect
            );

            barrier.getVelocity().x = -SCROLL_SPEED;
            barrier.setCollisionLayer(LAYER_GATE);
            barrier.setCollisionMask(LAYER_PLAYER);

            addEntity(barrier);
            barriers.add(barrier);
        }
    }

    private void clearDynamicEntities() {
        for (RectangleEntity wall : currentWalls) removeEntity(wall);
        for (CannonObstacle cannon : cannons) removeEntity(cannon);
        for (BulletProjectile bullet : bullets) removeEntity(bullet);
        for (BreakableBarrier barrier : barriers) removeEntity(barrier);
        for (PlayerBullet bullet : playerBullets) removeEntity(bullet);

        barriers.clear();
        playerBullets.clear();

        currentWalls.clear();
        cannons.clear();
        bullets.clear();
    }

    private void initializeInput() {
        engine.getIOManager().bindKeyContinuous(Input.Keys.UP, () -> {
            if (player != null && player.getPosition().y + player.getRadius() < WORLD_HEIGHT - 5) {
                player.getVelocity().y = 250;
            }
        });

        engine.getIOManager().bindKeyContinuous(Input.Keys.DOWN, () -> {
            if (player != null && player.getPosition().y - player.getRadius() > 5) {
                player.getVelocity().y = -250;
            }
        });
        
        engine.getIOManager().bindKeyJustPressed(Input.Keys.SPACE, () -> {
            if (player != null) {
                player.requestShoot();
            }
        });
    }

    private void updateCannons(float deltaTime) {
        for (CannonObstacle cannon : cannons) {
            if (!cannon.isActive()) continue;

            if (cannon.shouldFire(deltaTime)) {
                BulletProjectile bullet = cannon.fire(nextBulletId++);
                bullet.setCollisionLayer(LAYER_HAZARD);
                bullet.setCollisionMask(LAYER_PLAYER);
                addEntity(bullet);
                bullets.add(bullet);
            }
        }
    }
    
    private void updatePlayerShooting() {
        if (player != null && player.shootRequested && shootCooldown <= 0f) {
            PlayerBullet bullet = new PlayerBullet(
                    nextPlayerBulletId++,
                    new Vector2(player.getPosition().x + 22f, player.getPosition().y - 3f)
            );

            bullet.setCollisionLayer(LAYER_PLAYER);
            bullet.setCollisionMask(LAYER_GATE | LAYER_HAZARD);

            addEntity(bullet);
            playerBullets.add(bullet);

            player.shootRequested = false;
            shootCooldown = SHOOT_INTERVAL;
        } else if (player != null) {
            player.shootRequested = false;
        }
    }

    private void cleanupInactive() {
        for (int i = currentWalls.size - 1; i >= 0; i--) {
            if (!currentWalls.get(i).isActive() || currentWalls.get(i).getPosition().x + currentWalls.get(i).getWidth() < -10f) {
                removeEntity(currentWalls.get(i));
                currentWalls.removeIndex(i);
            }
        }

        for (int i = cannons.size - 1; i >= 0; i--) {
            if (!cannons.get(i).isActive()) {
                removeEntity(cannons.get(i));
                cannons.removeIndex(i);
            }
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).isActive()) {
                removeEntity(bullets.get(i));
                bullets.removeIndex(i);
            }
        }
        
        for (int i = barriers.size - 1; i >= 0; i--) {
            if (!barriers.get(i).isActive()) {
                removeEntity(barriers.get(i));
                barriers.removeIndex(i);
            }
        }

        for (int i = playerBullets.size - 1; i >= 0; i--) {
            if (!playerBullets.get(i).isActive()) {
                removeEntity(playerBullets.get(i));
                playerBullets.removeIndex(i);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
            if (settings != null) settings.setPreviousScene("MENU");
            sceneNavigator.goToScene("SETTINGS");
            return;
        }

        background.update(deltaTime, SCROLL_SPEED);
        updateCannons(deltaTime);
        
        if (shootCooldown > 0f) {
            shootCooldown -= deltaTime;
        }
        
        updatePlayerShooting();

        super.update(deltaTime);

        if (player != null) {
            player.getVelocity().y *= 0.85f;

            if (player.tookDamage) {
                gameState.loseLife();
                player.consumeDamage();

                if (gameState.isGameOver()) {
                    ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
                    if (result != null) {
                        result.setScore(gameState.getCurrentQuestionIndex(), gameState.getTotalQuestions());
                    }
                    sceneNavigator.goToScene("RESULT");
                    gameState.resetState();
                    return;
                }

                loadLevel();
                return;
            }

            if (player.reachedGate) {
                player.consumeGoal();
                gameState.advanceQuestion();
                loadLevel();
                return;
            }
        }

        uiManager.syncAnswerLabelsToWalls(currentWalls);
        uiManager.syncBarrierHp(barriers);  
        uiManager.act(deltaTime);
        cleanupInactive();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.03f, 0.04f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);

        background.render(batch, currentBGColor);

        batch.setColor(0f, 0f, 0f, 0.30f);
        batch.draw(background.getPixel(), 12, WORLD_HEIGHT - 70, WORLD_WIDTH - 24, 52);
        batch.draw(background.getPixel(), WORLD_WIDTH - 165, WORLD_HEIGHT - 58, 145, 38);
        batch.setColor(Color.WHITE);

        super.render(batch);

        for (int i = 0; i < gameState.getLives(); i++) {
            batch.draw(heartTexture, WORLD_WIDTH - 50 - (i * 40), WORLD_HEIGHT - 50, 30, 30);
        }

        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }

    public void dispose() {
        if (heartTexture != null) heartTexture.dispose();
        if (player != null) player.dispose();
        if (uiManager != null) uiManager.dispose();
        if (background != null) background.dispose();
    }
    
    @Override
    public void show() {
        super.show();
        resetGame();
    }

    public void resetGame() {
        gameState.resetState();

        clearDynamicEntities();

        if (player != null) {
            removeEntity(player);
            player.dispose();
            player = null;
        }

        nextBulletId = 500;
        nextPlayerBulletId = 1000;

        loadLevel();
    }
}