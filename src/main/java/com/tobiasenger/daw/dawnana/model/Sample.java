// File: src/main/java/com/tobiasenger/daw/dawnana/model/Sample.java
package com.tobiasenger.daw.dawnana.model;

import javafx.scene.media.AudioClip;

/**
 * Represents a single audio sample.
 */
public class Sample {
    private final String filePath;
    private AudioClip clip;

    public Sample(String filePath) {
        this.filePath = filePath;
        load();
    }

    private void load() {
        clip = new AudioClip(filePath);
    }

    public void play() {
        if (clip != null) clip.play();
    }

    public String getFilePath() {
        return filePath;
    }
}
