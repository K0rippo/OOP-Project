package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class MenuScene extends Scene {

    private Stage stage;

    public MenuScene(String id, final ISceneNavigator sceneNavigator, IGameEngine engine, Texture buttonTexture) {
        super(id, engine);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont font = new BitmapFont();
        // Scale the font up slightly for better readability
        font.getData().setScale(1.5f);

        // 1. Create a base drawable from the passed texture
        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        // 2. Setup the TextButtonStyle with different states using .tint()
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        
        // Dark grey when idle, lighter on hover, almost black when clicked
        textButtonStyle.up = baseDrawable.tint(new Color(0.2f, 0.2f, 0.2f, 1f));     
        textButtonStyle.over = baseDrawable.tint(new Color(0.35f, 0.35f, 0.35f, 1f)); 
        textButtonStyle.down = baseDrawable.tint(new Color(0.1f, 0.1f, 0.1f, 1f));   

        // 3. Add a Title to complete the clean UI look
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("MAIN MENU", labelStyle);
        titleLabel.setFontScale(2.5f); // Make the title larger than the buttons

        // 4. Instantiate Buttons
        TextButton btnPlay = new TextButton("PLAY", textButtonStyle);
        TextButton btnSettings = new TextButton("SETTINGS", textButtonStyle);
        TextButton btnQuit = new TextButton("QUIT", textButtonStyle);

        // 5. Build the Table Layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        
        // Add Title, row(), then add Buttons
        mainTable.add(titleLabel).padBottom(70).row();
        mainTable.add(btnPlay).size(250, 60).padBottom(20).row();
        mainTable.add(btnSettings).size(250, 60).padBottom(20).row();
        mainTable.add(btnQuit).size(250, 60);

        stage.addActor(mainTable);

        // --- Listeners ---
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScene gameScene = (GameScene) sceneNavigator.getScene("GAME");
                if (gameScene != null) gameScene.requestRestart();
                sceneNavigator.goToScene("GAME");
            }
        });

        btnSettings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsScene settings = (SettingsScene) sceneNavigator.getScene("SETTINGS");
                if (settings != null) settings.setPreviousScene("MENU");
                sceneNavigator.goToScene("SETTINGS");
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }
    
    @Override
    public void hide() { Gdx.input.setInputProcessor(null); }
    
    @Override
    public void update(float deltaTime) { 
        stage.act(deltaTime); 
    }
    
    @Override
    public void render(SpriteBatch batch) {
        stage.getViewport().apply();
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}