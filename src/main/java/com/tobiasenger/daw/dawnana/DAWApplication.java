// File: src/main/java/com/tobiasenger/daw/dawnana/DAWApplication.java
package com.tobiasenger.daw.dawnana;

import com.tobiasenger.daw.dawnana.model.Sample;
import com.tobiasenger.daw.dawnana.model.Sequencer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * DAW Prototype with resource-based icons, Play/Pause toggle, Stop reset,
 * dynamic tracks, BPM input, and enhanced step indicator.
 */
public class DAWApplication extends Application {
    private final Sequencer sequencer = new Sequencer();
    private final List<TrackPane> trackPanes = new ArrayList<>();
    private final GridPane grid = new GridPane();
    private final TextField bpmField = new TextField("120");
    private int currentStep = 0;

    @Override
    public void start(Stage stage) {
        // Transport controls: Play/Pause toggle and Stop
        ToggleButton playPauseBtn = new ToggleButton();
        playPauseBtn.setGraphic(loadIcon("play.png", 16));
        playPauseBtn.setOnAction(e -> {
            if (playPauseBtn.isSelected()) {
                playPauseBtn.setGraphic(loadIcon("pause.png", 16));
                int bpm = Integer.parseInt(bpmField.getText());
                sequencer.setBpm(bpm);
                sequencer.play(this::onStep);
            } else {
                playPauseBtn.setGraphic(loadIcon("play.png", 16));
                sequencer.stop();
            }
        });

        Button stopBtn = new Button();
        stopBtn.setGraphic(loadIcon("stop.png", 16));
        stopBtn.setOnAction(e -> {
            sequencer.stop();
            currentStep = 0;
            clearAllIndicators();
            playPauseBtn.setSelected(false);
            playPauseBtn.setGraphic(loadIcon("play.png", 16));
        });

        bpmField.setPrefWidth(60);
        HBox controls = new HBox(10, playPauseBtn, stopBtn, new Label("BPM:"), bpmField);
        controls.setPadding(new Insets(10));

        // Main grid: column 0=name, 1=load, 2..=pads
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        // Bottom: Add track
        Button addTrackBtn = new Button();
        addTrackBtn.setGraphic(loadIcon("plus.png", 16));
        addTrackBtn.setOnAction(e -> {
            addTrack();
            layoutGrid();
        });

        HBox bottom = new HBox(addTrackBtn);
        bottom.setPadding(new Insets(10));

        // Assemble scene
        VBox root = new VBox(controls, grid, bottom);
        VBox.setVgrow(grid, Priority.ALWAYS);

        // Init with 2 tracks
        addTrack();
        addTrack();
        layoutGrid();

        Scene scene = new Scene(root, 1000, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("DAW Prototype");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Helper to load an icon from resources/icons directory.
     * @param name filename in /icons/, e.g. "play.png"
     * @param size width/height in px
     */
    private ImageView loadIcon(String name, double size) {
        InputStream is = getClass().getResourceAsStream("/icons/" + name);
        if (is == null) throw new RuntimeException("Icon resource not found: " + name);
        Image img = new Image(is);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    private void addTrack() {
        TrackPane tp = new TrackPane(trackPanes.size());
        trackPanes.add(tp);
        sequencer.addTrack();
    }

    private void layoutGrid() {
        grid.getChildren().clear();
        for (int r = 0; r < trackPanes.size(); r++) {
            TrackPane tp = trackPanes.get(r);
            grid.add(tp.nameField, 0, r);
            grid.add(tp.loadBtn, 1, r);
            for (int c = 0; c < sequencer.getSteps(); c++) {
                ToggleButton pad = tp.pads.get(c);
                grid.add(pad, c + 2, r);
            }
        }
    }

    private void onStep() {
        int prev = (currentStep + sequencer.getSteps() - 1) % sequencer.getSteps();
        for (int r = 0; r < trackPanes.size(); r++) {
            TrackPane tp = trackPanes.get(r);
            tp.pads.get(prev).getStyleClass().removeAll("active-step", "active-play");
            ToggleButton pad = tp.pads.get(currentStep);
            if (pad.isSelected() && !sequencer.getTracks().get(r).getSamples().isEmpty()) {
                pad.getStyleClass().add("active-play");
                sequencer.getTracks().get(r).getSamples().get(0).play();
            } else {
                pad.getStyleClass().add("active-step");
            }
        }
        currentStep = (currentStep + 1) % sequencer.getSteps();
    }

    private void clearAllIndicators() {
        for (TrackPane tp : trackPanes) {
            for (ToggleButton pad : tp.pads) {
                pad.getStyleClass().removeAll("active-step", "active-play");
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Inner class: holds UI for one track (name editing, load button, pads)
     */
    private class TrackPane {
        final TextField nameField = new TextField("Track");
        final Button loadBtn = new Button("Load");
        final List<ToggleButton> pads = new ArrayList<>();
        private final int trackIndex;

        TrackPane(int index) {
            this.trackIndex = index;
            nameField.setPrefWidth(100);
            loadBtn.setOnAction(e -> loadSample());
            for (int i = 0; i < sequencer.getSteps(); i++) {
                ToggleButton tb = new ToggleButton();
                tb.setMinSize(30, 30);
                pads.add(tb);
            }
        }

        private void loadSample() {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV Files", "*.wav"));
            File file = fc.showOpenDialog(null);
            if (file != null) {
                nameField.setText(file.getName());
                sequencer.getTracks().get(trackIndex)
                        .addSample(new Sample(file.toURI().toString()));
            }
        }
    }
}
