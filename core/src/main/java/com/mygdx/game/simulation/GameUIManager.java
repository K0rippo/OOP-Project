package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engine.RectangleEntity;

public class GameUIManager {
    private final Stage stage;
    private final Label questionLabel;
    private final Label[] answerLabels = new Label[3];
    private final BitmapFont questionFont;
    private final BitmapFont answerFont;
    private final BitmapFont hpFont;
    private final float WORLD_HEIGHT;
    private final Label[] barrierHpLabels = new Label[3];

    public GameUIManager(Stage stage, float worldHeight) {
        this.stage = stage;
        this.WORLD_HEIGHT = worldHeight;

        questionFont = new BitmapFont();
        answerFont = new BitmapFont();
        hpFont = new BitmapFont();

        Label.LabelStyle questionStyle = new Label.LabelStyle(questionFont, new Color(1f, 0.95f, 0.75f, 1f));
        Label.LabelStyle answerStyle = new Label.LabelStyle(answerFont, Color.WHITE);
        Label.LabelStyle hpStyle = new Label.LabelStyle(hpFont, new Color(1f, 0.85f, 0.3f, 1f));

        questionLabel = new Label("", questionStyle);
        questionLabel.setFontScale(1.35f);
        questionLabel.setPosition(24, WORLD_HEIGHT - 55);
        stage.addActor(questionLabel);

        for (int i = 0; i < 3; i++) {
            answerLabels[i] = new Label("", answerStyle);
            answerLabels[i].setFontScale(1.2f);
            barrierHpLabels[i] = new Label("", hpStyle);
            barrierHpLabels[i].setFontScale(1.0f);
            barrierHpLabels[i].setVisible(false);
            stage.addActor(answerLabels[i]);
            stage.addActor(barrierHpLabels[i]);
        }
    }

    public void updateQuestion(Question question) {
        if (question == null) return;
        questionLabel.setText("Q: " + question.getText());
    }

    public void updateAnswerTexts(String[] answers) {
        if (answers == null || answers.length != 3) return;

        for (int i = 0; i < 3; i++) {
            answerLabels[i].setText(answers[i]);
        }
    }

    public void syncAnswerLabelsToWalls(Array<RectangleEntity> walls) {
        if (walls == null || walls.size < 3) return;

        for (int i = 0; i < 3; i++) {
            RectangleEntity wall = walls.get(i);
            answerLabels[i].setPosition(
                    wall.getPosition().x - 90f,
                    wall.getPosition().y + wall.getHeight() / 2f - 10f
            );
        }
    }
    
    public void syncBarrierHp(Array<BreakableBarrier> barriers) {
        for (int i = 0; i < 3; i++) {
            if (barriers != null && i < barriers.size) {
                BreakableBarrier barrier = barriers.get(i);

                if (barrier != null && barrier.isActive() && !barrier.isBroken()) {
                    barrierHpLabels[i].setText(String.valueOf(barrier.getHitPoints()));
                    barrierHpLabels[i].setVisible(true);
                    barrierHpLabels[i].setPosition(
                            barrier.getPosition().x + 2f,
                            barrier.getPosition().y + barrier.getHeight() / 2f - 8f
                    );
                } else {
                    barrierHpLabels[i].setText("");
                    barrierHpLabels[i].setVisible(false);
                }
            } else {
                barrierHpLabels[i].setText("");
                barrierHpLabels[i].setVisible(false);
            }
        }
    }

    public void act(float deltaTime) {
        stage.act(deltaTime);
    }

    public void dispose() {
        questionFont.dispose();
        answerFont.dispose();
        hpFont.dispose();
    }
}