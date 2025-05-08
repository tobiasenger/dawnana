// File: src/main/java/com/tobiasenger/daw/dawnana/model/Track.java
package com.tobiasenger.daw.dawnana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a track containing samples and volume.
 */
public class Track {
    private final String name;
    private final List<Sample> samples = new ArrayList<>();
    private double volume = 1.0;

    public Track(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSample(Sample sample) {
        samples.add(sample);
    }

    public List<Sample> getSamples() {
        return List.copyOf(samples);
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }
}
