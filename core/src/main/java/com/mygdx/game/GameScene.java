package com.mygdx.game;

import com.badlogic.gdx.Gdx;
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
    private boolean isPaused = false;
    
    // ui elements
    private Table pauseMenuTable;
    private Image dimOverlay;
    private Table topTable; // to hold the pause button

    public GameScene(String id, final SceneManager sceneManager) {
        super(id);
        this.sceneManager = sceneManager;
        initializeEntities();
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());

        // styling
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

        // create transparent black background
        Pixmap dimPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        dimPixmap.setColor(0, 0, 0, 0.7f);
        dimPixmap.fill();
        dimOverlay = new Image(new Texture(dimPixmap));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false); // hidden by default
        dimPixmap.dispose();

        // create top-right pause button
        TextButton btnPause = new TextButton("||", style); 
        btnPause.setColor(Color.ORANGE);
        
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().right();
        topTable.add(btnPause).size(50, 50).pad(10);

        // create pause menu
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.center();
        pauseMenuTable.setVisible(false); // hidden by default

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

        // add everything to stage
        stage.addActor(dimOverlay); // background first
        stage.addActor(topTable);   // top right button
        stage.addActor(pauseMenuTable); // pause menu on top

        // listeners
        
        // pause button logic
        btnPause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isPaused) {
                    togglePause(true);
                }
            }
        });

        // continue button logic
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(false);
            }
        });

        // settings button logic
        btnSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // find settings scene
                SettingsScene settings = (SettingsScene) sceneManager.getScene("SETTINGS");
                if (settings != null) {
                    // come back to game scene, not to main menu
                    settings.setPreviousScene("GAME");
                    sceneManager.setActiveScene("SETTINGS");
                }
            }
        });

        // leave button logic
        btnLeave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause(false); // reset pause state
                sceneManager.setActiveScene("MENU");
            }
        });
    }

    private void togglePause(boolean paused) {
        isPaused = paused;
        pauseMenuTable.setVisible(paused);
        dimOverlay.setVisible(paused);
        topTable.setVisible(!paused); // hide the top pause button when menu is open
    }

    private void initializeEntities() {
        Entity ball = new Circle(1, "Ball", new Vector2(200, 200), 15, Color.BROWN);
        addEntity(ball);

        Entity trampoline = new RectangleEntity(2, "Trampoline", new Vector2(50, 50), 150, 20, Color.GREEN);
        addEntity(trampoline);

        Entity wall = new RectangleEntity(3, "Wall", new Vector2(600, 50), 40, 200, Color.BLACK);
        addEntity(wall);
        
        for(int i=0; i<2; i++) {
             Entity coin = new Circle(100 + i, "Coin" + i, new Vector2(300 + i * 40, 250), 5, Color.GOLD);
             addEntity(coin);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void update(float deltaTime) {
        // update only game entities if not paused
        if (!isPaused) {
            super.update(deltaTime);
        }
        
        // update the ui
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch, EntityManager entityManager) {
        // render game entities (background)
        super.render(batch, entityManager);

        // render ui (pause menu + overlay)
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}