package io.github.binarybaggins.ainulindale.domain.time;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a musical duration as a <code>non-negative</code> rational number.
 * Provides methods for arithmetic operations and comparison of musical durations.
 */
public record MusicalDuration(Rational value) implements Comparable<MusicalDuration> {
    public static final MusicalDuration ZERO = new MusicalDuration(Rational.ZERO);

    /**
     * Constructs a new musical duration with the specified rational value.
     *
     * @param value the rational value of the musical duration
     * @throws NullPointerException if the value is null
     * @throws IllegalArgumentException if the value is negative
     */
    public MusicalDuration {
        Objects.requireNonNull(value, "value");

        if (value.signum() < 0) {
            throw new IllegalArgumentException("Musical duration must not be negative");
        }
    }

    /**
     * Creates a musical duration with the specified rational value.
     *
     * @param value the rational value of the musical duration
     * @return a new musical duration with the specified value
     */
    public static MusicalDuration of(Rational value) {
        return new MusicalDuration(value);
    }

    /**
     * Creates a musical duration with the specified numerator and denominator.
     *
     * @param numerator the numerator of the rational value
     * @param denominator the denominator of the rational value
     * @return a new musical duration with the specified value
     */
    public static MusicalDuration of(long numerator, long denominator) {
        return of(Rational.of(numerator, denominator));
    }

    /**
     * Adds the specified musical duration to this musical duration.
     *
     * @param other the musical duration to add
     * @return a new musical duration representing the sum
     */
    public MusicalDuration plus(MusicalDuration other) {
        Objects.requireNonNull(other, "other");

        return of(value.plus(other.value));
    }

    /**
     * Subtracts the specified musical duration from this musical duration.
     *
     * @param other the musical duration to subtract
     * @return a new musical offset representing the difference
     */
    public MusicalOffset minus(MusicalDuration other) {
        Objects.requireNonNull(other, "other");

        return MusicalOffset.of(value.minus(other.value));
    }

    @Override
    public int compareTo(MusicalDuration other) {
        Objects.requireNonNull(other, "other");

        return value.compareTo(other.value);
    }
}
