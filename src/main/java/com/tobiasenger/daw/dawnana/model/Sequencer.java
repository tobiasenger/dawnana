// File: src/main/java/com/tobiasenger/daw/dawnana/model/Sequencer.java
package com.tobiasenger.daw.dawnana.model;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Sequencer with track and scene management.
 */
public class Sequencer {
    private final List<Track> tracks = new ArrayList<>();
    private int bpm = 120;
    private final int steps = 16;
    private Timeline timeline;

    private final List<SequencerScene> scenes = new ArrayList<>();
    private int currentSceneIndex = -1;

    /** Adds a new track and augments all scenes by an empty pattern */
    public void addTrack() {
        tracks.add(new Track("Track " + (tracks.size() + 1)));
        for (SequencerScene scene : scenes) {
            scene.addTrack(steps);
        }
    }

    public List<Track> getTracks() {
        return List.copyOf(tracks);
    }

    public int getSteps() {
        return steps;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        if (timeline != null) timeline.setRate(bpm / 60.0);
    }

    public void play(Runnable onStep) {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;
        double interval = 60000.0 / bpm / 4;
        timeline = new Timeline(new KeyFrame(Duration.millis(interval), e -> onStep.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    // Szene-API

    /**
     * Creates a new and empty scene and activates it
     */
    public void addScene() {
        SequencerScene scene = new SequencerScene(tracks.size(), steps);
        scenes.add(scene);
        currentSceneIndex = scenes.size() - 1;
    }

    public List<SequencerScene> getScenes() {
        return List.copyOf(scenes);
    }

    public int getCurrentSceneIndex() {
        return currentSceneIndex;
    }

    public SequencerScene getCurrentScene() {
        if (currentSceneIndex < 0 || currentSceneIndex >= scenes.size()) {
            throw new IllegalStateException("No scene selected");
        }
        return scenes.get(currentSceneIndex);
    }

    /**
     * Switches to a new Scene
     */
    public void switchScene(int index) {
        if (index < 0 || index >= scenes.size()) {
            throw new IllegalArgumentException("Invalid scene index: " + index);
        }
        currentSceneIndex = index;
    }
}
