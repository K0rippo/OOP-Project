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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class InstructionsScene extends Scene {

    private Stage stage;
    private AudioManager audioManager;
    private Label instructionsLabel;
    
    private Label keyUp;
    private Label keyLeft;
    private Label keyDown;
    private Label keyRight;
    private Label keySpacebar;

    public InstructionsScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture, final AudioManager audioManager) {
        super(id);
        this.audioManager = audioManager;
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);
        
        BitmapFont textFont = new BitmapFont();
        textFont.getData().setScale(1.3f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        
        BitmapFont keyFont = new BitmapFont();
        keyFont.getData().setScale(1.6f);

        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Image bgImage = new Image(baseDrawable);
        bgImage.setColor(new Color(0.05f, 0.08f, 0.15f, 1f));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Color cyanBorder   = new Color(0.0f, 0.8f, 1.0f, 1f);
        Color yellowBorder = new Color(1.0f, 0.8f, 0.1f, 1f);
        Color coreBlue     = new Color(0.15f, 0.35f, 0.65f, 1f);
        Color hoverBlue    = new Color(0.25f, 0.50f, 0.85f, 1f);

        TextButton.TextButtonStyle cyanStyle = new TextButton.TextButtonStyle();
        cyanStyle.font = buttonFont;
        cyanStyle.fontColor = Color.WHITE;
        cyanStyle.up = createPillButtonDrawable(coreBlue, cyanBorder);
        cyanStyle.over = createPillButtonDrawable(hoverBlue, cyanBorder);
        cyanStyle.down = createPillButtonDrawable(cyanBorder, Color.WHITE);

        TextButton.TextButtonStyle yellowStyle = new TextButton.TextButtonStyle();
        yellowStyle.font = buttonFont;
        yellowStyle.fontColor = Color.WHITE;
        yellowStyle.up = createPillButtonDrawable(coreBlue, yellowBorder);
        yellowStyle.over = createPillButtonDrawable(hoverBlue, yellowBorder);
        yellowStyle.down = createPillButtonDrawable(yellowBorder, Color.WHITE);

        TextureRegionDrawable panelBackground = createPanelDrawable(cyanBorder);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("HOW TO PLAY", titleStyle);
        titleLabel.setAlignment(Align.center);

        Label.LabelStyle textStyle = new Label.LabelStyle(textFont, Color.WHITE);
        instructionsLabel = new Label("", textStyle);
        instructionsLabel.setAlignment(Align.center);
        
        Label.LabelStyle keyStyle = new Label.LabelStyle(keyFont, Color.WHITE);
        keyStyle.background = createKeycapDrawable(coreBlue, cyanBorder, 60, 60);
        
        keyUp = new Label("", keyStyle);
        keyUp.setAlignment(Align.center);
        
        keyLeft = new Label("", keyStyle);
        keyLeft.setAlignment(Align.center);
        
        keyDown = new Label("", keyStyle);
        keyDown.setAlignment(Align.center);
        
        keyRight = new Label("", keyStyle);
        keyRight.setAlignment(Align.center);
        
        Table keysTable = new Table();
        keysTable.add(keyUp).size(60, 60).padBottom(5).colspan(3).align(Align.center).row();
        keysTable.add(keyLeft).size(60, 60).padRight(5);
        keysTable.add(keyDown).size(60, 60).padRight(5);
        keysTable.add(keyRight).size(60, 60);

        Table moveRow = new Table();
        moveRow.add(keysTable).padRight(20);
        Label moveTextLabel = new Label("to move your ship.", textStyle);
        moveRow.add(moveTextLabel).align(Align.left);

        Label.LabelStyle spacebarStyle = new Label.LabelStyle(keyFont, Color.WHITE);
        spacebarStyle.background = createKeycapDrawable(coreBlue, cyanBorder, 200, 60);
        
        keySpacebar = new Label("SPACE", spacebarStyle);
        keySpacebar.setAlignment(Align.center);

        Table shootRow = new Table();
        shootRow.add(keySpacebar).size(200, 60).padRight(20);
        Label shootTextLabel = new Label("to shoot barriers.", textStyle);
        shootRow.add(shootTextLabel).align(Align.left);

        TextButton btnStart = new TextButton("START MISSION", cyanStyle); 
        TextButton btnBack = new TextButton("RETURN TO MENU", yellowStyle);

        Table panelTable = new Table();
        panelTable.setBackground(panelBackground);
        panelTable.setSize(650, 600);

        panelTable.add(titleLabel).width(650).padTop(25).padBottom(20).row();
        panelTable.add(moveRow).padBottom(15).row(); 
        panelTable.add(shootRow).padBottom(25).row(); 
        panelTable.add(instructionsLabel).padBottom(35).row();
        panelTable.add(btnStart).size(350, 65).padBottom(15).row();
        panelTable.add(btnBack).size(350, 65);
        panelTable.add().expandY().fillY();

        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        masterTable.add(panelTable).size(650, 600);

        stage.addActor(masterTable);

        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager != null) audioManager.playMenuButtonSound();
                GameScene gameScene = (GameScene) sceneNavigator.getScene("GAME");
                if (gameScene != null) gameScene.requestRestart();
                sceneNavigator.goToScene("GAME");
            }
        });

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager != null) audioManager.playMenuButtonSound();
                sceneNavigator.goToScene("MENU");
            }
        });
    }

    private TextureRegionDrawable createKeycapDrawable(Color coreColor, Color borderColor, int width, int height) {
        int r = 10;
        Pixmap p = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, width, height, r);
        p.setColor(new Color(0.02f, 0.1f, 0.25f, 1f));
        fillRoundedRect(p, 2, 2, width - 4, height - 4, r - 2);
        p.setColor(coreColor);
        fillRoundedRect(p, 4, 4, width - 8, height - 8, r - 4);
        p.setColor(new Color(1f, 1f, 1f, 0.15f));
        p.fillRectangle(r, 4, width - 2 * r, (height - 8) / 2);

        Texture tex = new Texture(p);
        p.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    private TextureRegionDrawable createPillButtonDrawable(Color coreColor, Color borderColor) {
        int w = 350;
        int h = 65;
        int r = h / 2;
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);
        p.setColor(new Color(0.02f, 0.1f, 0.25f, 1f));
        fillRoundedRect(p, 3, 3, w - 6, h - 6, r - 3);
        p.setColor(coreColor);
        fillRoundedRect(p, 6, 6, w - 12, h - 12, r - 6);
        p.setColor(new Color(1f, 1f, 1f, 0.15f));
        p.fillRectangle(r, 6, w - 2 * r, (h - 12) / 2);

        Texture tex = new Texture(p);
        p.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    private TextureRegionDrawable createPanelDrawable(Color borderColor) {
        int w = 650; 
        int h = 600;
        int r = 20;
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);
        p.setColor(new Color(0.02f, 0.1f, 0.25f, 0.95f));
        fillRoundedRect(p, 5, 5, w - 10, h - 10, r - 5);
        p.setColor(new Color(0.08f, 0.18f, 0.38f, 1f));
        fillRoundedRect(p, 5, 5, w - 10, 80, r - 5);
        p.fillRectangle(5, 25, w - 10, 60);
        p.setColor(new Color(0.03f, 0.1f, 0.25f, 1f));
        for (int y = 15; y < 75; y += 12) {
            p.fillRectangle(15, y, w - 30, 4);
        }
        p.setColor(new Color(0.0f, 0.8f, 1.0f, 0.8f));
        p.fillCircle(25, 45, 6);
        p.fillCircle(w - 25, 45, 6);
        p.setColor(new Color(1f, 1f, 1f, 0.15f));
        p.fillRectangle(15, 8, w - 30, 5);
        p.setColor(borderColor);
        p.fillRectangle(5, 85, w - 10, 4);

        Texture tex = new Texture(p);
        p.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    private void fillRoundedRect(Pixmap p, int x, int y, int width, int height, int radius) {
        p.fillRectangle(x + radius, y, width - 2 * radius, height);
        p.fillRectangle(x, y + radius, width, height - 2 * radius);
        p.fillCircle(x + radius, y + radius, radius);
        p.fillCircle(x + width - radius, y + radius, radius);
        p.fillCircle(x + radius, y + height - radius, radius);
        p.fillCircle(x + width - radius, y + height - radius, radius);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() { 
        super.show();
        Gdx.input.setInputProcessor(stage);
        
        if (audioManager != null) {
            audioManager.playMenuMusic();
        }
        
        if (GameMaster.isUseWASD()) {
            keyUp.setText("W");
            keyLeft.setText("A");
            keyDown.setText("S");
            keyRight.setText("D");
        } else {
            keyUp.setText("^"); 
            keyLeft.setText("<");
            keyDown.setText("v"); 
            keyRight.setText(">");
        }
            
        instructionsLabel.setText("Read the question, destroy the correct\nbarrier, and fly through the gate!");
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