package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Manages UI elements for the game scene.
 * Extracted from GameScene to support single responsibility principle.
 */
public class GameUIManager {
    private Stage stage;
    private Label questionLabel;
    private Label[] answerLabels = new Label[3];
    private BitmapFont font;
    
    private final float WORLD_HEIGHT;

    public GameUIManager(Stage stage, float worldHeight) {
        this.stage = stage;
        this.WORLD_HEIGHT = worldHeight;
        setupUI();
    }

    private void setupUI() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        
        questionLabel = new Label("", style);
        questionLabel.setFontScale(1.5f);
        questionLabel.setPosition(20, WORLD_HEIGHT - 40);
        stage.addActor(questionLabel);

        for (int i = 0; i < 3; i++) {
            answerLabels[i] = new Label("", style);
            answerLabels[i].setFontScale(1.5f);
            stage.addActor(answerLabels[i]);
        }
    }

    public void updateQuestion(Question question) {
        if (question == null) return;
        questionLabel.setText("Q: " + question.getText());
    }

    public void updateAnswerLabels(String[] shuffledAnswers, float wallX, float sectionHeight) {
        if (shuffledAnswers.length != 3) return;
        
        for (int i = 0; i < 3; i++) {
            answerLabels[i].setText(shuffledAnswers[i]);
            answerLabels[i].setPosition(wallX - 80, (sectionHeight * (2 - i)) + (sectionHeight / 2));
        }
    }

    public void updateBackgroundColor(Color color) {
        // Return color for caller to set
    }

    public void act(float deltaTime) {
        stage.act(deltaTime);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
