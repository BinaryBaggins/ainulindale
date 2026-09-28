package io.github.binarybaggins.ainulindale.domain.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class ScorePositionTest {

    @Test
    void zeroIsValid() {
        assertEquals(ScorePosition.ZERO, ScorePosition.of(0, 1));
    }

    @Test
    void positivePositionsAreValid() {
        assertEquals(Rational.of(1, 4), ScorePosition.of(1, 4).value());
    }

    @Test
    void negativePositionsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScorePosition.of(-1, 4));
    }

    @Test
    void nullValueIsRejected() {
        assertThrows(NullPointerException.class, () -> ScorePosition.of((Rational) null));
    }

    @Test
    void durationCanBeAdded() {
        assertEquals(ScorePosition.of(3, 8), ScorePosition.of(1, 4).plus(MusicalDuration.of(1, 8)));
    }

    @Test
    void offsetCanBeAdded() {
        assertEquals(ScorePosition.of(1, 8), ScorePosition.of(1, 4).plus(MusicalOffset.of(-1, 8)));
    }

    @Test
    void offsetCannotMovePositionBeforeZero() {
        assertThrows(IllegalArgumentException.class, () -> ScorePosition.ZERO.plus(MusicalOffset.of(-1, 8)));
    }

    @Test
    void positionDifferenceProducesOffset() {
        assertEquals(MusicalOffset.of(-1, 4), ScorePosition.of(1, 4).minus(ScorePosition.of(1, 2)));
    }

    @Test
    void positionsAreComparable() {
        assertTrue(ScorePosition.of(1, 4).compareTo(ScorePosition.of(1, 2)) < 0);
    }
}
