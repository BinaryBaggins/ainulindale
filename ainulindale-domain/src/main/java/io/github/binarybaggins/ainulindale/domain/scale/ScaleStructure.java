package io.github.binarybaggins.ainulindale.domain.scale;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.List;
import java.util.Objects;

/**
 * Represents an immutable, ordered scale structure relative to
 * a tonal center.
 *
 * <p>The structure describes exactly one ascending octave period,
 * including the root and excluding its octave repetition.
 *
 * @param degrees the ordered scale degrees
 */
public record ScaleStructure(List<ScaleDegree> degrees) {
    private static final Rational OCTAVE = Rational.of(12);

    /**
     * Constructs a scale structure.
     *
     * @throws NullPointerException if degrees or an element is null
     * @throws IllegalArgumentException if the structure violates
     *         its ordering, root, or octave-period invariants
     */
    public ScaleStructure {
        Objects.requireNonNull(degrees, "degrees must not be null");

        degrees = List.copyOf(degrees);

        if (degrees.isEmpty()) {
            throw new IllegalArgumentException("ScaleStructure must contain at least one degree");
        }

        ScaleDegree first = degrees.get(0);

        if (first.diatonicOffset() != 0 || !first.chromaticOffset().isZero()) {
            throw new IllegalArgumentException("First scale degree must be (0, 0)");
        }

        ScaleDegree previous = null;

        for (ScaleDegree degree : degrees) {
            if (degree.diatonicOffset() >= 7) {
                throw new IllegalArgumentException("Diatonic offset must be less than 7");
            }

            if (degree.chromaticOffset().signum() < 0 || degree.chromaticOffset().compareTo(OCTAVE) >= 0) {
                throw new IllegalArgumentException("Chromatic offset must be within [0, 12)");
            }

            if (previous != null) {
                if (degree.diatonicOffset() <= previous.diatonicOffset()) {
                    throw new IllegalArgumentException("Diatonic offsets must strictly increase");
                }

                if (degree.chromaticOffset().compareTo(previous.chromaticOffset()) <= 0) {
                    throw new IllegalArgumentException("Chromatic offsets must strictly increase");
                }
            }

            previous = degree;
        }
    }

    /**
     * Creates a scale structure from ordered scale degrees.
     *
     * @param degrees the ordered scale degrees
     * @return the immutable scale structure
     */
    public static ScaleStructure of(List<ScaleDegree> degrees) {
        return new ScaleStructure(degrees);
    }
}
