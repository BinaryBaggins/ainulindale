package io.github.binarybaggins.ainulindale.domain.pitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class PitchIntervalTest {

    @Test
    void zeroRepresentsIdentityInterval() {
        assertEquals(0, PitchInterval.ZERO.diatonicSteps());
        assertEquals(Rational.ZERO, PitchInterval.ZERO.chromaticOffset());
        assertEquals(PitchInterval.of(0, Rational.ZERO), PitchInterval.ZERO);
    }

    @Test
    void preservesPositiveDisplacements() {
        PitchInterval interval = PitchInterval.of(7, Rational.of(12));

        assertEquals(7, interval.diatonicSteps());
        assertEquals(Rational.of(12), interval.chromaticOffset());
    }

    @Test
    void preservesNegativeDisplacements() {
        PitchInterval interval = PitchInterval.of(-7, Rational.of(-12));

        assertEquals(-7, interval.diatonicSteps());
        assertEquals(Rational.of(-12), interval.chromaticOffset());
    }

    @Test
    void preservesRationalChromaticDisplacement() {
        PitchInterval interval = PitchInterval.of(1, Rational.of(3, 2));

        assertEquals(1, interval.diatonicSteps());
        assertEquals(Rational.of(3, 2), interval.chromaticOffset());
    }

    @Test
    void allowsIndependentDisplacementSigns() {
        PitchInterval interval = PitchInterval.of(-1, Rational.of(1, 2));

        assertEquals(-1, interval.diatonicSteps());
        assertEquals(Rational.of(1, 2), interval.chromaticOffset());
    }

    @Test
    void distinguishesEnharmonicIntervals() {
        PitchInterval augmentedUnison = PitchInterval.of(0, Rational.of(1));

        PitchInterval minorSecond = PitchInterval.of(1, Rational.of(1));

        assertNotEquals(augmentedUnison, minorSecond);
    }

    @Test
    void distinguishesDifferentChromaticOffsets() {
        PitchInterval minorSecond = PitchInterval.of(1, Rational.of(1));

        PitchInterval majorSecond = PitchInterval.of(1, Rational.of(2));

        assertNotEquals(minorSecond, majorSecond);
    }

    @Test
    void structurallyEqualIntervalsHaveEqualHashCodes() {
        PitchInterval first = PitchInterval.of(2, Rational.of(6, 2));

        PitchInterval second = PitchInterval.of(2, Rational.of(3));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void constructorAndFactoryProduceEqualIntervals() {
        PitchInterval direct = new PitchInterval(4, Rational.of(7));

        PitchInterval factory = PitchInterval.of(4, Rational.of(7));

        assertEquals(direct, factory);
    }

    @Test
    void acceptsFullIntegerRangeWithoutArithmetic() {
        PitchInterval minimum = PitchInterval.of(Integer.MIN_VALUE, Rational.ZERO);

        PitchInterval maximum = PitchInterval.of(Integer.MAX_VALUE, Rational.ZERO);

        assertEquals(Integer.MIN_VALUE, minimum.diatonicSteps());
        assertEquals(Integer.MAX_VALUE, maximum.diatonicSteps());
    }

    @Test
    void rejectsNullChromaticOffset() {
        assertThrows(NullPointerException.class, () -> new PitchInterval(0, null));

        assertThrows(NullPointerException.class, () -> PitchInterval.of(0, null));
    }

    @Test
    void negatedReversesBothDisplacements() {
        PitchInterval interval = PitchInterval.of(-1, Rational.of(-2));

        assertEquals(PitchInterval.of(1, Rational.of(2)), interval.negated());
    }

    @Test
    void doubleNegationRestoresOriginalInterval() {
        PitchInterval interval = PitchInterval.of(2, Rational.of(3, 2));

        assertEquals(interval, interval.negated().negated());
    }

    @Test
    void negatingMinimumDiatonicDisplacementThrows() {
        PitchInterval interval = PitchInterval.of(Integer.MIN_VALUE, Rational.ZERO);

        assertThrows(ArithmeticException.class, interval::negated);
    }
}
