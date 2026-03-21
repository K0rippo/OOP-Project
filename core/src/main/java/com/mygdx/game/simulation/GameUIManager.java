package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class GameUIManager {

    private static final int MAX_ANSWER_LABELS = 15;

    private final Stage      stage;
    private final Label      questionLabel;
    private final BitmapFont questionFont;
    private final BitmapFont answerFont;
    private final Label[]    answerLabelPool  = new Label[MAX_ANSWER_LABELS];

    public GameUIManager(Stage stage, float worldHeight) {
        this.stage        = stage;

        questionFont = new BitmapFont();
        answerFont   = new BitmapFont();

        Label.LabelStyle questionStyle = new Label.LabelStyle(questionFont, new Color(1f, 0.95f, 0.75f, 1f));
        Label.LabelStyle answerStyle   = new Label.LabelStyle(answerFont, Color.WHITE);

        questionLabel = new Label("", questionStyle);
        questionLabel.setFontScale(1.8f);
        questionLabel.setPosition(24, worldHeight - 60);
        stage.addActor(questionLabel);

        for (int i = 0; i < MAX_ANSWER_LABELS; i++) {
            answerLabelPool[i] = new Label("", answerStyle);
            answerLabelPool[i].setFontScale(1.2f);
            answerLabelPool[i].setVisible(false);
            stage.addActor(answerLabelPool[i]);
        }
    }

    public void updateQuestion(Question question) {
        if (question == null) return;
        questionLabel.setText("Q: " + question.getText());
    }

    public void syncAnswerLabels(String[] texts, float[] xs, float[] ys,
                                  float[] heights, int count) {
        //hide all labels before showing active ones
        for (Label l : answerLabelPool) l.setVisible(false);
        for (int i = 0; i < count && i < MAX_ANSWER_LABELS; i++) {
            answerLabelPool[i].setText(texts[i]);
            answerLabelPool[i].setPosition(xs[i] - 90f, ys[i] + heights[i] / 2f - 10f);
            answerLabelPool[i].setVisible(true);
        }
    }

    public void act(float deltaTime) { stage.act(deltaTime); }

    public void dispose() {
        questionFont.dispose();
        answerFont.dispose();
    }
}
