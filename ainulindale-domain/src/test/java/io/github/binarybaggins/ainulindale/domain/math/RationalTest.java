package io.github.binarybaggins.ainulindale.domain.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class RationalTest {

    @Test
    void createdRationalsAreReduced() {
        Rational r = Rational.of(2, 4);

        assertEquals(BigInteger.ONE, r.numerator());
        assertEquals(BigInteger.TWO, r.denominator());
    }

    @Test
    void createdRationalsAreFullyReduced() {
        Rational r = Rational.of(6, 9);

        assertEquals(BigInteger.valueOf(2), r.numerator());
        assertEquals(BigInteger.valueOf(3), r.denominator());
    }

    @Test
    void zeroIsAlwaysCanonical() {
        Rational r = Rational.of(0, -37);

        assertEquals(BigInteger.ZERO, r.numerator());
        assertEquals(BigInteger.ONE, r.denominator());
    }

    @Test
    void createdRationalsWithNegativeDenominatorAreNormalized() {
        Rational r = Rational.of(1, -2);

        assertEquals(BigInteger.valueOf(-1), r.numerator());
        assertEquals(BigInteger.TWO, r.denominator());
    }

    @Test
    void createdRationalsWithNegativeNumeratorAreNormalized() {
        Rational r = Rational.of(-1, 2);

        assertEquals(BigInteger.valueOf(-1), r.numerator());
        assertEquals(BigInteger.TWO, r.denominator());
    }

    @Test
    void createdRationalsWithBothNumeratorAndDenominatorNegativeAreNormalized() {
        Rational r = Rational.of(-1, -2);

        assertEquals(BigInteger.ONE, r.numerator());
        assertEquals(BigInteger.TWO, r.denominator());
    }

    @Test
    void rationalsWithDenominatorZeroThrowException() {
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 0));
    }

    @Test
    void rationalsWithNumeratorNullThrowException() {
        assertThrows(NullPointerException.class, () -> Rational.of(null, BigInteger.ONE));
    }

    @Test
    void rationalsWithDenominatorNullThrowException() {
        assertThrows(NullPointerException.class, () -> Rational.of(BigInteger.ONE, null));
    }

    @Test
    void rationalsCompareByMathematicalValue() {
        assertTrue(Rational.of(1, 3).compareTo(Rational.of(1, 2)) < 0);
        assertEquals(0, Rational.of(2, 4).compareTo(Rational.of(1, 2)));
        assertTrue(Rational.of(3, 4).compareTo(Rational.of(2, 3)) > 0);
    }

    @Test
    void negativeRationalsCompareCorrectly() {
        assertTrue(Rational.of(-1, 2).compareTo(Rational.ZERO) < 0);
        assertTrue(Rational.of(-1, 3).compareTo(Rational.of(-1, 2)) > 0);
    }

    @Test
    void rationalsCanBeAdded() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 3).plus(Rational.of(1, 6)));
    }

    @Test
    void rationalsCanBeSubtracted() {
        assertEquals(Rational.of(-1, 6), Rational.of(1, 3).minus(Rational.of(1, 2)));
    }

    @Test
    void rationalsCanBeMultiplied() {
        assertEquals(Rational.of(3, 2), Rational.of(2, 3).times(Rational.of(9, 4)));
    }

    @Test
    void rationalsCanBeDivided() {
        assertEquals(Rational.of(3, 2), Rational.of(2, 3).dividedBy(Rational.of(4, 9)));
    }

    @Test
    void divisionByZeroRationalThrowsException() {
        assertThrows(ArithmeticException.class, () -> Rational.ONE.dividedBy(Rational.ZERO));
    }

    @Test
    void rationalsCanBeNegated() {
        assertEquals(Rational.of(-1, 2), Rational.of(1, 2).negated());
    }

    @Test
    void rationalAbsoluteValueIsNonNegative() {
        assertEquals(Rational.of(1, 2), Rational.of(-1, 2).absolute());
        assertEquals(Rational.of(1, 2), Rational.of(1, 2).absolute());
    }

    @Test
    void rationalSignCanBeQueried() {
        assertEquals(-1, Rational.of(-1, 2).signum());
        assertEquals(0, Rational.ZERO.signum());
        assertEquals(1, Rational.of(1, 2).signum());
    }

    @Test
    void zeroCanBeDetected() {
        assertTrue(Rational.ZERO.isZero());
        assertFalse(Rational.ONE.isZero());
    }
}
