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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScene extends Scene {

    private Stage stage;
    private SceneManager sceneManager;
    private TextButton btnMute;
    
    // default to MENU, but can be changed to GAME
    private String previousSceneId = "MENU"; 

    public SettingsScene(String id, final SceneManager sceneManager) {
        super(id);
        this.sceneManager = sceneManager;
        stage = new Stage(new ScreenViewport());

        // styling
        BitmapFont font = new BitmapFont();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(texture));
        style.fontColor = Color.WHITE;
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        // layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label title = new Label("SETTINGS", labelStyle);
        title.setFontScale(1.5f);

        btnMute = new TextButton("SOUND: UNMUTED", style);
        btnMute.setColor(Color.DARK_GRAY);

        TextButton btnBack = new TextButton("BACK", style);
        btnBack.setColor(Color.GRAY);

        mainTable.top().padTop(50);
        mainTable.add(title).padBottom(50);
        mainTable.row();
        mainTable.add(btnMute).size(250, 50).padBottom(20);
        mainTable.row();
        mainTable.add(btnBack).size(200, 50);

        // listeners
        btnMute.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMute();
            }
        });

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Go back to wherever we came from (Menu or Game)
                sceneManager.setActiveScene(previousSceneId);
            }
        });
    }

    public void setPreviousScene(String id) {
        this.previousSceneId = id;
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateButtonText();
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
    public void render(SpriteBatch batch, EntityManager entityManager) {
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}