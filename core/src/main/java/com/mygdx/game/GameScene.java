package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScene extends Scene {

    private Stage stage;
    private SceneManager sceneManager;
    private IOManager ioManager; // Added reference
    
    // Class-level references so we can control them in update()
    private Entity ball; 
    private Entity trampoline;
    
    private boolean isPaused = false;
    
    // ui elements
    private Table pauseMenuTable;
    private Image dimOverlay;
    private Table topTable; 

    // Constructor updated to accept IOManager
    public GameScene(String id, final SceneManager sceneManager, IOManager ioManager) {
        super(id);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        
        initializeEntities();
        initializeUI();
        setupInputLogic(); // Bind keys to actions
    }

    private void setupInputLogic() {
        if (ball == null || trampoline == null) return;
        
        float ballSpeed = 200f;
        float trampolineSpeed = 250f;

        // --- BALL CONTROLS (WASD) ---
        ioManager.registerAction(InputAction.MOVE_UP, () -> ball.getVelocity().y = ballSpeed);
        ioManager.registerAction(InputAction.MOVE_DOWN, () -> ball.getVelocity().y = -ballSpeed);
        ioManager.registerAction(InputAction.MOVE_LEFT, () -> ball.getVelocity().x = -ballSpeed);
        ioManager.registerAction(InputAction.MOVE_RIGHT, () -> ball.getVelocity().x = ballSpeed);
        
        // --- TRAMPOLINE CONTROLS (ARROWS) ---
        ioManager.registerAction(InputAction.TRAMPOLINE_LEFT, () -> trampoline.getVelocity().x = -trampolineSpeed);
        ioManager.registerAction(InputAction.TRAMPOLINE_RIGHT, () -> trampoline.getVelocity().x = trampolineSpeed);
        
        // --- PAUSE ---
        ioManager.registerAction(InputAction.PAUSE_GAME, () -> {
            if (!isPaused) togglePause(true);
            else togglePause(false);
        });
    }

    private void initializeEntities() {
        // 1. Create Ball and save to class variable
        ball = new Circle(1, "Ball", new Vector2(200, 200), 15, Color.BROWN);
        addEntity(ball); 

        // 2. Create Trampoline and save to class variable
        trampoline = new RectangleEntity(2, "Trampoline", new Vector2(50, 50), 150, 20, Color.GREEN);
        addEntity(trampoline);

        // 3. Create Walls/Coins
        Entity wall = new RectangleEntity(3, "Wall", new Vector2(600, 50), 40, 200, Color.BLACK);
        addEntity(wall);
        
        for(int i=0; i<2; i++) {
             Entity coin = new Circle(100 + i, "Coin" + i, new Vector2(300 + i * 40, 250), 5, Color.GOLD);
             addEntity(coin);
        }
    }
    
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
        // MULTIPLEXER: This is critical. It lets us click buttons AND press keys.
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);      // UI
        multiplexer.addProcessor(ioManager);  // Game
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void update(float deltaTime) {
        if (!isPaused) {
            // STOP ON RELEASE: Reset velocity every frame. 
            // If a key is still held, IOManager will set it back to speed immediately after this.
            if (ball != null) ball.setVelocity(new Vector2(0,0));
            if (trampoline != null) trampoline.setVelocity(new Vector2(0,0));
            
            ioManager.handleInput(); 
            super.update(deltaTime); 
        }
        
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch, EntityManager entityManager) {
        super.render(batch, entityManager);
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}