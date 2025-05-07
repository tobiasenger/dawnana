// File: src/main/java/com/tobiasenger/daw/dawnana/model/Sequencer.java
package com.tobiasenger.daw.dawnana.model;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Basic sequencer logic: manages tracks, steps, and playback.
 */
public class Sequencer {
    private final List<Track> tracks = new ArrayList<>();
    private int bpm = 120;
    private final int steps = 16;
    private Timeline timeline;

    /**
     * Adds a new track to the sequencer.
     */
    public void addTrack() {
        String name = "Track " + (tracks.size() + 1);
        tracks.add(new Track(name));
    }

    /**
     * Returns an unmodifiable list of tracks.
     */
    public List<Track> getTracks() {
        return List.copyOf(tracks);
    }

    /**
     * Returns the number of steps (columns) in the sequencer.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Sets the tempo in beats per minute.
     */
    public void setBpm(int bpm) {
        this.bpm = bpm;
        if (timeline != null) {
            timeline.setRate(bpm / 60.0);
        }
    }

    /**
     * Starts playback, calling onStep for each step interval.
     */
    public void play(Runnable onStep) {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;
        double interval = 60000.0 / bpm / 4; // 16th notes
        timeline = new Timeline(new KeyFrame(Duration.millis(interval), e -> onStep.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops playback.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
