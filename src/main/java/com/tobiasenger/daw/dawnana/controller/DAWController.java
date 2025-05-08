// File: src/main/java/com/tobiasenger/daw/dawnana/controller/DAWController.java
package com.tobiasenger.daw.dawnana.controller;

import com.tobiasenger.daw.dawnana.model.Sequencer;
import com.tobiasenger.daw.dawnana.model.SequencerScene;
import com.tobiasenger.daw.dawnana.model.Sample;
import com.tobiasenger.daw.dawnana.view.DAWView;
import com.tobiasenger.daw.dawnana.view.MixerView;
import com.tobiasenger.daw.dawnana.view.SequencerView;
import com.tobiasenger.daw.dawnana.view.TrackPaneView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for scene-based DAW.
 * Allows immediate scene switches when not playing,
 * defers switches until end of cycle when playing.
 */
public class DAWController {
    private final Sequencer sequencer = new Sequencer();
    private final List<TrackPaneView> panes = new ArrayList<>();

    private final SequencerView sequencerView;
    private final MixerView mixerView;
    private final DAWView view;

    private int currentStep = 0;
    private boolean sceneSwitchRequested = false;
    private int requestedSceneIndex = -1;

    public DAWController(Stage stage) {
        sequencerView = new SequencerView();
        mixerView = new MixerView();
        view = new DAWView(stage, sequencerView, mixerView);

        // Hook up controls
        view.playPauseBtn.setOnAction(e -> onPlayPause());
        view.stopBtn.setOnAction(e -> onStop());
        sequencerView.addTrackBtn.setOnAction(e -> onAddTrack());

        // Add Scene button
        Button addSceneBtn = new Button("+ Scene");
        addSceneBtn.setOnAction(e -> onAddScene());
        view.sceneBar.getChildren().add(addSceneBtn);

        // Initialize 4 tracks with default samples
        initializeTracks();

        // Create the first scene
        onAddScene();
    }

    private void initializeTracks() {
        String[] sampleFiles = {"kick.mp3", "snare.mp3", "hh_closed.mp3", "hh_open.mp3"};
        String[] sampleNames = {"Kick", "Snare", "Closed HH", "Open HH"};
        for (int i = 0; i < 4; i++) {
            addTrackWithSample(sampleFiles[i], sampleNames[i]);
        }
    }

    private void addTrackWithSample(String sampleFile, String sampleName) {
        sequencer.addTrack();
        TrackPaneView tpv = new TrackPaneView(panes.size(), sequencer);
        tpv.nameField.setText(sampleName);
        panes.add(tpv);

        URL resource = getClass().getResource("/samples/" + sampleFile);
        if (resource == null) throw new RuntimeException(sampleFile);
        Sample sample = new Sample(resource.toExternalForm());

        sequencer.getTracks().get(panes.size() - 1).addSample(sample);
        sequencerView.rebuildGrid(panes, sequencer.getSteps());
        mixerView.buildMixer(panes);
    }


    private void onPlayPause() {
        boolean playing = view.playPauseBtn.isSelected();
        view.playPauseBtn.setGraphic(view.loadIcon(playing ? "pause.png" : "play.png", 16));
        if (playing) {
            sequencer.setBpm(Integer.parseInt(view.bpmField.getText()));
            sequencer.play(this::onStep);
            view.startSceneBlinking();
        } else {
            // if stopped and a switch is pending, apply immediately
            if (sceneSwitchRequested) {
                applySceneSwitch(requestedSceneIndex);
            }
            sequencer.stop();
            view.stopSceneBlinking();
        }
    }

    private void onStop() {
        sequencer.stop();
        view.stopSceneBlinking();
        currentStep = 0;
        panes.forEach(TrackPaneView::clearIndicators);
        view.playPauseBtn.setSelected(false);
        view.playPauseBtn.setGraphic(view.loadIcon("play.png", 16));
    }

    private void onAddTrack() {
        sequencer.addTrack();
        TrackPaneView tpv = new TrackPaneView(panes.size(), sequencer);
        panes.add(tpv);
        sequencerView.rebuildGrid(panes, sequencer.getSteps());
        mixerView.buildMixer(panes);
    }

    private void onAddScene() {
        if (sequencer.getCurrentSceneIndex() >= 0) {
            saveUIToScene(sequencer.getCurrentSceneIndex());
        }

        sequencer.addScene();
        int newIndex = sequencer.getCurrentSceneIndex();

        view.addSceneButton(newIndex + 1, () -> requestSceneSwitch(newIndex));
        view.highlightSceneButton(newIndex + 1);

        clearUI();
    }

    private void requestSceneSwitch(int newIndex) {
        if (!view.playPauseBtn.isSelected()) {
            // immediate switch when not playing
            applySceneSwitch(newIndex);
        } else {
            // defer until end of cycle
            sceneSwitchRequested = true;
            requestedSceneIndex = newIndex;
        }
    }

    private void applySceneSwitch(int sceneIndex) {
        view.stopSceneBlinking();
        // Save current UI to old scene
        saveUIToScene(sequencer.getCurrentSceneIndex());
        // Switch in model
        sequencer.switchScene(sceneIndex);
        // Load new scene UI
        loadSceneUI(sceneIndex);
        // Highlight button
        Platform.runLater(() -> view.highlightSceneButton(sceneIndex + 1));
        if(view.playPauseBtn.isSelected()) {
            view.startSceneBlinking();
        }
        sceneSwitchRequested = false;
    }

    private void onStep() {
        int steps = sequencer.getSteps();
        int prev = (currentStep + steps - 1) % steps;
        for (int r = 0; r < panes.size(); r++) {
            TrackPaneView tpv = panes.get(r);
            tpv.clearCell(prev);
            tpv.highlightCell(currentStep);
            if (tpv.isActive(currentStep)) {
                Sample s = sequencer.getTracks().get(r).getSamples().get(0);
                s.play(sequencer.getTracks().get(r).getVolume());
            }
        }
        // handle deferred switch at end of cycle
        if (currentStep == steps - 1 && sceneSwitchRequested) {
            applySceneSwitch(requestedSceneIndex);
        }
        currentStep = (currentStep + 1) % steps;
    }

    private void saveUIToScene(int sceneIndex) {
        SequencerScene scene = sequencer.getScenes().get(sceneIndex);
        for (int r = 0; r < panes.size(); r++) {
            boolean[] pattern = new boolean[sequencer.getSteps()];
            for (int c = 0; c < pattern.length; c++) {
                pattern[c] = panes.get(r).pads.get(c).isSelected();
            }
            scene.setPattern(r, pattern);
        }
    }

    private void loadSceneUI(int sceneIndex) {
        SequencerScene scene = sequencer.getScenes().get(sceneIndex);
        for (int r = 0; r < panes.size(); r++) {
            boolean[] pattern = scene.getPattern(r);
            for (int c = 0; c < pattern.length; c++) {
                panes.get(r).pads.get(c).setSelected(pattern[c]);
            }
        }
    }

    private void clearUI() {
        for (TrackPaneView tpv : panes) {
            for (int c = 0; c < sequencer.getSteps(); c++) {
                tpv.pads.get(c).setSelected(false);
            }
        }
    }
}
