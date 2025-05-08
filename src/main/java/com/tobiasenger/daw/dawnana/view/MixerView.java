// File: src/main/java/com/tobiasenger/daw/dawnana/view/MixerView.java
package com.tobiasenger.daw.dawnana.view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Displays horizontal volume sliders for each track with value display.
 */
public class MixerView {
    private final VBox container = new VBox(10);

    public MixerView() {
        container.setPadding(new Insets(10));
    }

    /**
     * Builds horizontal sliders for each track pane with track name and value (2 decimals) beneath.
     */
    public void buildMixer(List<TrackPaneView> panes) {
        container.getChildren().clear();
        for (TrackPaneView tpv : panes) {
            // Horizontal slider
            Slider vol = new Slider(0, 1, tpv.getVolume());
            vol.setOrientation(Orientation.HORIZONTAL);
            vol.setShowTickLabels(true);
            vol.setShowTickMarks(true);
            vol.setBlockIncrement(0.01);

            // Value label with 2 decimals
            Label valueLabel = new Label(String.format("%.2f", tpv.getVolume()));
            vol.valueProperty().addListener((obs, oldV, newV) -> {
                double v = Math.round(newV.doubleValue() * 100.0) / 100.0;
                tpv.setVolume(v);
                valueLabel.setText(String.format("%.2f", v));
            });

            // Track name label
            Label nameLabel = new Label(tpv.nameField.getText());

            // Container for this track
            VBox trackBox = new VBox(5, vol, nameLabel, valueLabel);
            trackBox.setPadding(new Insets(5));
            container.getChildren().add(trackBox);
        }
    }

    public VBox getView() {
        return container;
    }
}
