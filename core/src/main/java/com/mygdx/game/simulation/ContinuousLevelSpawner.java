package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

public class ContinuousLevelSpawner {

    public interface ISegmentSpawnDelegate {
        void onSpawnSegment(LevelSegment segment);
    }

    private static final float SPAWN_LOOKAHEAD = 250f;

    private static final float INTER_SEGMENT_GAP = 300f;

    private final Array<LevelSegment> segments = new Array<>();
    private final ISegmentSpawnDelegate delegate;

    public ContinuousLevelSpawner(IQuestionProvider questionProvider,
                                  float stageStartX,
                                  ISegmentSpawnDelegate delegate) {
        this.delegate = delegate;
        buildSegments(questionProvider, stageStartX);
    }

    public void update(float cameraRightEdge) {
        for (LevelSegment seg : segments) {
            if (!seg.isSpawned() && seg.getStartX() < cameraRightEdge + SPAWN_LOOKAHEAD) {
                seg.markSpawned();
                delegate.onSpawnSegment(seg);
            }
        }
    }

    public boolean allSegmentsSpawned() {
        for (LevelSegment seg : segments) {
            if (!seg.isSpawned()) return false;
        }
        return true;
    }

    private void buildSegments(IQuestionProvider provider, float firstStart) {
        //prebuild all segments so spawn checks are cheap at runtime
        float x = firstStart;
        for (int i = 0; i < provider.getTotalQuestions(); i++) {
            segments.add(new LevelSegment(i, x));
            x += LevelSegment.SEGMENT_WIDTH + INTER_SEGMENT_GAP;
        }
    }
}
