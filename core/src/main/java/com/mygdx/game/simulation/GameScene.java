package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.engine.IOManager;
import com.mygdx.game.engine.MovementManager;
import com.mygdx.game.engine.RectangleEntity;
import com.mygdx.game.engine.Scene;
import com.mygdx.game.engine.SceneManager;
public class GameScene extends Scene {

    private SceneManager sceneManager;
    private IOManager ioManager;
    private MovementManager movementManager;
    private ObstacleFactory obstacleFactory;
    private Stage stage;

    private PlayerCharacter player;
    private Label questionLabel;
    private Label[] answerLabels = new Label[3];
    
    private Texture heartTexture; 
    
    private Array<Question> questionBank;
    private Array<RectangleEntity> currentWalls = new Array<>();
    
    private int currentQuestionIndex = 0;
    private int lives = 3;
    private Color currentBGColor = Color.BLACK;

    private final float WORLD_WIDTH = 800f;
    private final float WORLD_HEIGHT = 600f;

    public GameScene(String id, SceneManager sceneManager) {
        super(id); 
        this.sceneManager = sceneManager;
        
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.ioManager = new IOManager();
        this.movementManager = new MovementManager();
        this.obstacleFactory = new ObstacleFactory();
        
        this.heartTexture = new Texture("heart.png"); 
        
        createQuestionBank();
        setupUI();
        initializeInput();
        loadLevel(0);
    }

    private void createQuestionBank() {
        questionBank = new Array<>();
        
        questionBank.add(new Question(
            "What is the probability of flipping tails on a fair coin?", 
            new String[]{"1/2", "1/3", "1/4"}, 
            new Color(0.1f, 0.1f, 0.2f, 1f), 5f));
            
        questionBank.add(new Question(
            "What is the probability of rolling a 4 on a 6-sided die?", 
            new String[]{"1/6", "1/2", "1/4"}, 
            new Color(0.1f, 0.2f, 0.1f, 1f), 5f));
        
        questionBank.add(new Question(
            "What is the probability of drawing an Ace from a standard deck?", 
            new String[]{"1/13", "1/4", "1/52"}, 
            new Color(0.2f, 0.1f, 0.1f, 1f), 4f));
            
        questionBank.add(new Question(
            "What is the probability of flipping two heads in a row?", 
            new String[]{"1/4", "1/2", "3/4"}, 
            new Color(0.2f, 0.2f, 0.1f, 1f), 4f));
            
        questionBank.add(new Question(
            "What is the probability of rolling a sum of 7 with two dice?", 
            new String[]{"1/6", "1/12", "1/36"}, 
            new Color(0.1f, 0.1f, 0.1f, 1f), 3.5f));
    }

    private void setupUI() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        
        questionLabel = new Label("", style);
        questionLabel.setFontScale(1.5f); 
        questionLabel.setPosition(20, WORLD_HEIGHT - 40);
        stage.addActor(questionLabel);

