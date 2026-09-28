# ADR-0007 – Exact Rational Score Time

**Status:** Accepted

## Context

Floating-point values and global tick grids create long-term problems with:

- Tuplets
- ABC note lengths
- Quantization
- Comparisons
- Collision detection
- MIDI conversion

## Decision

Canonical musical time uses exact, normalized rational values.

The base unit is the whole note.

Semantically distinct types are used:

```text
ScorePosition
MusicalDuration
MusicalOffset
```

### Canonical Rational Representation

`Rational` uses `BigInteger numerator` and `BigInteger denominator`. It represents all of ℚ, including negative values, rather than only nonnegative musical time.

- The denominator must never be zero.
- After normalization, the denominator is positive and the sign is in the numerator.
- Numerator and denominator are fully reduced by their greatest common divisor.
- Zero has exactly the canonical representation `0/1`.
- Canonical values remain exact; floating-point representation is excluded.

A zero denominator or division by zero is a programming-contract violation, not an expected domain error. This decision does not prescribe a concrete exception type.

### Time Invariants and Algebra

`ScorePosition >= 0` and `MusicalDuration >= 0`. `MusicalDuration.ZERO` is valid at the primitive value-object level; concrete event types may impose stricter rules. MusicalOffset may be negative, zero, or positive.

The accepted semantic operations are:

```text
ScorePosition + MusicalDuration → ScorePosition
ScorePosition + MusicalOffset   → ScorePosition
ScorePosition - ScorePosition   → MusicalOffset
MusicalDuration + MusicalDuration → MusicalDuration
MusicalDuration - MusicalDuration → MusicalOffset
MusicalOffset + MusicalOffset → MusicalOffset
MusicalOffset - MusicalOffset → MusicalOffset
-MusicalOffset → MusicalOffset
```

Results must satisfy their target type's invariants; invalid negative positions or durations are not silently saturated or clamped to zero.

There is no addition of two ScorePositions or general multiplication of ScorePosition. General MusicalDuration scaling is not specified or included for implementation in this slice without a concrete need. Exact Java method names are not architectural decisions here.

## Consequences

- No rounding errors in the canonical timeline
- ABC time values can be represented exactly
- MIDI ticks are converted at the system boundary
- Editing grids remain editing state
