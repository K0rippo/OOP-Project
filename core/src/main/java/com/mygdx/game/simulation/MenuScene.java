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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.engine.IGameEngine;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class MenuScene extends Scene {

    private Stage stage;

    public MenuScene(String id, final ISceneNavigator sceneNavigator, IGameEngine engine, Texture buttonTexture) {
        super(id, engine);
        this.stage = new Stage(new FitViewport(800, 600));

        BitmapFont font = new BitmapFont();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.DARK_GRAY;

        TextButton btnPlay = new TextButton("PLAY", textButtonStyle);
        TextButton btnSettings = new TextButton("SETTINGS", textButtonStyle);
        TextButton btnQuit = new TextButton("QUIT", textButtonStyle);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(btnPlay).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnSettings).size(200, 50).padBottom(15);
        mainTable.row();
        mainTable.add(btnQuit).size(200, 50);

        stage.addActor(mainTable);

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
        //super.update(deltaTime); 
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