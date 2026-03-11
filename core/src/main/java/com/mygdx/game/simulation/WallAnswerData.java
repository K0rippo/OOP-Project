package com.mygdx.game.simulation;

/**
 * WallAnswerData - plain DTO passed from GameScene to GameUIManager.
 *
 * SRP : carries only what is needed to position and label one answer wall.
 * DIP : GameUIManager depends on this, not on GameScene internals.
 */
public class WallAnswerData {
    public final String  answerText;
    public final float   wallX;
    public final float   wallY;
    public final float   wallHeight;

    public WallAnswerData(String text, float x, float y, float height) {
        this.answerText = text;
        this.wallX      = x;
        this.wallY      = y;
        this.wallHeight = height;
    }
}
