package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

/**
 * SettingsScene - displays settings and mute toggle.
 * Updated to use ISceneNavigator and IGameEngine abstractions (DIP).
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

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.DARK_GRAY;

        btnMute = new TextButton("SOUND: UNMUTED", textButtonStyle);
        btnBack = new TextButton("BACK", textButtonStyle);
        btnExitMenu = new TextButton("EXIT TO MENU", textButtonStyle);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(btnMute).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnBack).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnExitMenu).size(200, 50);

        stage.addActor(mainTable);

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
        GameMaster.isMuted = !GameMaster.isMuted;
        updateButtonText();
    }

    private void updateButtonText() {
        if (GameMaster.isMuted) {
            btnMute.setText("SOUND: MUTED");
            btnMute.setColor(Color.RED);
        } else {
            btnMute.setText("SOUND: UNMUTED");
            btnMute.setColor(Color.DARK_GRAY);
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
        super.update(deltaTime);
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