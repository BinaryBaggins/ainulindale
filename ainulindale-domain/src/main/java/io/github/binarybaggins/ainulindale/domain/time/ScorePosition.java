package io.github.binarybaggins.ainulindale.domain.time;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a position within a musical score as a rational number.
 * Provides methods for creating and manipulating score positions within a musical composition.
 */
public record ScorePosition(Rational value) implements Comparable<ScorePosition> {
    public static final ScorePosition ZERO = new ScorePosition(Rational.ZERO);

    /**
     * Constructs a new ScorePosition with the given Rational value.
     *
     * @param value the Rational value representing the score position
     * @throws NullPointerException if the value is null
     * @throws IllegalArgumentException if the value is negative
     */
    public ScorePosition {
        Objects.requireNonNull(value, "value must not be null");

        if (value.signum() < 0) {
            throw new IllegalArgumentException("Score position must not be negative");
        }
    }

    /**
     * Creates a new ScorePosition instance with the given Rational value.
     *
     * @param value the Rational value representing the score position
     * @return a new ScorePosition instance with the given Rational value
     */
    public static ScorePosition of(Rational value) {
        return new ScorePosition(value);
    }

    /**
     * Creates a new ScorePosition instance with the given numerator and denominator.
     *
     * @param numerator the numerator of the Rational value representing the score position
     * @param denominator the denominator of the Rational value representing the score position
     * @return a new ScorePosition instance with the given numerator and denominator
     */
    public static ScorePosition of(long numerator, long denominator) {
        return of(Rational.of(numerator, denominator));
    }

    /**
     * Creates a new ScorePosition instance by adding the given MusicalDuration to this ScorePosition.
     *
     * @param duration the MusicalDuration to add to this ScorePosition
     * @return a new ScorePosition instance representing the sum of this ScorePosition and the given MusicalDuration
     * @throws NullPointerException if the duration is null
     */
    public ScorePosition plus(MusicalDuration duration) {
        Objects.requireNonNull(duration, "duration must not be null");
        return of(value.plus(duration.value()));
    }

    /**
     * Creates a new ScorePosition instance by adding the given MusicalOffset to this ScorePosition.
     *
     * @param offset the MusicalOffset to add to this ScorePosition
     * @return a new ScorePosition instance representing the sum of this ScorePosition and the given MusicalOffset
     * @throws NullPointerException if the offset is null
     */
    public ScorePosition plus(MusicalOffset offset) {
        Objects.requireNonNull(offset, "offset must not be null");
        return of(value.plus(offset.value()));
    }

    /**
     * Creates a new MusicalOffset instance by subtracting the given ScorePosition from this ScorePosition.
     *
     * @param other the ScorePosition to subtract from this ScorePosition
     * @return a new MusicalOffset instance representing the difference between this ScorePosition and the given ScorePosition
     * @throws NullPointerException if the other ScorePosition is null
     */
    public MusicalOffset minus(ScorePosition other) {
        Objects.requireNonNull(other, "other must not be null");
        return MusicalOffset.of(value.minus(other.value));
    }

    @Override
    public int compareTo(ScorePosition other) {
        Objects.requireNonNull(other, "other must not be null");
        return value.compareTo(other.value);
    }
}
