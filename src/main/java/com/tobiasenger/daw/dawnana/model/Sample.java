// File: src/main/java/com/tobiasenger/daw/dawnana/model/Sample.java
package com.tobiasenger.daw.dawnana.model;

import javafx.scene.media.AudioClip;

/**
 * Represents a single audio sample with volume control.
 */
public class Sample {
    private final String filePath;
    private final AudioClip clip;

    public Sample(String filePath) {
        this.filePath = filePath;
        this.clip = new AudioClip(filePath);
    }

    /**
     * Play the sample at given volume (0.0 - 1.0).
     */
    public void play(double volume) {
        clip.play(volume);
    }

    public String getFilePath() {
        return filePath;
    }
}
