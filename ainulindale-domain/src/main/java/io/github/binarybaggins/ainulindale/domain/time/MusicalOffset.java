package io.github.binarybaggins.ainulindale.domain.time;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a musical offset as a rational number.
 * Provides methods for arithmetic operations and comparison of musical offsets.
 */
public record MusicalOffset(Rational value) implements Comparable<MusicalOffset> {
    public static final MusicalOffset ZERO = new MusicalOffset(Rational.ZERO);

    /**
     * Constructs a new musical offset with the specified rational value.
     *
     * @param value the rational value of the musical offset
     * @throws NullPointerException if the value is null
     */
    public MusicalOffset {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a musical offset with the specified rational value.
     *
     * @param value the rational value of the musical offset
     * @return a new musical offset with the specified value
     */
    public static MusicalOffset of(Rational value) {
        return new MusicalOffset(value);
    }

    /**
     * Creates a musical offset with the specified numerator and denominator.
     *
     * @param numerator the numerator of the rational value
     * @param denominator the denominator of the rational value
     * @return a new musical offset with the specified value
     */
    public static MusicalOffset of(long numerator, long denominator) {
        return of(Rational.of(numerator, denominator));
    }

    /**
     * Adds the specified musical offset to this musical offset.
     *
     * @param other the musical offset to add
     * @return a new musical offset representing the sum
     */
    public MusicalOffset plus(MusicalOffset other) {
        Objects.requireNonNull(other, "other must not be null");
        return of(value.plus(other.value));
    }

    /**
     * Subtracts the specified musical offset from this musical offset.
     *
     * @param other the musical offset to subtract
     * @return a new musical offset representing the difference
     */
    public MusicalOffset minus(MusicalOffset other) {
        Objects.requireNonNull(other, "other must not be null");
        return of(value.minus(other.value));
    }

    /**
     * Negates this musical offset.
     * @return a new musical offset representing the negation
     */
    public MusicalOffset negated() {
        return of(value.negated());
    }

    @Override
    public int compareTo(MusicalOffset other) {
        Objects.requireNonNull(other, "other must not be null");
        return value.compareTo(other.value);
    }
}
