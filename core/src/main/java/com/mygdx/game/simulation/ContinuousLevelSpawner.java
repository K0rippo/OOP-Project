package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

/**
 * ContinuousLevelSpawner - builds the full list of {@link LevelSegment}s upfront and
 * triggers entity spawning for each segment when the camera scroll reaches it.
 *
 * <p>Responsibilities (SRP):
 * <ul>
 *   <li>Owns the ordered list of segments.</li>
 *   <li>Decides <em>when</em> a segment should be spawned (lookahead distance).</li>
 *   <li>Delegates <em>what</em> to spawn to a {@link ISegmentSpawnDelegate}.</li>
 * </ul>
 *
 * <p>OCP: adding new segment types only requires extending {@link LevelSegment} and
 * updating the delegate — this class stays closed for modification.
 *
 * <p>DIP: spawning logic is hidden behind {@link ISegmentSpawnDelegate}; GameScene
 * provides the concrete implementation as a lambda or anonymous class.
 */
public class ContinuousLevelSpawner {

    /**
     * Callback interface (DIP) so the spawner does not depend on GameScene directly.
     */
    public interface ISegmentSpawnDelegate {
        /**
         * Called exactly once per segment, just before it scrolls on-screen.
         *
         * @param segment the segment whose entities should now be created
         */
        void onSpawnSegment(LevelSegment segment);
    }

    // -----------------------------------------------------------------------

    /** How far ahead of the current camera-right edge we pre-spawn a segment. */
    private static final float SPAWN_LOOKAHEAD = 250f;

    /** Gap of empty space between consecutive segments (breathing room). */
    private static final float INTER_SEGMENT_GAP = 300f;

    private final Array<LevelSegment> segments = new Array<>();
    private final ISegmentSpawnDelegate delegate;

    // -----------------------------------------------------------------------

    /**
     * @param questionProvider  source of questions — one segment is created per question
     * @param stageStartX       world-X where the first segment begins
     * @param delegate          entity-creation callback provided by GameScene
     */
    public ContinuousLevelSpawner(IQuestionProvider questionProvider,
                                  float stageStartX,
                                  ISegmentSpawnDelegate delegate) {
        this.delegate = delegate;
        buildSegments(questionProvider, stageStartX);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Call every frame from GameScene.update().
     *
     * @param cameraRightEdge  the rightmost world-X currently visible on screen
     *                         (= scrolled distance + WORLD_WIDTH)
     */
    public void update(float cameraRightEdge) {
        for (LevelSegment seg : segments) {
            if (!seg.isSpawned() && seg.getStartX() < cameraRightEdge + SPAWN_LOOKAHEAD) {
                seg.markSpawned();
                delegate.onSpawnSegment(seg);
            }
        }
    }

    /** Returns all segments (read-only use only). */
    public Array<LevelSegment> getSegments() {
        return segments;
    }

    /** True when every segment has been spawned. */
    public boolean allSegmentsSpawned() {
        for (LevelSegment seg : segments) {
            if (!seg.isSpawned()) return false;
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void buildSegments(IQuestionProvider provider, float firstStart) {
        float x = firstStart;
        for (int i = 0; i < provider.getTotalQuestions(); i++) {
            segments.add(new LevelSegment(i, x));
            x += LevelSegment.SEGMENT_WIDTH + INTER_SEGMENT_GAP;
        }
    }
}
