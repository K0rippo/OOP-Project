package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music; // Import added
import com.badlogic.gdx.audio.Sound; // Import added
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
import com.mygdx.game.engine.MovementManager;
import com.mygdx.game.engine.iMovable;


public class GameScene extends Scene {

    private Stage stage;
    private SceneManager sceneManager;
    private boolean isPaused = false;
    
    private Table pauseMenuTable;
    private Image dimOverlay;
    private Table topTable;

    private IOManager ioManager; 
    private Trampoline trampoline;
    private Ball ball; 
    private RectangleEntity wall;
    
    private float coinTimer = 0f;
    private int coinCount = 0;
    private int jumpCount = 0;
    
    private MovementManager movementManager = new MovementManager();


    // --- AUDIO VARIABLES ---
    private Music backgroundMusic;
    private Sound coinSound;

    public GameScene(String id, final SceneManager sceneManager) {
        super(id);
        this.sceneManager = sceneManager;
        this.ioManager = new IOManager();
        
        // 1. Load Audio
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Game Music.mp3"));
            coinSound = Gdx.audio.newSound(Gdx.files.internal("Coin.wav"));
            
            // 2. Configure Music
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.3f); // 30% volume
        } catch (Exception e) {
            System.out.println("Audio files not found! Check assets folder.");
            e.printStackTrace();
        }

        initializeEntities();
        initializeInput(); 
        initializeUI();
    }

    private void initializeEntities() {
        this.ball = new Ball(1, new Vector2(200, 400), 10, Color.BROWN);
        ball.setVelocity(new Vector2(150, 0)); 
        addEntity(ball);
        movementManager.registerMovable((iMovable) ball);

        this.trampoline = new Trampoline(2, new Vector2(Gdx.graphics.getWidth() / 2f - 50f, 50f), 100f, 15f, Color.GREEN);
       	trampoline.setSpeed(300f);
        addEntity(trampoline);
        movementManager.registerMovable((iMovable) trampoline);
       
        this.wall = new RectangleEntity(3, "Wall", new Vector2(600, 0), 25, 250, Color.BLACK);
        addEntity(wall);
        
        trampoline.setPatrolBounds(0f, wall.getPosition().x);
    }

    private void initializeInput() {
        ioManager.bindKeyJustPressed(Input.Keys.W, new Runnable() {
            @Override
            public void run() {
                if (ball != null) {
                	if (ball.getJumpCount() < 1) { // If hasn't jumped yet
                	    ball.getVelocity().y = 450; // Jump up
                	    ball.setJumpCount(1);       // Mark as jumped
                	}
                }
            }
        });


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
    }

    @Override
    public void update(float deltaTime) {
        // 3. Audio Control (Mute logic)
        if (backgroundMusic != null) {
            if (GameMaster.isMuted && backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
            } else if (!GameMaster.isMuted && !backgroundMusic.isPlaying() && !isPaused) {
                backgroundMusic.play();
            }
        }

        if (!isPaused) {
            if (ball != null) ball.getVelocity().x *= 0.95f; 
            
            float vyBefore = (ball != null) ? ball.getVelocity().y : 0;

            ioManager.handleInput();
            updateCoinSpawner(deltaTime);
            super.update(deltaTime); 
            movementManager.update(deltaTime);

            if (ball != null) {
                float vyAfter = ball.getVelocity().y;
                if (vyBefore < 0 && vyAfter >= 0) {
                    jumpCount = 0;
                }
                if (Math.abs(vyAfter) < 10f && ball.getPosition().y < 15f) {
                    jumpCount = 0;
                }
            }
        }
        stage.act(deltaTime);
    }

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
        
        // 4. Pass the loaded 'coinSound' to the new Coin
        Coin coin = new Coin(100 + coinCount, new Vector2(randomX, startY), 6, coinSound);
        addEntity(coin);
        movementManager.registerMovable(coin);
        coinCount++;
    }

    // --- UI SECTION ---
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
        
        // PAUSE MUSIC LOGIC
        if (backgroundMusic != null) {
            if (paused) backgroundMusic.pause();
            else if (!GameMaster.isMuted) backgroundMusic.play();
        }

        topTable.setVisible(!paused);
    }
    @Override
    public void show() {
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stage);      
        multiplexer.addProcessor(ioManager);  
        Gdx.input.setInputProcessor(multiplexer);
        
        // RESUME MUSIC ON SHOW
        if (backgroundMusic != null && !GameMaster.isMuted) {
            backgroundMusic.play();
        }
    }
    @Override
    public void hide() { 
        Gdx.input.setInputProcessor(null);
        if (backgroundMusic != null) backgroundMusic.pause();
    }
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}