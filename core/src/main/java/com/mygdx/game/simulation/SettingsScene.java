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

/**
 * SettingsScene - displays settings and mute toggle.
 * Updated with clean UI hover effects.
 */
public class SettingsScene extends Scene {

    private Stage stage;
    private TextButton btnMute;
    private TextButton btnBack;
    private TextButton btnExitMenu;
    private String previousSceneId = "MENU"; 

    public SettingsScene(String id, final ISceneNavigator sceneNavigator, IGameEngine engine, Texture buttonTexture) {
        super(id, engine);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f); // Match the MenuScene scale

        // 1. Create base drawable and set up the tinted style
        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        
        // Idle (Dark Grey), Hover (Lighter Grey), Clicked (Almost Black)
        textButtonStyle.up = baseDrawable.tint(new Color(0.2f, 0.2f, 0.2f, 1f));     
        textButtonStyle.over = baseDrawable.tint(new Color(0.35f, 0.35f, 0.35f, 1f)); 
        textButtonStyle.down = baseDrawable.tint(new Color(0.1f, 0.1f, 0.1f, 1f));   

        // 2. Add Title Label
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("SETTINGS", labelStyle);
        titleLabel.setFontScale(2.5f);

        // 3. Instantiate Buttons
        btnMute = new TextButton("SOUND: UNMUTED", textButtonStyle);
        btnBack = new TextButton("BACK", textButtonStyle);
        btnExitMenu = new TextButton("EXIT TO MENU", textButtonStyle);

        // 4. Build the Table Layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        
        mainTable.add(titleLabel).padBottom(70).row();
        mainTable.add(btnMute).size(250, 60).padBottom(20).row();
        mainTable.add(btnBack).size(250, 60).padBottom(20).row();
        mainTable.add(btnExitMenu).size(250, 60);

        stage.addActor(mainTable);

        // --- Listeners ---
        btnMute.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMute();
            }
        });

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneNavigator.goToScene(previousSceneId);
            }
        });

        btnExitMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneNavigator.goToScene("MENU");
            }
        });
    }

    public void setPreviousScene(String id) {
        this.previousSceneId = id;
        if (id.equals("MENU")) {
            btnBack.setText("BACK TO MENU");
            btnExitMenu.setVisible(false);
        } else {
            btnBack.setText("BACK TO GAME");
            btnExitMenu.setVisible(true);
        }
    }

    private void toggleMute() {
        GameMaster.setMuted(!GameMaster.isMuted());
        updateButtonText();
    }

    private void updateButtonText() {
        if (GameMaster.isMuted()) {
            btnMute.setText("SOUND: MUTED");
            btnMute.setColor(Color.RED); // Tints the button red
        } else {
            btnMute.setText("SOUND: UNMUTED");
            btnMute.setColor(Color.WHITE); // Resets to default style colors
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateButtonText();
    }

    @Override
    public void hide() { Gdx.input.setInputProcessor(null); }

    @Override
    public void update(float deltaTime) {
        // Do NOT call super.update() — that would tick the shared engine and
        // advance game physics while the settings overlay is open.
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