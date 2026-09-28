package io.github.binarybaggins.ainulindale.domain.pitch;

import java.util.Objects;

/**
 * Represents a structural musical pitch defined by a diatonic step,
 * a pitch alteration, and a scientific pitch notation octave.
 */
public record Pitch(DiatonicStep step, PitchAlteration alteration, int octave) {
    /**
     * Constructs a pitch with the given structural components.
     *
     * @param step the diatonic step
     * @param alteration the chromatic alteration
     * @param octave the scientific pitch notation octave
     * @throws NullPointerException if step or alteration is null
     */
    public Pitch {
        Objects.requireNonNull(step, "step must not be null");
        Objects.requireNonNull(alteration, "alteration must not be null");
    }
}
