package com.mygdx.game.simulation;

/**
 * LevelSegment - immutable descriptor for one question zone on the continuous stage.
 *
 * A segment occupies a known horizontal range on the world's coordinate system.
 * The ContinuousLevelSpawner reads this to know when to materialise entities.
 *
 * OCP: new segment types (boss, bonus…) can extend this without touching GameScene.
 */
public class LevelSegment {

    /** Horizontal width reserved for one full question zone. */
    public static final float SEGMENT_WIDTH = 900f;

    /** Offset from segment start where the answer gate columns appear. */
    public static final float GATE_OFFSET = 520f;
    
    /** Offset from segment start where the breakable barriers appear — flush with the gate's left edge. */
    public static final float BARRIER_OFFSET = GATE_OFFSET - 12f;

    /** Offset from segment start where cannons appear (staggered between barriers & gates). */
    public static final float CANNON_OFFSET = 270f;

    // -----------------------------------------------------------------------

    private final int   questionIndex;
    private final float startX;      // world-X coordinate where this segment begins
    private boolean     spawned = false;

    public LevelSegment(int questionIndex, float startX) {
        this.questionIndex = questionIndex;
        this.startX        = startX;
    }

    // ---- accessors --------------------------------------------------------

    public int   getQuestionIndex() { return questionIndex; }
    public float getStartX()        { return startX; }
    public float getEndX()          { return startX + SEGMENT_WIDTH; }

    public boolean isSpawned()  { return spawned; }
    public void    markSpawned(){ spawned = true; }

    // ---- derived spawn X positions ----------------------------------------

    /** World-X for the breakable barriers of this segment. */
    public float barrierX() { return startX + BARRIER_OFFSET; }

    /** World-X for the answer-gate columns of this segment. */
    public float gateX()    { return startX + GATE_OFFSET; }

    /** World-X base for cannons in this segment. */
    public float cannonX()  { return startX + CANNON_OFFSET; }
}