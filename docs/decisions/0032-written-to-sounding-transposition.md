# ADR-0032 – Instrument Transposition Uses Directed Diatonic and Chromatic Interval Semantics

**Status:** Accepted

## Context

Canonical NoteEvent.pitch is sounding/concert musical pitch. Instrument-specific notation needs a well-defined relationship between written and sounding pitch without losing enharmonic spelling or duplicating conversion state.

Chromatic distance alone cannot distinguish an augmented unison from a minor second.

## Decision

`PitchInterval` is an immutable value object containing signed integer `diatonicSteps` and rational `chromaticOffset` in the accepted abstract semitone unit. Both components may be negative, zero, or positive. Floating point is not canonical; exact Java representations and APIs remain open.

Structural equality preserves both components: `(0, +1)` and `(+1, +1)` are distinct intervals. Acoustic equivalence under a tuning does not justify normalization by chromatic distance alone.

Application uses the diatonic component to select the target base step and octave, and derives its PitchAlteration to satisfy the chromatic distance:

```text
C4 + PitchInterval(+1, +1) → D♭4
C4 + PitchInterval( 0, +1) → C♯4
```

Octave displacement is encoded in the interval itself. Written C4 to sounding C5 uses `(+7, +12)`; written C4 to sounding C3 uses `(-7, -12)`. No separate stored octave-transposition field is introduced.

`WrittenToSoundingTransposition` is a directionally typed concept containing a PitchInterval:

```text
written Pitch + WrittenToSoundingTransposition → canonical sounding Pitch
```

The direction must be represented by a wrapper value object or equivalent strong typing, not merely comments around a bare interval.

Because stored NoteEvent.pitch remains sounding/concert pitch, instrument-specific written pitch is derived through the inverse:

```text
canonical sounding Pitch - WrittenToSoundingTransposition → written Pitch
```

No independent SoundingToWrittenTransposition is stored. Non-transposing instruments use the explicit identity interval `(0, 0)` rather than absent transposition.

## Consequences

- Interval application preserves diatonic and chromatic spelling semantics.
- Conversion direction is explicit in the domain representation.
- The inverse cannot diverge from independently stored forward state.
- Canonical note pitch remains sounding/concert pitch; transposition derives instrument-specific notation.
- Concrete APIs, tuning, acoustic interpretation, and temporary setup transformations remain separate open concerns.
