package io.github.binarybaggins.ainulindale.domain.scale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class ScaleDegreeTest {

    @Test
    void preservesStructuralComponents() {
        ScaleDegree degree = ScaleDegree.of(2, Rational.of(4));

        assertEquals(2, degree.diatonicOffset());
        assertEquals(Rational.of(4), degree.chromaticOffset());
    }

    @Test
    void zeroOffsetsAreValid() {
        ScaleDegree degree = ScaleDegree.of(0, Rational.ZERO);

        assertEquals(0, degree.diatonicOffset());
        assertEquals(Rational.ZERO, degree.chromaticOffset());
    }

    @Test
    void negativeDiatonicOffsetIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleDegree.of(-1, Rational.ZERO));
    }

    @Test
    void nullChromaticOffsetIsRejected() {
        assertThrows(NullPointerException.class, () -> ScaleDegree.of(0, null));
    }

    @Test
    void rationalChromaticOffsetsAreSupported() {
        ScaleDegree degree = ScaleDegree.of(1, Rational.of(3, 2));

        assertEquals(Rational.of(3, 2), degree.chromaticOffset());
    }

    @Test
    void structurallyEqualDegreesAreEqual() {
        ScaleDegree first = ScaleDegree.of(2, Rational.of(4));
        ScaleDegree second = ScaleDegree.of(2, Rational.of(8, 2));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentDiatonicOffsetsRemainDistinct() {
        ScaleDegree first = ScaleDegree.of(1, Rational.of(2));
        ScaleDegree second = ScaleDegree.of(2, Rational.of(2));

        assertNotEquals(first, second);
    }

    @Test
    void differentChromaticOffsetsRemainDistinct() {
        ScaleDegree first = ScaleDegree.of(1, Rational.of(2));
        ScaleDegree second = ScaleDegree.of(1, Rational.of(3));

        assertNotEquals(first, second);
    }

    @Test
    void negativeChromaticOffsetsAreRepresentable() {
        ScaleDegree degree = ScaleDegree.of(1, Rational.of(-1));

        assertEquals(Rational.of(-1), degree.chromaticOffset());
    }

    @Test
    void offsetsBeyondOneOctaveAreRepresentable() {
        ScaleDegree degree = ScaleDegree.of(7, Rational.of(12));

        assertEquals(7, degree.diatonicOffset());
        assertEquals(Rational.of(12), degree.chromaticOffset());
    }
}
