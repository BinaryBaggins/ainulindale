package io.github.binarybaggins.ainulindale.domain.time;

import java.util.Objects;

/**
 * Represents a range within a musical score, defined by a starting position and a duration.
 */
public record ScoreRange(ScorePosition start, MusicalDuration duration) {
    /**
     * Constructs a new ScoreRange, ensuring that neither the start position nor the duration is null.
     *
     * @param start the starting position of the range
     * @param duration the duration of the range
     * @throws NullPointerException if either start or duration is null
     */
    public ScoreRange {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
    }

    /**
     * Returns the ending position of the range, calculated as the start position plus the duration.
     * @return the ending position of the range
     */
    public ScorePosition end() {
        return start.plus(duration);
    }
}
