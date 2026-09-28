# ADR-0029 – ScaleStructure Preserves Diatonic and Chromatic Degree Semantics

**Status:** Accepted

## Context

Tonal centers are octave-independent, while scale degrees must preserve diatonic spelling as well as chromatic distance. Chromatic offsets alone would lose the enharmonic distinctions already preserved by canonical Pitch.

The model follows musical semantics and Ainulindalë's requirements, independently of external representation, as established in [ADR-0028](0028-external-formats-as-expressiveness-references.md).

## Decision

`PitchClass` consists of `DiatonicStep` and `PitchAlteration`, without an octave. Its equality preserves structural spelling: C♯ and D♭ remain distinct even when acoustically equivalent under a particular tuning. It contains no MIDI note number, frequency, or tuning information.

`ScaleStructure` is an immutable value object containing a non-empty ordered sequence of ScaleDegrees relative to a tonal center. Degree order is canonical musical semantics, not incidental insertion order.

Each `ScaleDegree` contains a nonnegative integer `diatonicOffset` and a rational `chromaticOffset` in the abstract semitone unit used by PitchAlteration. Concrete Java types and APIs remain open.

### Diatonic and Chromatic Meaning

A major scale can be expressed as:

```text
(0, 0), (1, 2), (2, 4), (3, 5), (4, 7), (5, 9), (6, 11)
```

Applied to center D, degree `(2, 4)` selects F as the diatonic base step and requires F♯ to satisfy the chromatic distance. Offset 4 alone would not preserve that spelling.

Rational values allow microtonal degrees such as `(1, 3/2)`. Neither floating-point offsets nor an integer-only restriction define the canonical model. Abstract chromatic offsets do not prescribe 12-TET or a frequency mapping; tuning remains separate.

### Invariants and Periodicity

- At least one degree
- First degree is `(0, 0)`
- Diatonic offsets strictly increase
- Chromatic offsets strictly increase
- No duplicate degrees

Degrees must not be silently inferred, reordered, sorted, normalized, or merged. Invalid input must not become a different valid scale through automatic normalization.

The current structure is ascending and repeats at the octave.

> ScaleStructure describes exactly one octave period, including the root and excluding its octave repetition.

Every stored ScaleDegree also satisfies:

```text
0 <= diatonicOffset < 7
0 <= chromaticOffset < 12
```

These bounds complement the existing invariants. The major-scale example ends at `(6, 11)`; `(7, 12)` must not be stored because it repeats the root in the next octave period.

The value 12 defines the octave span in canonical abstract semitone units, not 12-TET or a particular tuning. Chromatic offsets remain rational: values such as `7/2` and `11/3` are valid in principle if they satisfy the ordering and within-octave constraints. Acoustic interpretation remains a future tuning concern.

Arbitrary non-octave-periodic systems are deferred until a concrete requirement justifies an extension. No general equave or arbitrary-period model is introduced.

## Consequences

- Tonal centers can preserve spelling independently of octave.
- Scale degrees preserve both diatonic and chromatic meaning.
- Ordered custom and microtonal structures remain representable without a fixed mode enumeration.
- Concrete implementations, tuning, generalized periodicity, and format mappings remain open.

The use of PitchClass and ScaleStructure in tonal context is defined in [ADR-0030](0030-tonal-context.md).
