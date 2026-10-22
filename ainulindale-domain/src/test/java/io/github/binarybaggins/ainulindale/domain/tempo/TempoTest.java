package io.github.binarybaggins.ainulindale.domain.tempo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TempoTest {

    static Rational[] validRationals() {
        return new Rational[] {
            Rational.of(1, 1),
            Rational.of(2, 3),
            Rational.of(BigInteger.TEN.pow(100), BigInteger.ONE),
        };
    }

    @ParameterizedTest
    @MethodSource("validRationals")
    void createTempoAcceptsValidRational(Rational rational) {
        Tempo tempo = Tempo.of(rational);
        assertEquals(rational, tempo.wholeNotesPerMinute());
    }

    static Rational[] invalidRationals() {
        return new Rational[] { Rational.ZERO, Rational.of(-1, 1), Rational.of(2, -3) };
    }

    @ParameterizedTest
    @MethodSource("invalidRationals")
    void createTempoRejectsInvalidRational(Rational rational) {
        assertThrows(IllegalArgumentException.class, () -> Tempo.of(rational));
    }

    @Test
    void createTempoRejectsNullRational() {
        assertThrows(NullPointerException.class, () -> Tempo.of(null));
    }

    static Rational[][] equivalentRationals() {
        return new Rational[][] {
            { Rational.of(1, 2), Rational.of(2, 4) },
            { Rational.of(3, 4), Rational.of(6, 8) },
            { Rational.of(2, 3), Rational.of(4, 6) },
        };
    }

    @ParameterizedTest
    @MethodSource("equivalentRationals")
    void equivalentRationalsCreateEqualTempos(Rational rational, Rational equivalent) {
        var tempo1 = Tempo.of(rational);
        var tempo2 = Tempo.of(equivalent);
        assertEquals(tempo1, tempo2);
        assertEquals(tempo1.hashCode(), tempo2.hashCode());
    }
}
