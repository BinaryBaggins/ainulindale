package io.github.binarybaggins.ainulindale.domain.tempo;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a musical tempo in terms of whole notes per minute.
 */
public record Tempo(Rational wholeNotesPerMinute) {
    /**
     * Constructs a new Tempo instance with the specified number of whole notes per minute.
     *
     * @param wholeNotesPerMinute the number of whole notes per minute, must be strictly positive
     * @throws NullPointerException if wholeNotesPerMinute is null
     * @throws IllegalArgumentException if wholeNotesPerMinute is not strictly positive
     */
    public Tempo {
        Objects.requireNonNull(wholeNotesPerMinute, "wholeNotesPerMinute must not be null");
        if (wholeNotesPerMinute.signum() <= 0) {
            throw new IllegalArgumentException("Tempo must be strictly positive");
        }
    }

    /**
     * Creates a new Tempo instance with the specified number of whole notes per minute.
     *
     * @param wholeNotesPerMinute the number of whole notes per minute, must be strictly positive
     * @return a new Tempo instance
     * @throws NullPointerException if wholeNotesPerMinute is null
     * @throws IllegalArgumentException if wholeNotesPerMinute is not strictly positive
     */
    public static Tempo of(Rational wholeNotesPerMinute) {
        return new Tempo(wholeNotesPerMinute);
    }
}
