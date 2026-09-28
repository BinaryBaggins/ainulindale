package io.github.binarybaggins.ainulindale.domain.pitch;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a pitch alteration in music, such as a sharp, flat, or natural.
 * The alteration is expressed as a rational number, where
 * positive values indicate upward alterations (sharps) and
 * negative values indicate downward alterations (flats).
 */
public record PitchAlteration(Rational value) {
    /**
     * Represents a natural pitch alteration (no sharp or flat).
     * This is represented by a rational value of zero.
     */
    public static final PitchAlteration NATURAL = new PitchAlteration(Rational.ZERO);

    /**
     * Constructs a new PitchAlteration with the given rational value.
     * @param value the rational value representing the pitch alteration
     * @throws NullPointerException if the value is null
     */
    public PitchAlteration {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a pitch alteration from a rational value.
     * @param value the rational value representing the pitch alteration
     * @return a new PitchAlteration representing the given rational value
     */
    public static PitchAlteration of(Rational value) {
        return new PitchAlteration(value);
    }

    /**
     * Creates a pitch alteration from a rational value.
     * @param numerator the numerator of the rational value
     * @param denominator the denominator of the rational value
     * @return a new PitchAlteration representing the given rational value
     */
    public static PitchAlteration of(long numerator, long denominator) {
        return of(Rational.of(numerator, denominator));
    }

    /**
     * Creates a pitch alteration from an integer value.
     * @param integer the integer value representing the pitch alteration
     * @return a new PitchAlteration representing the given integer value
     */
    public static PitchAlteration of(long integer) {
        return of(Rational.of(integer));
    }
}
