# ADR-0027 – KeySignature Represents Diatonic-Step Alteration Defaults

**Status:** Accepted

## Context

A key signature supplies chromatic defaults by diatonic step. Those defaults do not uniquely identify a tonal center, mode, or scale: different tonal interpretations may share a signature.

Canonical pitch already stores explicit rational alteration. KeySignature must provide context without turning resolved note pitches into context-dependent values or importing notation syntax into the domain.

## Decision

`KeySignature` is an immutable value object whose semantics are a total mapping from `DiatonicStep` to `PitchAlteration`.

Each of the seven steps `C`, `D`, `E`, `F`, `G`, `A`, and `B` has one effective alteration. Any step omitted during construction or parsing has effective value `PitchAlteration.ZERO`. The exact Java representation and APIs remain implementation details; sparse storage is not part of the canonical contract.

Zero defaults complete a specified value. They do not materialize an absent KeySignature state or replace inheritance with an implicit all-zero signature.

### Expressiveness and Equality

The accepted rational `PitchAlteration` model permits microtonal signatures, for example `F → +1/2`, `B → -1`, and zero for all other steps.

Canonical signatures are not limited to traditional sharp/flat signatures, integer alterations, seven sharps or flats, major/minor conventions, or any particular notation system. Format and target restrictions belong at adapter, exporter, validation, or `TargetRuleset` boundaries.

Equality compares all seven effective alterations. Sparse input containing only `F → +1` is equal to total input with the same F alteration and explicit zero for every other step.

Construction syntax, source format, entry order, display glyphs, and sparse versus total storage do not affect equality. Distinct alterations are not normalized merely because external notation might render them similarly.

### Tonal and Notation Boundaries

KeySignature does not contain tonal center, tonic, mode, scale, major/minor identity, or harmonic function. Those semantics and their relationships remain open; no finalized types for them are introduced.

Canonical note pitches remain explicit. Under a signature with `F → +1`, an F-sharp note is `Pitch(F, +1, 4)` and an F-natural note is `Pitch(F, 0, 4)`. The signature is not required to interpret either stored pitch. Context-dependent source notation must be resolved at the format boundary.

Semantic `PitchAlteration` remains distinct from displayed `Accidental`. Similarly, KeySignature defaults are distinct from key-signature glyphs, accidental display order, layout, source spelling, and format-specific tokens. Such information belongs in notation/source representations, not the value object.

### Scope

The state scopes and inheritance from [ADR-0022](0022-scoped-musical-state-inheritance.md) and [ADR-0023](0023-musical-state-scopes.md) remain unchanged: Composition, Part, and Voice support hierarchical inheritance, persistent local overrides, and explicit return to inheritance.

This decision does not design timeline storage, override APIs, or concrete format adapters.

## Consequences

- Chromatic defaults can be represented independently of tonal interpretation and notation conventions.
- Equality is stable across equivalent construction and source representations.
- Resolved canonical pitches retain their explicit meaning regardless of surrounding signature state.
- Tonal center, mode, scale, concrete Java details, and format mappings remain open.
