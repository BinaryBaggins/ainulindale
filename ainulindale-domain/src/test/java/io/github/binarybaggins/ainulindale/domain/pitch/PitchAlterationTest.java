package io.github.binarybaggins.ainulindale.domain.pitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class PitchAlterationTest {

    @Test
    void naturalIsZero() {
        assertEquals(PitchAlteration.of(Rational.ZERO), PitchAlteration.NATURAL);
    }

    @Test
    void positiveAlterationIsValid() {
        assertEquals(Rational.of(1), PitchAlteration.of(1).value());
    }

    @Test
    void negativeAlterationIsValid() {
        assertEquals(Rational.of(-1, 2), PitchAlteration.of(-1, 2).value());
    }

    @Test
    void microtonalAlterationIsValid() {
        assertEquals(Rational.of(1, 2), PitchAlteration.of(1, 2).value());
    }

    @Test
    void nullValueIsRejected() {
        assertThrows(NullPointerException.class, () -> PitchAlteration.of((Rational) null));
    }
}
