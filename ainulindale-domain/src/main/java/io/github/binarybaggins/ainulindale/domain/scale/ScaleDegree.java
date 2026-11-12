package io.github.binarybaggins.ainulindale.domain.scale;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a scale degree relative to a tonal center.
 *
 * @param diatonicOffset the nonnegative diatonic displacement
 * @param chromaticOffset the chromatic displacement in
 *                        abstract semitone units
 */
public record ScaleDegree(int diatonicOffset, Rational chromaticOffset) {
    /**
     * Constructs a scale degree.
     *
     * @throws IllegalArgumentException if diatonicOffset is negative
     * @throws NullPointerException if chromaticOffset is null
     */
    public ScaleDegree {
        Objects.requireNonNull(chromaticOffset, "chromaticOffset must not be null");

        if (diatonicOffset < 0) {
            throw new IllegalArgumentException("diatonicOffset must not be negative");
        }
    }

    /**
     * Creates a scale degree from its structural offsets.
     *
     * @param diatonicOffset the nonnegative diatonic displacement
     * @param chromaticOffset the chromatic displacement
     * @return the scale degree
     */
    public static ScaleDegree of(int diatonicOffset, Rational chromaticOffset) {
        return new ScaleDegree(diatonicOffset, chromaticOffset);
    }
}
