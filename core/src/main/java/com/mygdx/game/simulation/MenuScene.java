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

public class MenuScene extends Scene {

    private Stage stage;

    public MenuScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture) {
        super(id);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.8f); // Scaled to fit nicely in the panel header

        // 1. Space Background
        TextureRegionDrawable baseDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        Image bgImage = new Image(baseDrawable);
        bgImage.setColor(new Color(0.05f, 0.08f, 0.15f, 1f)); 
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        // --- DEFINING THE COLORS ---
        Color cyanBorder  = new Color(0.0f, 0.8f, 1.0f, 1f);     
        Color yellowBorder = new Color(1.0f, 0.8f, 0.1f, 1f); 
        Color coreBlue    = new Color(0.15f, 0.35f, 0.65f, 1f); 
        Color hoverBlue   = new Color(0.25f, 0.50f, 0.85f, 1f); 

        // 2. Setup Button Styles
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

        // 3. Create the Main Panel Background
        TextureRegionDrawable panelBackground = createPanelDrawable(cyanBorder);

        // 4. Game Title Text
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE); 
        Label titleLabel = new Label("MATH RUN", titleStyle);
        titleLabel.setAlignment(Align.center);

        // 5. Instantiate Buttons
        TextButton btnPlay = new TextButton("PLAY", cyanStyle);
        TextButton btnSettings = new TextButton("SYSTEM SETTINGS", yellowStyle); 
        TextButton btnQuit = new TextButton("EXIT DESKTOP", cyanStyle);

        // 6. Layout within the Panel
        Table panelTable = new Table();
        panelTable.setBackground(panelBackground);
        panelTable.setSize(500, 550); // Panel size to perfectly fit 3 buttons
        
        // Add Header
        panelTable.add(titleLabel).width(500).padTop(25).padBottom(50).row();
        
        // Add Body Elements
        panelTable.add(btnPlay).size(350, 65).padBottom(20).row();
        panelTable.add(btnSettings).size(350, 65).padBottom(20).row();
        panelTable.add(btnQuit).size(350, 65);
        
        // Push everything up slightly
        panelTable.add().expandY().fillY(); 

        // 7. Master Table Layout
        Table masterTable = new Table();
        masterTable.setFillParent(true);
        masterTable.center();
        masterTable.add(panelTable).size(500, 550);

        stage.addActor(masterTable);

        // --- Listeners ---
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Routes to your new Instructions Scene!
                sceneNavigator.goToScene("INSTRUCTIONS");
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
        int w = 500;
        int h = 550; 
        int r = 20; 
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        // 1. Outer Border
        p.setColor(borderColor);
        fillRoundedRect(p, 0, 0, w, h, r);

        // 2. Inner Dark Body (Fills the whole panel)
        p.setColor(new Color(0.02f, 0.1f, 0.25f, 0.95f));
        fillRoundedRect(p, 5, 5, w - 10, h - 10, r - 5);

        // 3. Lighter Header Bar (Fixed: Placed properly at the top)
        p.setColor(new Color(0.05f, 0.2f, 0.5f, 1f));
        fillRoundedRect(p, 5, 5, w - 10, 80, r - 5); // Draws with rounded top corners
        p.fillRectangle(5, 25, w - 10, 60);          // Squares off the bottom of the header

        // 4. Header Divider Line
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