        for (int i = 0; i < 3; i++) {
            answerLabels[i] = new Label("", style);
            answerLabels[i].setFontScale(1.5f); 
            stage.addActor(answerLabels[i]);
        }
    }

    private void loadLevel(int index) {
        if (index >= questionBank.size) {
            ResultScene result = (ResultScene) sceneManager.getScene("RESULT");
            if (result != null) result.setScore(questionBank.size, questionBank.size);
            
            sceneManager.setActiveScene("RESULT");
            resetGameData();
            return;
        }

        Question q = questionBank.get(index);
        currentBGColor = q.themeColor;
        questionLabel.setText("Q: " + q.text);
        
        float wallX = WORLD_WIDTH * 0.8f;
        float speedX = (wallX - 50f) / q.timeToReach;

        if (player == null) {
            player = new PlayerCharacter(1, new Vector2(50, WORLD_HEIGHT / 2), 25f, speedX);
            addEntity(player);
            movementManager.registerMovable(player);
        } else {
            player.getPosition().set(50, WORLD_HEIGHT / 2);
            player.getVelocity().set(speedX, 0);
            player.isLevelComplete = false; 
            player.hitWrongWall = false;
        }

        Array<String> shuffledAnswers = new Array<>(q.answers);
        shuffledAnswers.shuffle();
        int correctIndex = shuffledAnswers.indexOf(q.answers[0], false);

        float sectionH = WORLD_HEIGHT / 3f;
        
        answerLabels[0].setText(shuffledAnswers.get(0)); 
        answerLabels[0].setPosition(wallX - 80, (sectionH * 2) + (sectionH / 2));
        
        answerLabels[1].setText(shuffledAnswers.get(1)); 
        answerLabels[1].setPosition(wallX - 80, sectionH + (sectionH / 2));
        
        answerLabels[2].setText(shuffledAnswers.get(2)); 
        answerLabels[2].setPosition(wallX - 80, sectionH / 2);

        spawnObstacles(wallX, sectionH, correctIndex);
    }

    private void spawnObstacles(float x, float h, int correctPosition) {
        for (RectangleEntity wall : currentWalls) removeEntity(wall);
        currentWalls.clear(); 

        RectangleEntity topWall = (correctPosition == 0) ? 
            obstacleFactory.createCorrectWall(2, x, h * 2, 50, h) : 
            obstacleFactory.createWrongWall(2, x, h * 2, 50, h);
            
        RectangleEntity middleWall = (correctPosition == 1) ? 
            obstacleFactory.createCorrectWall(3, x, h, 50, h) : 
            obstacleFactory.createWrongWall(3, x, h, 50, h);
            
        RectangleEntity bottomWall = (correctPosition == 2) ? 
            obstacleFactory.createCorrectWall(4, x, 0, 50, h) : 
            obstacleFactory.createWrongWall(4, x, 0, 50, h);

        addEntity(topWall);
        addEntity(middleWall);
        addEntity(bottomWall);

        currentWalls.add(topWall);
        currentWalls.add(middleWall);
        currentWalls.add(bottomWall);
    }

    private void resetGameData() {
        currentQuestionIndex = 0;
        lives = 3;
        loadLevel(0); 
    }

    private void initializeInput() {
        ioManager.bindKeyContinuous(Input.Keys.UP, () -> { if (player != null) player.getVelocity().y = 300; });
        ioManager.bindKeyContinuous(Input.Keys.DOWN, () -> { if (player != null) player.getVelocity().y = -300; });
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            SettingsScene settings = (SettingsScene) sceneManager.getScene("SETTINGS");
            if (settings != null) settings.setPreviousScene("GAME");
            sceneManager.setActiveScene("SETTINGS");
            return;
        }

        ioManager.handleInput();

        if (player != null) {
            player.getVelocity().y *= 0.85f;
            
            if (player.hitWrongWall) {
                lives--;
                player.hitWrongWall = false; 
                
                if (lives <= 0) {
                    ResultScene result = (ResultScene) sceneManager.getScene("RESULT");
                    if (result != null) result.setScore(currentQuestionIndex, questionBank.size);
                    
                    sceneManager.setActiveScene("RESULT");
                    resetGameData();
                    return;
                }
            }
            
            if (player.isLevelComplete) {
                currentQuestionIndex++;
                loadLevel(currentQuestionIndex);
            }
        }
        
        super.update(deltaTime);
        movementManager.update(deltaTime);
        stage.act(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(currentBGColor.r, currentBGColor.g, currentBGColor.b, 1);
        
        stage.getViewport().apply();
        
        super.render(batch);

        batch.setProjectionMatrix(stage.getCamera().combined);
        for (int i = 0; i < lives; i++) {
            batch.draw(heartTexture, WORLD_WIDTH - 50 - (i * 40), WORLD_HEIGHT - 50, 30, 30);
        }

        if (batch.isDrawing()) batch.end();
        stage.draw();
        
        if (!batch.isDrawing()) batch.begin();
    }

    public void dispose() {
        if (heartTexture != null) {
            heartTexture.dispose();
        }
        if (player != null) {
            player.dispose();
        }
    }
}