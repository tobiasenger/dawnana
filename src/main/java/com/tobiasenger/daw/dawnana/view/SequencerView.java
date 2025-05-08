// File: src/main/java/com/tobiasenger/daw/dawnana/view/SequencerView.java
package com.tobiasenger.daw.dawnana.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * View for the sequencer grid and track controls.
 */
public class SequencerView {
    public final Button addTrackBtn = new Button("+ Add Track");
    private final VBox container = new VBox();
    private final GridPane grid = new GridPane();

    public SequencerView() {
        setupLayout();
    }

    private void setupLayout() {
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        HBox bottom = new HBox(addTrackBtn);
        bottom.setPadding(new Insets(10));

        container.getChildren().addAll(grid, bottom);
        VBox.setVgrow(grid, Priority.ALWAYS);
    }

    /**
     * Rebuilds grid with given track panes and step count.
     */
    public void rebuildGrid(List<TrackPaneView> panes, int steps) {
        grid.getChildren().clear();
        for (int r = 0; r < panes.size(); r++) {
            TrackPaneView tpv = panes.get(r);
            grid.add(tpv.nameField, 0, r);
            grid.add(tpv.loadBtn,   1, r);
            for (int c = 0; c < steps; c++) {
                ToggleButton pad = tpv.pads.get(c);
                if (c > 0 && c % 4 == 0) pad.getStyleClass().add("step-separator");
                grid.add(pad, c + 2, r);
            }
        }
    }

    public VBox getView() {
        return container;
    }
}
