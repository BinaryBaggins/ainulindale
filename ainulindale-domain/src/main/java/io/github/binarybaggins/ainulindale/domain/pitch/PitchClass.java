package io.github.binarybaggins.ainulindale.domain.pitch;

import java.util.Objects;

public record PitchClass(DiatonicStep diatonicStep, PitchAlteration pitchAlteration) {
    /**
     * Constructs a new PitchClass with the specified diatonic step and pitch alteration.
     *
     * @param diatonicStep the diatonic step, must not be null
     * @param pitchAlteration the pitch alteration, must not be null
     * @throws NullPointerException if diatonicStep or pitchAlteration is null
     */
    public PitchClass {
        Objects.requireNonNull(diatonicStep, "diatonicStep must not be null");
        Objects.requireNonNull(pitchAlteration, "pitchAlteration must not be null");
    }

    /**
     * Creates a new PitchClass with the specified diatonic step and pitch alteration.
     *
     * @param diatonicStep the diatonic step, must not be null
     * @param pitchAlteration the pitch alteration, must not be null
     * @return a new PitchClass instance
     * @throws NullPointerException if diatonicStep or pitchAlteration is null
     */
    public static PitchClass of(DiatonicStep diatonicStep, PitchAlteration pitchAlteration) {
        return new PitchClass(diatonicStep, pitchAlteration);
    }
}
