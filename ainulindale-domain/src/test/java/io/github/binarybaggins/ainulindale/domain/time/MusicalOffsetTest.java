package io.github.binarybaggins.ainulindale.domain.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class MusicalOffsetTest {

    @Test
    void zeroIsValid() {
        assertEquals(MusicalOffset.ZERO, MusicalOffset.of(0, 1));
    }

    @Test
    void positiveOffsetsAreValid() {
        assertEquals(Rational.of(1, 4), MusicalOffset.of(1, 4).value());
    }

    @Test
    void negativeOffsetsAreValid() {
        assertEquals(Rational.of(-1, 4), MusicalOffset.of(-1, 4).value());
    }

    @Test
    void nullValueIsRejected() {
        assertThrows(NullPointerException.class, () -> MusicalOffset.of((Rational) null));
    }

    @Test
    void offsetsCanBeAdded() {
        assertEquals(MusicalOffset.of(1, 2), MusicalOffset.of(1, 3).plus(MusicalOffset.of(1, 6)));
    }

    @Test
    void offsetsCanBeSubtracted() {
        assertEquals(MusicalOffset.of(-1, 6), MusicalOffset.of(1, 3).minus(MusicalOffset.of(1, 2)));
    }

    @Test
    void offsetsCanBeNegated() {
        assertEquals(MusicalOffset.of(-1, 4), MusicalOffset.of(1, 4).negated());
    }

    @Test
    void offsetsAreComparable() {
        assertTrue(MusicalOffset.of(-1, 4).compareTo(MusicalOffset.ZERO) < 0);
        assertTrue(MusicalOffset.of(1, 4).compareTo(MusicalOffset.ZERO) > 0);
    }
}
