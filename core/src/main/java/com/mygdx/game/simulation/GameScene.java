package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.engine.*;

/**
 * GameScene - manages the game loop, level progression, and scene coordination.
 * Refactored to follow SOLID principles by delegating responsibilities to:
 * - GameStateManager: game state and level progression (SRP)
 * - GameUIManager: UI setup and rendering (SRP)
 * - ObstacleFactory: obstacle creation (Factory pattern, OCP)
 * - Depends on ISceneNavigator and IGameEngine abstractions (DIP)
 */
public class GameScene extends Scene {

    public static final int LAYER_PLAYER = 1;
    public static final int LAYER_WALL = 2;

    private ISceneNavigator sceneNavigator;
    private ObstacleFactory obstacleFactory;
    private GameStateManager gameState;
    private GameUIManager uiManager;
    private Stage stage;

    private PlayerCharacter player;
    private Texture heartTexture;
    private Array<RectangleEntity> currentWalls = new Array<>();
    private Color currentBGColor = Color.BLACK;

    private final float WORLD_WIDTH = 800f;
    private final float WORLD_HEIGHT = 600f;

    public GameScene(String id, ISceneNavigator sceneNavigator, IGameEngine engine, IQuestionProvider questionProvider) {
        super(id, engine);
        this.sceneNavigator = sceneNavigator;
        this.obstacleFactory = new ObstacleFactory();
        this.gameState = new GameStateManager(questionProvider);
        
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.uiManager = new GameUIManager(stage, WORLD_HEIGHT);
        this.heartTexture = new Texture("heart.png");
        
        initializeInput();
        loadLevel(0);
    }

    private void loadLevel(int index) {
        if (gameState.isLastQuestion()) {
            ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
            if (result != null) result.setScore(gameState.getTotalQuestions(), gameState.getTotalQuestions());
            sceneNavigator.goToScene("RESULT");
            gameState.resetState();
            return;
        }

        Question q = gameState.getCurrentQuestion();
        if (q == null) return;
        
        currentBGColor = q.getThemeColor();
        uiManager.updateQuestion(q);
        
        float wallX = WORLD_WIDTH * 0.8f;
        float speedX = (wallX - 50f) / q.getTimeToReach();

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(50, WORLD_HEIGHT / 2), 25f, speedX);
            player.setCollisionLayer(LAYER_PLAYER);
            player.setCollisionMask(LAYER_WALL);
            addEntity(player);
        } else {
            player.getPosition().set(50, WORLD_HEIGHT / 2);
            player.getVelocity().set(speedX, 0);
            player.isLevelComplete = false;
            player.hitWrongWall = false;
        }

        Array<String> shuffledAnswers = new Array<>(q.getAnswers());
        shuffledAnswers.shuffle();
        int correctIndex = shuffledAnswers.indexOf(q.getAnswers()[0], false);
        float sectionH = WORLD_HEIGHT / 3f;
        
        uiManager.updateAnswerLabels(shuffledAnswers.toArray(String.class), wallX, sectionH);
        spawnObstacles(wallX, sectionH, correctIndex);
    }

    private void spawnObstacles(float x, float h, int correctPosition) {
        for (RectangleEntity wall : currentWalls) removeEntity(wall);
        currentWalls.clear();

        for (int i = 0; i < 3; i++) {
            WallType wallType = (correctPosition == i) ? WallType.CORRECT : WallType.WRONG;
            RectangleEntity wall = obstacleFactory.createWall(wallType, i + 2, x, h * (2 - i), 50, h);
            
            wall.setCollisionLayer(LAYER_WALL);
            wall.setCollisionMask(LAYER_PLAYER);
            addEntity(wall);
            currentWalls.add(wall);
        }
    }

    private void initializeInput() {
        // UP: Only allow upward movement if not at ceiling
        engine.getIOManager().bindKeyContinuous(Input.Keys.UP, () -> { 
            if (player != null) {
                // Check if player is not at ceiling (position + radius < world height)
                if (player.getPosition().y + player.getRadius() < WORLD_HEIGHT - 5) {
                    player.getVelocity().y = 300;
                } else {
                    player.getVelocity().y = 0;  // Stop if at ceiling
                }
            }
        });
        
        // DOWN: Only allow downward movement if not at floor
        engine.getIOManager().bindKeyContinuous(Input.Keys.DOWN, () -> { 
            if (player != null) {
                // Check if player is not at floor (position - radius > 0)
                if (player.getPosition().y - player.getRadius() > 5) {
                    player.getVelocity().y = -300;
                } else {
                    player.getVelocity().y = 0;  // Stop if at floor
                }
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
            if (settings != null) settings.setPreviousScene("MENU");
            sceneNavigator.goToScene("SETTINGS");
            return;
        }

        super.update(deltaTime);
        if (player != null) {
            player.getVelocity().y *= 0.85f;
            if (player.hitWrongWall) {
                gameState.loseLife();
                player.hitWrongWall = false; 
                if (gameState.isGameOver()) {
                    ResultScene result = (ResultScene) sceneNavigator.getScene("RESULT");
                    if (result != null) result.setScore(gameState.getCurrentQuestionIndex(), gameState.getTotalQuestions());
                    sceneNavigator.goToScene("RESULT");
                    gameState.resetState();
                    return;
                }
            }
            if (player.isLevelComplete) {
                gameState.advanceQuestion();
                loadLevel(gameState.getCurrentQuestionIndex());
            }
        }
        uiManager.act(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(currentBGColor.r, currentBGColor.g, currentBGColor.b, 1);
        
        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);
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
    }
}