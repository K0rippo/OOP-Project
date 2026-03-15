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

public class ResultScene extends Scene {

    private Stage stage;
    private Label scoreLabel;

    public ResultScene(String id, final ISceneNavigator sceneNavigator, Texture buttonTexture) {
        super(id);
        this.stage = new Stage(new StretchViewport(1280, 720));

        BitmapFont buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);
        
        BitmapFont scoreFont = new BitmapFont();
        scoreFont.getData().setScale(2.5f);

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

        TextButton.TextButtonStyle cyanStyle = new TextButton.TextButtonStyle();
        cyanStyle.font = buttonFont;
        cyanStyle.fontColor = cyanBorder; 
        cyanStyle.up = createBorderedDrawable(darkBlueBg, cyanBorder);     
        cyanStyle.over = createBorderedDrawable(hoverBlueBg, cyanBorder); 
        cyanStyle.down = createBorderedDrawable(cyanBorder, Color.WHITE); 

        TextButton.TextButtonStyle yellowStyle = new TextButton.TextButtonStyle();
        yellowStyle.font = buttonFont;
        yellowStyle.fontColor = yellowBorder; 
        yellowStyle.up = createBorderedDrawable(darkBlueBg, yellowBorder);     
        yellowStyle.over = createBorderedDrawable(hoverBlueBg, yellowBorder); 
        yellowStyle.down = createBorderedDrawable(yellowBorder, Color.WHITE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, cyanBorder); 
        Label titleLabel = new Label("RUN COMPLETE!", titleStyle); 

        Label.LabelStyle scoreStyle = new Label.LabelStyle(scoreFont, yellowBorder);
        scoreLabel = new Label("Score: 0 / 5", scoreStyle);

        TextButton btnRestart = new TextButton("RESTART RUN", cyanStyle);
        TextButton btnMenu = new TextButton("RETURN TO MENU", yellowStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        
        table.add(titleLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(60).row();
        table.add(btnRestart).size(350, 70).padBottom(20).row();
        table.add(btnMenu).size(350, 70);

        stage.addActor(table);

        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScene gameScene = (GameScene) sceneNavigator.getScene("GAME");
                if (gameScene != null) gameScene.requestRestart();
                sceneNavigator.goToScene("GAME");
            }
        });

        btnMenu.addListener(new ClickListener() {
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

    public void setScore(int current, int max) {
        scoreLabel.setText("Score: " + current + " / " + max);
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