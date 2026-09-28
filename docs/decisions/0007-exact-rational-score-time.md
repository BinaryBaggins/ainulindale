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

## Consequences

- No rounding errors in the canonical timeline
- ABC time values can be represented exactly
- MIDI ticks are converted at the system boundary
- Editing grids remain editing state
