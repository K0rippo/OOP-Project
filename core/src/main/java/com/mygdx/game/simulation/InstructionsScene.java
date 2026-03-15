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

    public InstructionsScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture) {
        super(id);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);
        
        BitmapFont textFont = new BitmapFont();
        textFont.getData().setScale(1.5f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Image bgImage = new Image(baseDrawable);
        bgImage.setColor(new Color(0.05f, 0.08f, 0.15f, 1f)); 
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Color cyanBorder  = new Color(0.0f, 0.8f, 1.0f, 1f);     
        Color yellowBorder = new Color(1.0f, 0.8f, 0.1f, 1f); 
        Color coreBlue    = new Color(0.15f, 0.35f, 0.65f, 1f); 
        Color hoverBlue   = new Color(0.25f, 0.50f, 0.85f, 1f); 

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
        String instructions = "UP / DOWN ARROWS to move.\n\n" +
                              "SPACEBAR to shoot barriers.\n\n" +
                              "Read the question, destroy the correct\n" +
                              "barrier, and fly through the gate!";
        Label instructionsLabel = new Label(instructions, textStyle);
        instructionsLabel.setAlignment(Align.center);

        TextButton btnStart = new TextButton("START RUN", cyanStyle);
        TextButton btnBack = new TextButton("RETURN TO MENU", yellowStyle);

        Table panelTable = new Table();
        panelTable.setBackground(panelBackground);
        panelTable.setSize(600, 600); 
        
        panelTable.add(titleLabel).width(600).padTop(25).padBottom(40).row();
        panelTable.add(instructionsLabel).padBottom(50).row();
        panelTable.add(btnStart).size(350, 65).padBottom(20).row();
        panelTable.add(btnBack).size(350, 65);
        panelTable.add().expandY().fillY(); 

        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        masterTable.add(panelTable).size(600, 600);

        stage.addActor(masterTable);

        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScene gameScene = (GameScene) sceneNavigator.getScene("GAME");
                if (gameScene != null) gameScene.requestRestart();
                sceneNavigator.goToScene("GAME");
            }
        });

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneNavigator.goToScene("MENU");
            }
        });
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
        int w = 600; // Wider panel
        int h = 600; 
        int r = 20; 
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);

        p.setColor(new Color(0.02f, 0.1f, 0.25f, 0.95f));
        fillRoundedRect(p, 5, 5, w - 10, h - 10, r - 5);

        // Fixed: Header properly placed at the top
        p.setColor(new Color(0.05f, 0.2f, 0.5f, 1f));
        fillRoundedRect(p, 5, 5, w - 10, 80, r - 5); 
        p.fillRectangle(5, 25, w - 10, 60);          

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