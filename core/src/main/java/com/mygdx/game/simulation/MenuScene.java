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

public class MenuScene extends Scene {

    private Stage stage;

    public MenuScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture) {
        super(id);
        this.stage = new Stage(new StretchViewport(1280, 720));

        // 1. Setup Default BitmapFonts (Scaled for UI)
        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3.5f);

        // 2. Programmatic Space Background
        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Image bgImage = new Image(baseDrawable);
        bgImage.setColor(new Color(0.05f, 0.08f, 0.2f, 1f)); 
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        // --- DEFINING THE COLORS ---
        Color darkBlueBg = new Color(0.02f, 0.1f, 0.25f, 0.9f); 
        Color hoverBlueBg = new Color(0.1f, 0.25f, 0.45f, 0.9f); 
        
        Color cyanBorder = new Color(0.1f, 0.7f, 1f, 1f);       
        Color yellowBorder = new Color(1f, 0.8f, 0.1f, 1f);     

        // 3. Setup CYAN Button Style
        TextButton.TextButtonStyle cyanStyle = new TextButton.TextButtonStyle();
        cyanStyle.font = buttonFont; 
        cyanStyle.fontColor = cyanBorder; 
        cyanStyle.up = createBorderedDrawable(darkBlueBg, cyanBorder);     
        cyanStyle.over = createBorderedDrawable(hoverBlueBg, cyanBorder); 
        cyanStyle.down = createBorderedDrawable(cyanBorder, Color.WHITE); 

        // 4. Setup YELLOW Button Style
        TextButton.TextButtonStyle yellowStyle = new TextButton.TextButtonStyle();
        yellowStyle.font = buttonFont; 
        yellowStyle.fontColor = yellowBorder; 
        yellowStyle.up = createBorderedDrawable(darkBlueBg, yellowBorder);     
        yellowStyle.over = createBorderedDrawable(hoverBlueBg, yellowBorder); 
        yellowStyle.down = createBorderedDrawable(yellowBorder, Color.WHITE); 

        // 5. Game Title Text
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE); 
        Label titleLabel = new Label("FOOD RUN", titleStyle);

        // 6. Instantiate Buttons
        TextButton btnPlay = new TextButton("START RUN", cyanStyle);
        TextButton btnSettings = new TextButton("SYSTEM SETTINGS", yellowStyle); 
        TextButton btnQuit = new TextButton("EXIT DESKTOP", cyanStyle);

        // 7. Master Table Layout
        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        
        masterTable.add(titleLabel).padBottom(60).row();
        masterTable.add(btnPlay).size(350, 70).padBottom(20).row();
        masterTable.add(btnSettings).size(350, 70).padBottom(20).row();
        masterTable.add(btnQuit).size(350, 70);

        stage.addActor(masterTable);

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