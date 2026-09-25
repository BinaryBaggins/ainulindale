# ADR-0026 – Tempo Uses a Canonical Rational Score-to-Time Rate

**Status:** Accepted

## Context

Canonical score time already uses whole-note units. Quantitative tempo must relate that score time to real time without depending on the source or display form of a metronome indication.

Different reference note values can express the same rate: quarter note = 120 and half note = 60 both represent 30 whole notes per minute. Treating their reference units as canonical identity would preserve presentation differences as different quantitative rates.

## Decision

`Tempo` is an immutable value object normalized to an exact `Rational` number of whole notes per minute. This unit follows directly from the canonical whole-note score-time unit.

Conceptually:

```java
record Tempo(Rational wholeNotesPerMinute) {}
```

The exact Java representation and API remain implementation details. The rate must be strictly positive; zero and negative values are invalid. The generic domain imposes no arbitrary upper bound.

### Normalization and Equality

A metronome indication resolves to canonical tempo by multiplying its reference musical duration by its rate per minute:

```text
quarter = 120       → 120 × 1/4 → 30   → Tempo(30)
half = 60           → 60 × 1/2  → 30   → Tempo(30)
dotted quarter = 60 → 60 × 3/8  → 45/2 → Tempo(45/2)
```

Canonical equality is equality of the normalized quantitative rate. The metronome reference duration is not part of `Tempo` identity. Unlike structural Meter equality, Tempo equality does not preserve reference-unit differences that express the same rate.

`Rational` preserves exact rates and conversions without floating-point approximation in canonical state. The rate remains rational for as long as possible. Concrete rounding belongs at playback, MIDI, audio, or other technical boundaries, not inside `Tempo`.

### Representation and Expression Boundaries

`Tempo` is the quantitative musical rate. A possible `MetronomeMark` is a notation, presentation, or source representation of that rate. Its reference duration and rate per minute may differ between equivalent expressions, but no concrete metronome-mark type is designed here.

Source spelling and preserved notation belong to the corresponding source/notation model, such as the separate ABC representation.

Textual indications such as Largo, Andante, Allegro, and Presto do not specify one unique mathematical rate. Their possible future models and relationship to quantitative tempo remain open.

The accepted tempo timeline represents state changes, with constant quantitative tempo between changes. Ritardando, accelerando, and tempo curves remain separate future questions. No curve, interpolation, or transition-duration fields are added to `Tempo`.

### Missing State and Scope

Absent Tempo state means no quantitative tempo has been specified. It is not zero and does not imply a canonical default such as 120 BPM. A value at `ScorePosition.ZERO` is not mandatory.

A player, importer, exporter, target, or UI may later apply a fallback policy without materializing it as canonical Composition state unless explicitly requested. No fallback policy is selected here.

Tempo remains Composition-scoped state under [ADR-0023](0023-musical-state-scopes.md). Part- or Voice-local tempo and polytempo are not introduced. Polytempo would require a deliberate revision of the shared-time model.

### Derived Playback Relationship

At constant specified tempo, real duration is derived as:

```text
score duration / wholeNotesPerMinute × one minute
```

At 30 whole notes per minute, one whole note lasts two seconds and a quarter note lasts half a second.

This is a semantic relationship, not a playback implementation. PlaybackTime representation, clocks, time units, scheduling, rounding policies, MIDI timing, and audio-engine timing remain open technical boundary concerns.

## Consequences

- Equivalent metronome indications resolve to equal canonical Tempo values.
- Canonical rates remain exact and independent of presentation or source syntax.
- Missing tempo remains explicitly unspecified without introducing invalid or default canonical values.
- Textual and gradual tempo expressions remain separate from the basic quantitative value.
- Concrete Java, notation, timeline storage, and playback designs remain open.
