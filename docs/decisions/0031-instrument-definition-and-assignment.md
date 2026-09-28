# ADR-0031 – Instrument Identity Is Separate from Part Assignment and Performance Setup

**Status:** Accepted

## Context

An instrument's musical identity and intrinsic properties are distinct from where and when a part uses it. Conflating identity with assignment would duplicate definitions or allow an assignment to change what the instrument means.

Temporary performance configuration must also remain distinct from intrinsic instrument semantics.

## Decision

`InstrumentDefinition` is an entity with stable `InstrumentDefinitionId`, metadata, and `WrittenToSoundingTransposition`. Names and descriptive metadata are not identity. The definition is independent of part assignment, time of use, editor/session state, source format, and external catalog availability. Exact metadata fields and Java APIs remain open.

`InstrumentAssignment` is Part-scoped state and a value object, not an independently identified entity. It references InstrumentDefinitionId to identify the instrument active for a part at a ScorePosition. The full definition is not embedded redundantly in every assignment.

Intrinsic written-to-sounding transposition belongs to the definition. Assignments neither define nor override it: intrinsic transposition is part of what the instrument is, not where it happens to be assigned.

Every definition has an explicit transposition. Concert/non-transposing instruments use `PitchInterval(0, 0)` inside WrittenToSoundingTransposition, not missing or null state. Directed interval semantics are defined in [ADR-0032](0032-written-to-sounding-transposition.md).

Capo, scordatura, alternate tuning, and other temporary performance configurations belong to future separate setup concepts. They must not mutate or override the definition's intrinsic transposition or become transposition overrides in InstrumentAssignment. Concrete setup names, types, and behavior are not decided here.

## Consequences

- Instrument identity survives changes to names, metadata, or part assignment.
- Assignments reference reusable local definitions without duplicating intrinsic properties.
- Intrinsic instrument semantics and temporary performance setup remain distinct.
- Metadata, setup models, assignment timeline storage, and concrete APIs remain open.
- Local definition retention and external catalogs follow [ADR-0033](0033-instrument-catalog-boundary.md), without deciding Composition-versus-Project persistence ownership.
