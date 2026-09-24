# ADR-0011 – Structural Pitch Rather Than MIDI Pitch

**Status:** Accepted

## Decision

The canonical `Pitch` is defined by:

```text
DiatonicStep
PitchAlteration
SPN octave
```

MIDI note number and frequency in hertz are not part of `Pitch`.

## Consequences

The domain model remains independent of:

- MIDI
- Specific tuning
- Playback technology
