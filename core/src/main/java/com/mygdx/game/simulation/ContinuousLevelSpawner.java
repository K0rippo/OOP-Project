package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

/**
 * ContinuousLevelSpawner - spawns one segment at a time, gated by player progress.
 *
 * SRP : owns the segment list and spawn state only.
 * OCP : new segment types extend LevelSegment without touching this class.
 * DIP : delegates actual entity creation to ISegmentSpawnDelegate (GameScene lambda).
 */
public class ContinuousLevelSpawner {

    public interface ISegmentSpawnDelegate {
        void onSpawnSegment(LevelSegment segment);
    }

    private final Array<LevelSegment>    segments = new Array<>();
    private final ISegmentSpawnDelegate  delegate;
    private       int                    nextIndex = 0;

    public ContinuousLevelSpawner(IQuestionProvider questionProvider,
                                  float stageStartX,
                                  ISegmentSpawnDelegate delegate) {
        this.delegate = delegate;
        buildSegments(questionProvider, stageStartX);
    }

    /**
     * Spawns the first segment immediately on construction so the first
     * question is ready before the player starts moving.
     */
    public void spawnFirst() {
        if (!segments.isEmpty()) spawnNext();
    }

    /**
     * Call this after the player passes a gate to spawn the next segment.
     * Does nothing if all segments have already been spawned.
     */
    public void spawnNext() {
        if (nextIndex >= segments.size) return;
        LevelSegment seg = segments.get(nextIndex);
        seg.markSpawned();
        delegate.onSpawnSegment(seg);
        nextIndex++;
    }

    /** True when every segment has been spawned. */
    public boolean allSegmentsSpawned() {
        return nextIndex >= segments.size;
    }

    /** Returns all segments (read-only). */
    public Array<LevelSegment> getSegments() { return segments; }

    private void buildSegments(IQuestionProvider provider, float firstStart) {
        // All segments share the same startX since we now spawn them one at a time
        // relative to the current scroll position — GameScene offsets them on spawn.
        for (int i = 0; i < provider.getTotalQuestions(); i++) {
            segments.add(new LevelSegment(i, firstStart));
        }
    }
}