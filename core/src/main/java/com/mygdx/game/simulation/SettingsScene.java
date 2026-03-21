package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.engine.ISettingsScene;
import com.mygdx.game.engine.ISceneNavigator;
import com.mygdx.game.engine.Scene;

public class SettingsScene extends Scene implements ISettingsScene {

    private Stage stage;
    private Table panelTable;
    
    private Label titleLabel;
    private Label musicLabel;
    private Label sfxLabel;
    private Slider musicSlider;
    private Slider sfxSlider;
    
    private TextButton btnControls;
    private TextButton btnBack;
    private TextButton btnExitMenu;
    
    private String previousSceneId = "MENU"; 
    private AudioManager audioManager;

    public SettingsScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture, final AudioManager audioManager) {
        super(id);
        this.audioManager = audioManager;
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        
        BitmapFont labelFont = new BitmapFont();
        labelFont.getData().setScale(1.2f);

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
        
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = createSliderBg(cyanBorder);
        sliderStyle.knob = createSliderKnob(coreBlue, cyanBorder);

        TextureRegionDrawable panelBackground = createPanelDrawable(cyanBorder);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        titleLabel = new Label("SYSTEM OPTIONS", titleStyle);
        titleLabel.setAlignment(Align.center);
        
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(labelFont, cyanBorder);
        musicLabel = new Label("MUSIC VOLUME", smallLabelStyle);
        sfxLabel = new Label("SFX VOLUME", smallLabelStyle);

        musicSlider = new Slider(0f, 1f, 0.05f, false, sliderStyle);
        musicSlider.setValue(GameMaster.getMusicVolume());
        
        sfxSlider = new Slider(0f, 1f, 0.05f, false, sliderStyle);
        sfxSlider.setValue(GameMaster.getSfxVolume());

        btnControls = new TextButton("CONTROLS: " + (GameMaster.isUseWASD() ? "WASD" : "ARROW KEYS"), cyanStyle);
        btnBack = new TextButton("RETURN TO MENU", yellowStyle);
        btnExitMenu = new TextButton("ABORT MISSION", yellowStyle);

        panelTable = new Table();
        panelTable.setBackground(panelBackground);
        panelTable.setSize(600, 700); 

        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        masterTable.add(panelTable).size(600, 700);

        stage.addActor(masterTable);
        
        rebuildLayout(false);

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameMaster.setMusicVolume(musicSlider.getValue());
                if (audioManager != null) {
                    audioManager.setMusicVolume(musicSlider.getValue());
                }
            }
        });
        
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameMaster.setSfxVolume(sfxSlider.getValue());
            }
        });

        btnControls.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager != null) audioManager.playMenuButtonSound();
                GameMaster.setUseWASD(!GameMaster.isUseWASD());
                btnControls.setText("CONTROLS: " + (GameMaster.isUseWASD() ? "WASD" : "ARROW KEYS"));
            }
        });

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager != null) audioManager.playMenuButtonSound();
                sceneNavigator.goToScene(previousSceneId);
            }
        });

        btnExitMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager != null) audioManager.playMenuButtonSound();
                sceneNavigator.goToScene("MENU");
            }
        });
    }
    
    private void rebuildLayout(boolean fromGame) {
        panelTable.clearChildren();
        panelTable.add(titleLabel).width(600).padTop(25).padBottom(40).row();
        
        if (fromGame) {
            panelTable.add(btnBack).size(350, 65).padBottom(30).row();
            
            panelTable.add(musicLabel).padBottom(5).row();
            panelTable.add(musicSlider).width(400).padBottom(20).row();
            
            panelTable.add(sfxLabel).padBottom(5).row();
            panelTable.add(sfxSlider).width(400).padBottom(30).row();
            
            panelTable.add(btnControls).size(350, 65).padBottom(20).row();
            panelTable.add(btnExitMenu).size(350, 65);
        } else {
            panelTable.add(musicLabel).padBottom(5).row();
            panelTable.add(musicSlider).width(400).padBottom(20).row();
            
            panelTable.add(sfxLabel).padBottom(5).row();
            panelTable.add(sfxSlider).width(400).padBottom(40).row();
            
            panelTable.add(btnControls).size(350, 65).padBottom(20).row();
            panelTable.add(btnBack).size(350, 65); 
        }
        
        panelTable.add().expandY().fillY();
    }

    public void setPreviousScene(String id) {
        this.previousSceneId = id;
        if (id.equals("MENU")) {
            btnBack.setText("RETURN TO MENU");
            btnExitMenu.setVisible(false);
            rebuildLayout(false); 
        } else {
            btnBack.setText("RESUME MISSION");
            btnExitMenu.setVisible(true);
            rebuildLayout(true); 
        }
    }

    private TextureRegionDrawable createSliderBg(Color borderColor) {
        int w = 400;
        int h = 24; 
        int r = h / 2;
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);
        p.setColor(new Color(0.02f, 0.1f, 0.25f, 1f));
        fillRoundedRect(p, 2, 2, w - 4, h - 4, r - 2);
        p.setColor(new Color(1f, 1f, 1f, 0.15f));
        p.fillRectangle(r, 2, w - 2 * r, (h - 4) / 2);

        Texture tex = new Texture(p);
        p.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    private TextureRegionDrawable createSliderKnob(Color coreColor, Color borderColor) {
        int w = 24;
        int h = 40;
        int r = w / 2;
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);
        p.setColor(coreColor);
        fillRoundedRect(p, 2, 2, w - 4, h - 4, r - 1);
        p.setColor(new Color(1f, 1f, 1f, 0.25f));
        p.fillRectangle(r / 2, 2, w - r, (h - 4) / 2);

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
        int w = 600;
        int h = 700;
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