// File: src/main/java/com/tobiasenger/daw/dawnana/view/DAWView.java
package com.tobiasenger.daw.dawnana.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;

/** Main container toggling between SequencerView and MixerView */
public class DAWView {
    public final ToggleButton playPauseBtn = new ToggleButton();
    public final Button stopBtn = new Button();
    public final ToggleButton mixerToggle = new ToggleButton("Mixer");
    public final TextField bpmField = new TextField("128");
    public final HBox sceneBar = new HBox(5);

    private final BorderPane root = new BorderPane();
    private final SequencerView sequencerView;
    private final MixerView mixerView;
    private Timeline sceneBlinkTimeline;

    public DAWView(Stage stage, SequencerView seqView, MixerView mixView) {
        this.sequencerView = seqView;
        this.mixerView = mixView;
        setupTopBar();
        playPauseBtn.getStyleClass().add("play-pause-btn");
        stopBtn.getStyleClass().add("stop-btn");
        root.setCenter(sequencerView.getView());
        VBox topContainer = new VBox();
        topContainer.getChildren().addAll((HBox) root.getTop());
        root.setCenter(sequencerView.getView());
        root.setTop(topContainer);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("DAWnana");
        stage.setMinWidth(780);
        stage.setMinHeight(400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.show();
    }

    private void setupTopBar() {
        playPauseBtn.setGraphic(loadIcon("play.png", 16));
        stopBtn.setGraphic(loadIcon("stop.png", 16));
        mixerToggle.setOnAction(e -> root.setCenter(mixerToggle.isSelected() ? mixerView.getView() : sequencerView.getView()));
        bpmField.setPrefWidth(60);
        HBox controls = new HBox(10, playPauseBtn, stopBtn, mixerToggle, new Label("BPM:"), bpmField, sceneBar);
        controls.setPadding(new Insets(10));
        root.setTop(controls);
    }

    /** Loads an icon from /icons/ resource. */
    public ImageView loadIcon(String name, double size) {
        InputStream is = getClass().getResourceAsStream("/icons/" + name);
        if (is == null) throw new RuntimeException("Icon not found: " + name);
        Image img = new Image(is);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    /** Adds a scene-selection button labeled 1-based. */
    public void addSceneButton(int sceneNumber, Runnable onClick) {
        ToggleButton btn = new ToggleButton(String.valueOf(sceneNumber));
        btn.setOnAction(e -> onClick.run());
        sceneBar.getChildren().add(btn);
    }

    /** Highlights the active scene button. */
    public void highlightSceneButton(int sceneNumber) {
        for (Node node : sceneBar.getChildren()) {
            if (node instanceof ToggleButton btn) {
                btn.setSelected(btn.getText().equals(String.valueOf(sceneNumber)));
            }
        }
    }

    /** Starts blinking the active scene button (toggle CSS classes). */
    public void startSceneBlinking() {
        stopSceneBlinking();
        final boolean[] state = {false};
        // Lies den BPM‐Wert aus dem Textfeld aus
        double bpm;
        try {
            bpm = Double.parseDouble(bpmField.getText());
        } catch (NumberFormatException ex) {
            bpm = 120; // Fallback
        }

        double intervalMs = 60000.0 / bpm;
        sceneBlinkTimeline = new Timeline(new KeyFrame(Duration.millis(intervalMs), e -> {

            ToggleButton active = findActiveSceneButton();
            if (active == null) {
                stopSceneBlinking();
                return;
            }

            for (Node node : sceneBar.getChildren()) {
                if (node instanceof ToggleButton) {
                    node.getStyleClass().removeAll("blink-phase1", "blink-phase2");
                }
            }

            state[0] = !state[0];
            active.getStyleClass().add(state[0] ? "blink-phase1" : "blink-phase2");
        }));
        sceneBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        sceneBlinkTimeline.play();
    }


    /** Stops blinking and clears blink-phase classes. */
    public void stopSceneBlinking() {
        if (sceneBlinkTimeline != null) {
            sceneBlinkTimeline.stop();
            sceneBlinkTimeline = null;
        }
        for (Node node : sceneBar.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) node;
                btn.getStyleClass().removeAll("blink-phase1", "blink-phase2");
            }
        }
    }

    private ToggleButton findActiveSceneButton() {
        for (Node node : sceneBar.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) node;
                if (btn.isSelected()) return btn;
            }
        }
        return null;
    }

    private void applyBlinkPhase(ToggleButton btn, boolean phase1) {
        btn.getStyleClass().removeAll("blink-phase1", "blink-phase2");
        btn.getStyleClass().add(phase1 ? "blink-phase1" : "blink-phase2");
    }
}
