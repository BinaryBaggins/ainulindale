package io.github.binarybaggins.ainulindale.domain.math;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a rational number with a numerator and a denominator. The rational number is always kept in its reduced form, and the denominator is always positive.
 * Provides methods for arithmetic operations, comparison, and creation of rational numbers.
 * @param numerator
 * @param denominator
 */
public record Rational(BigInteger numerator, BigInteger denominator) implements Comparable<Rational> {
    public static final Rational ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE);

    public static final Rational ONE = new Rational(BigInteger.ONE, BigInteger.ONE);

    public static final Rational MINUS_ONE = new Rational(BigInteger.ONE.negate(), BigInteger.ONE);

    /**
     * Constructs a new rational number with the specified numerator and denominator.
     * @param numerator the numerator of the rational number
     * @param denominator the denominator of the rational number
     * @throws NullPointerException if the numerator or denominator is null
     * @throws ArithmeticException if the denominator is zero
     */
    public Rational {
        Objects.requireNonNull(numerator, "Numerator cannot be null");
        Objects.requireNonNull(denominator, "Denominator cannot be null");

        if (denominator.signum() == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }

        if (numerator.signum() == 0) {
            numerator = BigInteger.ZERO;
            denominator = BigInteger.ONE;
        } else {
            BigInteger gcd = numerator.gcd(denominator);
            numerator = numerator.divide(gcd);
            denominator = denominator.divide(gcd);

            if (denominator.signum() < 0) {
                numerator = numerator.negate();
                denominator = denominator.negate();
            }
        }
    }

    /**
     * Creates a new rational number with the specified numerator and denominator.
     * @param numerator the numerator of the rational number
     * @param denominator the denominator of the rational number
     * @return a new rational number with the specified numerator and denominator
     */
    public static Rational of(BigInteger numerator, BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    /**
     * Creates a new rational number with the specified numerator and denominator as long values.
     * @param numerator the numerator of the rational number as a long value
     * @param denominator the denominator of the rational number as a long value
     * @return a new rational number with the specified numerator and denominator as long values
     */
    public static Rational of(long numerator, long denominator) {
        return of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    /**
     * Creates a new rational number with the specified integer value.
     * @param integer the integer value to create a rational number from
     * @return a new rational number representing the specified integer value
     */
    public static Rational of(long integer) {
        return of(integer, 1);
    }

    @Override
    public int compareTo(Rational other) {
        Objects.requireNonNull(other, "Other rational cannot be null");
        return this.numerator.multiply(other.denominator).compareTo(other.numerator.multiply(this.denominator));
    }

    /**
     * Returns the result of adding another rational number to this rational number.
     * @param other the rational number to add
     * @return the result of adding the specified rational number to this rational number
     */
    public Rational plus(Rational other) {
        Objects.requireNonNull(other, "Other rational cannot be null");
        return of(
            numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
            denominator.multiply(other.denominator)
        );
    }

    /**
     * Returns the result of subtracting another rational number from this rational number.
     * @param other the rational number to subtract
     * @return the result of subtracting the specified rational number from this rational number
     */
    public Rational minus(Rational other) {
        Objects.requireNonNull(other, "Other rational cannot be null");
        return of(
            numerator.multiply(other.denominator).subtract(other.numerator.multiply(denominator)),
            denominator.multiply(other.denominator)
        );
    }

    /**
     * Returns the result of multiplying this rational number by another rational number.
     * @param other the rational number to multiply by
     * @return the result of multiplying this rational number by the specified rational number
     */
    public Rational times(Rational other) {
        Objects.requireNonNull(other, "Other rational cannot be null");
        return of(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
    }

    /**
     * Returns the result of dividing this rational number by another rational number.
     * @param other the rational number to divide by
     * @return the result of dividing this rational number by the specified rational number
     * @throws ArithmeticException if the specified rational number is zero
     * @see #isZero()
     */
    public Rational dividedBy(Rational other) {
        Objects.requireNonNull(other, "Other rational cannot be null");
        if (other.isZero()) {
            throw new ArithmeticException("Division by zero");
        }
        return of(numerator.multiply(other.denominator), denominator.multiply(other.numerator));
    }

    /**
     * Returns the negation of this rational number.
     * @return the negated rational number
     */
    public Rational negated() {
        return of(numerator.negate(), denominator);
    }

    /**
     * Returns the absolute value of this rational number.
     * @return the absolute value of this rational number
     */
    public Rational absolute() {
        return signum() < 0 ? negated() : this;
    }

    /**
     * Returns the signum of this rational number.
     * @return -1 if this rational number is negative, 0 if it is zero, 1 if it is positive
     */
    public int signum() {
        return numerator.signum();
    }

    /**
     * Returns whether this rational number is zero.
     * @return true if this rational number is zero, false otherwise
     */
    public boolean isZero() {
        return numerator.signum() == 0;
    }
}
