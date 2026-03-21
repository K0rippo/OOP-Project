package com.mygdx.game.simulation;

public class LevelSegment {

    public static final float SEGMENT_WIDTH = 900f;

    public static final float GATE_OFFSET = 520f;

    public static final float BARRIER_OFFSET = GATE_OFFSET - 12f;

    private final int   questionIndex;
    private final float startX;
    private boolean     spawned = false;

    public LevelSegment(int questionIndex, float startX) {
        this.questionIndex = questionIndex;
        this.startX        = startX;
    }

    public int   getQuestionIndex() { return questionIndex; }
    public float getStartX()        { return startX; }

    public boolean isSpawned()  { return spawned; }
    public void    markSpawned(){ spawned = true; }

    public float barrierX() { return startX + BARRIER_OFFSET; }

    public float gateX()    { return startX + GATE_OFFSET; }
}