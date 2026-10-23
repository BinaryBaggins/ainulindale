package io.github.binarybaggins.ainulindale.domain.pitch;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a directed musical interval using diatonic
 * displacement and chromatic displacement in abstract
 * semitone units.
 *
 * @param diatonicSteps the signed diatonic displacement
 * @param chromaticOffset the signed chromatic displacement
 */
public record PitchInterval(int diatonicSteps, Rational chromaticOffset) {
    /**
     * Represents an interval with no displacement.
     */
    public static final PitchInterval ZERO = new PitchInterval(0, Rational.ZERO);

    /**
     * Creates a pitch interval.
     *
     * @throws NullPointerException if chromaticOffset is null
     */
    public PitchInterval {
        Objects.requireNonNull(chromaticOffset, "chromaticOffset must not be null");
    }

    /**
     * Creates an interval with the specified displacements.
     *
     * @param diatonicSteps the signed diatonic displacement
     * @param chromaticOffset the signed chromatic displacement
     * @return the interval
     */
    public static PitchInterval of(int diatonicSteps, Rational chromaticOffset) {
        return new PitchInterval(diatonicSteps, chromaticOffset);
    }
}
