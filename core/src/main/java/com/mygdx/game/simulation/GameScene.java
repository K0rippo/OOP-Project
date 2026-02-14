package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.IOManager;
import com.mygdx.game.engine.Scene;
import com.mygdx.game.engine.SceneManager;
import com.mygdx.game.engine.MovableEntity;

public class GameScene extends Scene {

    private Stage stage;
    private SceneManager sceneManager;
    private boolean isPaused = false;
    
    private Table pauseMenuTable;
    private Image dimOverlay;
    private Table topTable;

    private IOManager ioManager; 
    private MovableEntity trampoline;
    private MovableEntity ball;         
    
    private float coinTimer = 0f;
    private int coinCount = 0;

    public GameScene(String id, final SceneManager sceneManager) {
        super(id);
        this.sceneManager = sceneManager;
        this.ioManager = new IOManager();
        
        initializeEntities();
        initializeInput(); 
        initializeUI();
    }

    private void initializeEntities() {
        // --- Entities remain unchanged ---
        this.ball = new Ball(1, new Vector2(200, 400), 15, Color.BROWN);
        ball.setVelocity(new Vector2(150, 0)); 
        addEntity(ball);

        this.trampoline = new RectangleEntity(2, "Trampoline", new Vector2(Gdx.graphics.getWidth() / 2 - 75, 50), 150, 20, Color.GREEN);
        addEntity(trampoline);

        Entity wall = new RectangleEntity(3, "Wall", new Vector2(600, 0), 40, 400, Color.BLACK);
        addEntity(wall);
    }

    private void initializeInput() {
        // --- UPDATED INPUT SECTION ---
        // We now bind keys directly to logic using the Generic IOManager.
        // No "InputAction" enum is used.

        // 1. One-Shot Actions (Jump / Smash) - Happens once per press
        ioManager.bindKeyJustPressed(Input.Keys.W, new Runnable() {
            @Override
            public void run() {
                if (ball != null) ball.getVelocity().y = 450;
            }
        });

        ioManager.bindKeyJustPressed(Input.Keys.S, new Runnable() {
            @Override
            public void run() {
                if (ball != null) ball.getVelocity().y = -600;
            }
        });

        // 2. Continuous Actions (Movement) - Happens while holding key
        // This replaces the old handleInputPolling() method
        
        // Ball Left/Right
        ioManager.bindKeyContinuous(Input.Keys.A, new Runnable() {
            @Override
            public void run() {
                if (ball != null) ball.getVelocity().x = -300;
            }
        });

        ioManager.bindKeyContinuous(Input.Keys.D, new Runnable() {
            @Override
            public void run() {
                if (ball != null) ball.getVelocity().x = 300;
            }
        });

        // Trampoline Left/Right
        if (trampoline != null) {
            ioManager.bindKeyContinuous(Input.Keys.LEFT, new Runnable() {
                @Override
                public void run() {
                    trampoline.getVelocity().x = -400;
                }
            });

            ioManager.bindKeyContinuous(Input.Keys.RIGHT, new Runnable() {
                @Override
                public void run() {
                    trampoline.getVelocity().x = 400;
                }
            });
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isPaused) {
            
            // 1. Friction & Reset (Unchanged logic)
            // TRAMPOLINE: Stops instantly when no key is pressed
            if (trampoline != null) trampoline.getVelocity().x = 0;
            
            // BALL: Does NOT stop instantly (Friction).
            if (ball != null) ball.getVelocity().x *= 0.95f; 

            // 2. Handle Input
            // The IOManager now handles both JustPressed and Continuous inputs here.
            // If keys are held (A/D/Arrows), they will override the friction above.
            ioManager.handleInput();

            // 3. Logic (Unchanged)
            updateCoinSpawner(deltaTime);
            super.update(deltaTime);
        }
        stage.act(deltaTime);
    }

    // REMOVED: handleInputPolling() - Logic moved to initializeInput / ioManager.

    private void updateCoinSpawner(float deltaTime) {
        coinTimer += deltaTime;
        if (coinTimer >= 5.0f) {
            spawnCoin();
            coinTimer = 0f;
        }
    }

    private void spawnCoin() {
        float randomX = MathUtils.random(50, 550);
        float startY = Gdx.graphics.getHeight() + 20;
        
        Entity coin = new Coin(100 + coinCount, new Vector2(randomX, startY), 10);
        addEntity(coin);
        coinCount++;
    }

    // --- UI SECTION (Unchanged) ---
    private void initializeUI() {
        stage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        pixmap.dispose();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(whiteTexture));
        style.fontColor = Color.BLACK;
        Pixmap dimPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        dimPixmap.setColor(0, 0, 0, 0.7f);
        dimPixmap.fill();
        dimOverlay = new Image(new Texture(dimPixmap));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false);
        dimPixmap.dispose();
        TextButton btnPause = new TextButton("||", style); 
        btnPause.setColor(Color.ORANGE);
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().right();
        topTable.add(btnPause).size(50, 50).pad(10);
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.center();
        pauseMenuTable.setVisible(false);
        TextButton btnContinue = new TextButton("CONTINUE", style);
        btnContinue.setColor(Color.GRAY);
        TextButton btnSettings = new TextButton("SETTINGS", style);
        btnSettings.setColor(Color.GRAY);
        TextButton btnLeave = new TextButton("LEAVE", style);
        btnLeave.setColor(Color.RED);
        pauseMenuTable.add(btnContinue).size(200, 50).padBottom(15);
        pauseMenuTable.row();
        pauseMenuTable.add(btnSettings).size(200, 50).padBottom(15);
        pauseMenuTable.row();
        pauseMenuTable.add(btnLeave).size(200, 50);
        stage.addActor(dimOverlay);
        stage.addActor(topTable);
        stage.addActor(pauseMenuTable);
        btnPause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isPaused) togglePause(true);
            }
        });
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(false);
            }
        });
        btnSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsScene settings = (SettingsScene) sceneManager.getScene("SETTINGS");
                if (settings != null) {
                    settings.setPreviousScene("GAME");
                    sceneManager.setActiveScene("SETTINGS");
                }
            }
        });
        btnLeave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(false);
                sceneManager.setActiveScene("MENU");
            }
        });
    }
    private void togglePause(boolean paused) {
        isPaused = paused;
        pauseMenuTable.setVisible(paused);
        dimOverlay.setVisible(paused);
        topTable.setVisible(!paused);
    }
    @Override
    public void show() {
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stage);      
        multiplexer.addProcessor(ioManager);  
        Gdx.input.setInputProcessor(multiplexer);
    }
    @Override
    public void hide() { Gdx.input.setInputProcessor(null); }
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}