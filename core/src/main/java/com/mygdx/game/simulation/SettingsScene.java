package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class SettingsScene extends Scene {

    private Stage stage;
    private TextButton btnMute;
    private TextButton btnBack;
    private TextButton btnExitMenu;
    private String previousSceneId = "MENU"; 
    
    private TextButton.TextButtonStyle audioActiveStyle;
    private TextButton.TextButtonStyle audioMutedStyle;

    public SettingsScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture) {
        super(id);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3.5f);

        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Image bgImage = new Image(baseDrawable);
        bgImage.setColor(new Color(0.05f, 0.08f, 0.2f, 1f)); 
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Color darkBlueBg = new Color(0.02f, 0.1f, 0.25f, 0.9f);
        Color hoverBlueBg = new Color(0.1f, 0.25f, 0.45f, 0.9f);
        Color cyanBorder = new Color(0.1f, 0.7f, 1f, 1f);     
        Color yellowBorder = new Color(1f, 0.8f, 0.1f, 1f);   
        Color redBorder = new Color(1f, 0.2f, 0.2f, 1f); 

        audioActiveStyle = new TextButton.TextButtonStyle();
        audioActiveStyle.font = buttonFont;
        audioActiveStyle.fontColor = cyanBorder; 
        audioActiveStyle.up = createBorderedDrawable(darkBlueBg, cyanBorder);     
        audioActiveStyle.over = createBorderedDrawable(hoverBlueBg, cyanBorder); 
        audioActiveStyle.down = createBorderedDrawable(cyanBorder, Color.WHITE); 

        audioMutedStyle = new TextButton.TextButtonStyle();
        audioMutedStyle.font = buttonFont;
        audioMutedStyle.fontColor = redBorder; 
        audioMutedStyle.up = createBorderedDrawable(darkBlueBg, redBorder);     
        audioMutedStyle.over = createBorderedDrawable(hoverBlueBg, redBorder); 
        audioMutedStyle.down = createBorderedDrawable(redBorder, Color.WHITE); 

        TextButton.TextButtonStyle yellowStyle = new TextButton.TextButtonStyle();
        yellowStyle.font = buttonFont;
        yellowStyle.fontColor = yellowBorder; 
        yellowStyle.up = createBorderedDrawable(darkBlueBg, yellowBorder);     
        yellowStyle.over = createBorderedDrawable(hoverBlueBg, yellowBorder); 
        yellowStyle.down = createBorderedDrawable(yellowBorder, Color.WHITE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE); 
        Label titleLabel = new Label("SYSTEM OPTIONS", titleStyle);

        btnMute = new TextButton("AUDIO: ACTIVE", audioActiveStyle);
        btnBack = new TextButton("RESUME RUN", yellowStyle);
        btnExitMenu = new TextButton("ABORT RUN", yellowStyle);

        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        
        masterTable.add(titleLabel).padBottom(60).row();
        masterTable.add(btnMute).size(350, 70).padBottom(20).row();
        masterTable.add(btnBack).size(350, 70).padBottom(20).row();
        masterTable.add(btnExitMenu).size(350, 70);

        stage.addActor(masterTable);

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

    private TextureRegionDrawable createBorderedDrawable(Color bgColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(350, 70, Pixmap.Format.RGBA8888);
        pixmap.setColor(bgColor);
        pixmap.fill();
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(0, 0, 350, 70);
        pixmap.drawRectangle(1, 1, 348, 68);
        pixmap.drawRectangle(2, 2, 346, 66); 

        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    public void setPreviousScene(String id) {
        this.previousSceneId = id;
        if (id.equals("MENU")) {
            btnBack.setText("RETURN TO MENU");
            btnExitMenu.setVisible(false); 
        } else {
            btnBack.setText("RESUME RUN");
            btnExitMenu.setVisible(true); 
        }
    }

    private void toggleMute() {
        GameMaster.setMuted(!GameMaster.isMuted());
        updateButtonText();
    }

    private void updateButtonText() {
        if (GameMaster.isMuted()) {
            btnMute.setText("AUDIO: MUTED");
            btnMute.setStyle(audioMutedStyle); 
        } else {
            btnMute.setText("AUDIO: ACTIVE");
            btnMute.setStyle(audioActiveStyle); 
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
        updateButtonText(); 
    }

    @Override
    public void hide() { 
        super.hide();
        Gdx.input.setInputProcessor(null); 
    }

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;
        stage.act(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive()) return;
        stage.getViewport().apply();
        if (batch.isDrawing()) batch.end();
        stage.draw();
        if (!batch.isDrawing()) batch.begin();
    }
}