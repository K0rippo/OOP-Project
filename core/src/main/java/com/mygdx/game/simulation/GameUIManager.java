package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

/**
 * GameUIManager - renders HUD elements.
 *
 * SRP : only responsible for UI label management.
 * DIP : GameScene passes primitive arrays — no coupling to GameScene internals.
 */
public class GameUIManager {

    private static final int MAX_ANSWER_LABELS = 15; // 5 questions x 3 answers max on screen

    private final Stage      stage;
    private final Label      questionLabel;
    private final BitmapFont questionFont;
    private final BitmapFont answerFont;
    private final BitmapFont hpFont;
    private final float      WORLD_HEIGHT;
    private final Label[]    barrierHpLabels  = new Label[3];
    private final Label[]    answerLabelPool  = new Label[MAX_ANSWER_LABELS];

    public GameUIManager(Stage stage, float worldHeight) {
        this.stage        = stage;
        this.WORLD_HEIGHT = worldHeight;

        questionFont = new BitmapFont();
        answerFont   = new BitmapFont();
        hpFont       = new BitmapFont();

        Label.LabelStyle questionStyle = new Label.LabelStyle(questionFont, new Color(1f, 0.95f, 0.75f, 1f));
        Label.LabelStyle answerStyle   = new Label.LabelStyle(answerFont, Color.WHITE);
        Label.LabelStyle hpStyle       = new Label.LabelStyle(hpFont, new Color(1f, 0.85f, 0.3f, 1f));

        questionLabel = new Label("", questionStyle);
        questionLabel.setFontScale(1.35f);
        questionLabel.setPosition(24, WORLD_HEIGHT - 55);
        stage.addActor(questionLabel);

        for (int i = 0; i < MAX_ANSWER_LABELS; i++) {
            answerLabelPool[i] = new Label("", answerStyle);
            answerLabelPool[i].setFontScale(1.2f);
            answerLabelPool[i].setVisible(false);
            stage.addActor(answerLabelPool[i]);
        }

        for (int i = 0; i < 3; i++) {
            barrierHpLabels[i] = new Label("", hpStyle);
            barrierHpLabels[i].setFontScale(1.0f);
            barrierHpLabels[i].setVisible(false);
            stage.addActor(barrierHpLabels[i]);
        }
    }

    public void updateQuestion(Question question) {
        if (question == null) return;
        questionLabel.setText("Q: " + question.getText());
    }

    /**
     * Positions answer labels using parallel arrays of text and wall geometry.
     * GameScene passes raw data — no DTO class required.
     * count = how many labels to show (rest are hidden).
     */
    public void syncAnswerLabels(String[] texts, float[] xs, float[] ys,
                                  float[] heights, int count) {
        for (Label l : answerLabelPool) l.setVisible(false);
        for (int i = 0; i < count && i < MAX_ANSWER_LABELS; i++) {
            answerLabelPool[i].setText(texts[i]);
            answerLabelPool[i].setPosition(xs[i] - 90f, ys[i] + heights[i] / 2f - 10f);
            answerLabelPool[i].setVisible(true);
        }
    }

    public void syncBarrierHp(Array<BreakableBarrier> barriers) {
        for (int i = 0; i < 3; i++) {
            if (barriers != null && i < barriers.size) {
                BreakableBarrier b = barriers.get(i);
                if (b != null && b.isActive() && !b.isBroken()) {
                    barrierHpLabels[i].setText(String.valueOf(b.getHitPoints()));
                    barrierHpLabels[i].setVisible(true);
                    barrierHpLabels[i].setPosition(
                            b.getPosition().x + 2f,
                            b.getPosition().y + b.getHeight() / 2f - 8f);
                    continue;
                }
            }
            barrierHpLabels[i].setVisible(false);
        }
    }

    public void act(float deltaTime) { stage.act(deltaTime); }

    public void dispose() {
        questionFont.dispose();
        answerFont.dispose();
        hpFont.dispose();
    }
}
