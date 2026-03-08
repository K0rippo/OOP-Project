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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.engine.Engine;
import com.mygdx.game.engine.Scene;
import com.mygdx.game.engine.SceneManager;

public class ResultScene extends Scene {

    private Stage stage;
    private Label scoreLabel;

    public ResultScene(String id, final SceneManager sceneManager, Engine engine, Texture buttonTexture) {
        super(id, engine);
        this.stage = new Stage(new FitViewport(800, 600));

        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.YELLOW);
        
        Label titleLabel = new Label("QUIZ COMPLETE!", labelStyle);
        titleLabel.setFontScale(2.0f);

        scoreLabel = new Label("Score: 0 / 5", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        btnStyle.font = font;
        btnStyle.fontColor = Color.BLACK;

        TextButton btnRestart = new TextButton("RESTART GAME", btnStyle);
        TextButton btnMenu = new TextButton("MAIN MENU", btnStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.add(titleLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(40).row();
        table.add(btnRestart).size(200, 50).padBottom(10).row();
        table.add(btnMenu).size(200, 50);

        stage.addActor(table);

        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setActiveScene("GAME"); 
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setActiveScene("MENU");
            }
        });
    }

    public void setScore(int current, int max) {
        scoreLabel.setText("Score: " + current + " / " + max);
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