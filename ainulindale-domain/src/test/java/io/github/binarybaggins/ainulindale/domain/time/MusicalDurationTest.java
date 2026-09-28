package io.github.binarybaggins.ainulindale.domain.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class MusicalDurationTest {

    @Test
    void zeroIsValid() {
        assertEquals(MusicalDuration.ZERO, MusicalDuration.of(0, 1));
    }

    @Test
    void positiveDurationsAreValid() {
        assertEquals(Rational.of(1, 4), MusicalDuration.of(1, 4).value());
    }

    @Test
    void negativeDurationsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> MusicalDuration.of(-1, 4));
    }

    @Test
    void nullValueIsRejected() {
        assertThrows(NullPointerException.class, () -> MusicalDuration.of((Rational) null));
    }

    @Test
    void durationsCanBeAdded() {
        assertEquals(MusicalDuration.of(1, 2), MusicalDuration.of(1, 3).plus(MusicalDuration.of(1, 6)));
    }

    @Test
    void durationDifferenceProducesOffset() {
        assertEquals(MusicalOffset.of(-1, 6), MusicalDuration.of(1, 3).minus(MusicalDuration.of(1, 2)));
    }

    @Test
    void durationsAreComparable() {
        assertTrue(MusicalDuration.of(1, 4).compareTo(MusicalDuration.of(1, 2)) < 0);
    }
}
