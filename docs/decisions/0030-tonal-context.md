# ADR-0030 – TonalContext Combines PitchClass Center and ScaleStructure

**Status:** Accepted

## Context

Tonal information needs an octave-independent center and explicit scale-degree structure. A traditional mode name or key signature alone does not supply the canonical meaning of both.

The value concepts in [ADR-0029](0029-pitch-class-and-scale-structure.md) provide this foundation without relying on external formats.

## Decision

`TonalContext` is an immutable value object composed of a `PitchClass` center and a `ScaleStructure`. Concrete Java APIs remain implementation details.

The center has no octave. No separate `TonalCenter` wrapper is introduced solely around PitchClass; a concrete future semantic requirement may justify reconsidering that choice.

For example:

```text
center = PitchClass(D, 0)
scaleStructure =
    (0, 0), (1, 2), (2, 3), (3, 5), (4, 7), (5, 9), (6, 10)
```

This is the structure conventionally classified as D Dorian, but its canonical meaning does not depend on that name.

### Mode Classification

No fixed `Mode` enum or required mode-name field defines canonical scale identity. Traditional modes, custom scales, and rational microtonal structures are represented by their degree semantics.

A name may later be derived, attached as metadata, or represented by a separate classification concept. The details and relationship between conventional names and ScaleStructure remain open.

### Independence from KeySignature and Pitch

KeySignature supplies default alterations per diatonic step. TonalContext supplies a center and ordered scale structure. They remain independent: multiple contexts may share a compatible signature, and neither context nor scale structure takes a signature as hidden input.

Any future relationship between KeySignature and TonalContext must be explicit. The fully resolved Pitch of a NoteEvent remains independent of both; tonal context does not make note storage relative.

## Consequences

- The basic relationship between tonal center and scale structure is explicit.
- Canonical musical meaning does not require traditional mode names.
- KeySignature, TonalContext, and resolved note Pitch retain distinct responsibilities.
- Named classification, additional tonal/harmonic concepts, TonalContext scope and timeline semantics, tuning, and concrete APIs remain open.
- External mappings remain future adapter concerns under [ADR-0028](0028-external-formats-as-expressiveness-references.md).
