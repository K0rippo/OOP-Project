package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScene extends Scene {

    private Stage stage;
    private SceneManager sceneManager;
    
    // Assets
    private BitmapFont font;
    private Texture buttonTexture;

    public MenuScene(String id, final SceneManager sceneManager) {
        super(id);
        this.sceneManager = sceneManager;
        stage = new Stage(new ScreenViewport());

        // Setup Style
        font = new BitmapFont();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        buttonTexture = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        style.fontColor = Color.WHITE;

        // Setup Layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Create Buttons
        TextButton btnPlay = new TextButton("PLAY", style);
        btnPlay.setColor(Color.GRAY);
        
        TextButton btnSettings = new TextButton("SETTINGS", style);
        btnSettings.setColor(Color.GRAY);

        TextButton btnQuit = new TextButton("QUIT", style);
        btnQuit.setColor(Color.GRAY);

        // Add to Table (Vertical Stack)
        mainTable.center();
        mainTable.add(btnPlay).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnSettings).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnQuit).size(200, 50);

        // Listeners
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setActiveScene("GAME");
            }
        });

        btnSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setActiveScene("SETTINGS");
            }
        });

        btnQuit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
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
        super.update(deltaTime);
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}