// File: src/main/java/com/tobiasenger/daw/dawnana/model/Track.java
package com.tobiasenger.daw.dawnana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a track containing a sequence of samples.
 */
public class Track {
    private final String name;
    private final List<Sample> samples = new ArrayList<>();

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
}
