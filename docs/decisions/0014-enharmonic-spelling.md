# ADR-0014 – Enharmonic Spelling Is Structural Semantics

**Status:** Accepted

## Decision

Enharmonic pitches that may sound identical are distinct domain values.

```text
C♯4 != D♭4
B♯4 != C5
```

No automatic enharmonic normalization takes place.

## Consequences

Notation and harmonic information are preserved.

Acoustic equivalence must be determined separately.
