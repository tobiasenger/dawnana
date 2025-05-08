// File: src/main/java/com/tobiasenger/daw/dawnana/model/SequencerScene.java
package com.tobiasenger.daw.dawnana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A scene saves for every Track a boolean array for the active steps
 */
public class SequencerScene {
    private final List<boolean[]> trackPatterns;

    /**
     * initializes a new scene with a number of steps
     */
    public SequencerScene(int trackCount, int steps) {
        trackPatterns = new ArrayList<>(trackCount);
        for (int i = 0; i < trackCount; i++) {
            trackPatterns.add(new boolean[steps]);
        }
    }

    /** returns the pattern of the track */
    public boolean[] getPattern(int trackIndex) {
        return trackPatterns.get(trackIndex);
    }

    /** saves a new pattern of the track */
    public void setPattern(int trackIndex, boolean[] pattern) {
        trackPatterns.set(trackIndex, pattern);
    }

    /** add a new track with empty pattern */
    public void addTrack(int steps) {
        trackPatterns.add(new boolean[steps]);
    }

    /** returns the number of tracks of this scene */
    public int getTrackCount() {
        return trackPatterns.size();
    }

    /** returns the number of steps of a pattern */
    public int getSteps() {
        return trackPatterns.isEmpty() ? 0 : trackPatterns.get(0).length;
    }
}
