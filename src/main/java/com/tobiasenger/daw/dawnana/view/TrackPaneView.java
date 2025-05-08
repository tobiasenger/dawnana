// File: src/main/java/com/tobiasenger/daw/dawnana/view/TrackPaneView.java
package com.tobiasenger.daw.dawnana.view;

import com.tobiasenger.daw.dawnana.model.Sample;
import com.tobiasenger.daw.dawnana.model.Sequencer;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * UI helper for a single track row: name, load button, and pads.
 */
public class TrackPaneView {
    public final TextField nameField;
    public final Button loadBtn;
    public final List<ToggleButton> pads;
    private final int trackIndex;
    private final Sequencer sequencer;

    public TrackPaneView(int trackIndex, Sequencer sequencer) {
        this.trackIndex = trackIndex;
        this.sequencer = sequencer;
        nameField = new TextField("Track " + (trackIndex + 1));
        nameField.setPrefWidth(100);
        loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> loadSample());
        pads = new ArrayList<>();
        for (int i = 0; i < sequencer.getSteps(); i++) {
            ToggleButton tb = new ToggleButton();
            tb.setMinSize(30, 30);
            tb.getStyleClass().add("step-pad");
            pads.add(tb);
        }
    }

    private void loadSample() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            nameField.setText(file.getName());
            sequencer.getTracks().get(trackIndex)
                    .addSample(new Sample(file.toURI().toString()));
        }
    }

    public void clearCell(int step) {
        pads.get(step).getStyleClass().removeAll("active-step", "active-play");
    }

    public void highlightCell(int step) {
        ToggleButton pad = pads.get(step);
        if (pad.isSelected() && !sequencer.getTracks().get(trackIndex).getSamples().isEmpty()) {
            pad.getStyleClass().add("active-play");
        } else {
            pad.getStyleClass().add("active-step");
        }
    }

    public boolean isActive(int step) {
        return pads.get(step).isSelected()
                && !sequencer.getTracks().get(trackIndex).getSamples().isEmpty();
    }

    public void clearIndicators() {
        pads.forEach(p -> p.getStyleClass().removeAll("active-step", "active-play"));
    }

    public double getVolume() {
        return sequencer.getTracks().get(trackIndex).getVolume();
    }

    public void setVolume(double v) {
        sequencer.getTracks().get(trackIndex).setVolume(v);
    }
}
