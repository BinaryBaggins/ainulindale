# ADR-0041 – Project Organizes Arrangements, Revisions, and Shared Instrument Definitions

**Status:** Accepted

## Context

[ADR-0002](0002-composition-as-domain-root.md), [ADR-0021](0021-composition-aggregate-mutation-boundary.md), and [ADR-0037](0037-composition-public-api-boundary.md) establish Composition as the canonical musical aggregate and editing mutation boundary. Related adaptations of a musical work need organization and comparison without moving project management or versioning into that aggregate.

[ADR-0031](0031-instrument-definition-and-assignment.md) separates instrument definitions from assignments. [ADR-0033](0033-instrument-catalog-boundary.md) requires locally retained instrument semantics independent of external catalogs, but leaves Composition-versus-Project ownership open. Shared definitions must also preserve the interpretation of historical musical states.

## Decision

### Project and Arrangement

A Project represents a musical work and organizes related Arrangements of that work. Multiple Arrangements primarily support alternative interpretations, adaptations, and comparisons without requiring separate Projects. A Project is not currently intended as a general-purpose collection of unrelated musical works.

An Arrangement is an independently editable musical adaptation or interpretation within a Project. Examples include an original arrangement, a LOTRO-specific arrangement, alternative instrumentation, and an alternative musical adaptation. LOTRO is an example of a target adaptation, not a constraint on the canonical domain.

Each Arrangement has its own canonical Composition. Arrangements must not depend directly on another Arrangement's mutable musical state. Composition remains the canonical representation of musical content; Composition, Part, and Voice gain no project-management or versioning responsibilities.

Project and Arrangement do not extend the musical-state inheritance hierarchy. The scopes and inheritance in [ADR-0022](0022-scoped-musical-state-inheritance.md) and [ADR-0023](0023-musical-state-scopes.md) remain unchanged: inheritance is within Composition → Part → Voice where permitted, and InstrumentAssignment remains Part-local state.

### Revision History and Comparison

Each Arrangement may have its own revision history. The architecture must eventually support preserving earlier Arrangement states, comparing revisions of the same Arrangement, restoring earlier revisions, and comparing different Arrangements.

Revision history and parallel Arrangements are separate concepts. An Arrangement is not merely a revision of another Arrangement. Existing domain entity identifiers alone must not be assumed sufficient to match musical elements across different Arrangements; comparison correspondence remains open. Stable identities from [ADR-0005](0005-stable-typed-domain-identities.md) retain their established meaning and scopes.

The revision-storage mechanism is undecided. This decision does not select event sourcing, Git-style branching or merging, snapshot frequency or storage format, automatic change tracking, a diff algorithm, or a persistence technology.

### Project Instrument Library

A Project owns or manages a shared library of locally retained InstrumentDefinitions. Multiple Arrangements and their Compositions within the Project may reference the same definitions. InstrumentAssignments continue to reference InstrumentDefinitionId rather than embedding full definitions, as established by ADR-0031.

This resolves ADR-0033's open conceptual ownership question in favor of Project-scoped shared ownership for the planned Project model. Technical storage and aggregate implementation remain undecided. The catalog boundary in ADR-0033 remains authoritative: an external InstrumentCatalog is only a source of definitions, never the authoritative owner of a Project's musical meaning. External catalog updates must not silently modify locally retained instrument semantics.

Historical revisions must remain interpretable according to the instrument definitions relevant to their recorded musical state. Shared ownership does not permit later definition changes to reinterpret history. Definition versioning, immutable snapshots, and revision storage remain open mechanisms, not prescribed solutions.

Instrument identity, Part-local assignment, and intrinsic written-to-sounding transposition retain the semantics of ADR-0031 and [ADR-0032](0032-written-to-sounding-transposition.md). Sharing definitions does not introduce assignment transposition overrides or change canonical sounding pitch.

### Standalone Composition Boundaries

The architecture preserves the ability to exchange, export, or otherwise transfer a Composition together with the instrument definitions necessary to interpret it. Interpreting exported musical content must not require permanent access to the originating Project or an external catalog. The exchange format, packaging strategy, and import/export APIs remain open.

### Conceptual Ownership and Aggregate Boundaries

Project organization and shared library ownership describe conceptual responsibilities, not a requirement for one large transactional DDD aggregate containing every Arrangement, Composition, and Revision.

Composition remains the established aggregate root for canonical musical editing. Its Part, Voice, event, and relation ownership and invariant-safe mutation boundaries remain unchanged. How Project, Arrangement, InstrumentLibrary, and Revision are represented as aggregates or application-level coordination concepts remains open, as do their module placement and technical storage.

### Preferred Direction: Instrument Definition Versioning

The preferred future approach is to preserve an instrument's logical identity across revisions of its definition.

An `InstrumentDefinitionId` should continue to identify the same logical instrument, while distinct immutable definition versions preserve its intrinsic musical properties at different points in its history.

Changes to intrinsic properties, particularly `WrittenToSoundingTransposition`, must not silently reinterpret existing Arrangements or historical Revisions. Each recorded musical state must remain interpretable using the instrument semantics applicable to that state.

For the initial implementation, `InstrumentDefinition` remains immutable. Definition updates and version management are deferred until the Project Instrument Library is designed.

**This is a preferred architectural direction, not yet a finalized versioning contract.** Version identifiers, historical references, update semantics, metadata versioning, and persistence mechanisms remain open design questions.

## Consequences

- Related musical adaptations can be organized and compared within one Project while retaining independent canonical musical content.
- Revision history preserves earlier states of an Arrangement without conflating those states with parallel Arrangements.
- Shared instrument definitions require preservation of historical meaning and portability of standalone Compositions.
- The conceptual ownership question left by ADR-0033 is resolved; persistence and transactional boundaries are not.
- No classes, Composition APIs, persistence schemas, modules, or dependencies are introduced by this decision. A concrete version-control subsystem is not designed here.
- Maestro may inspire workflows, but Ainulindalë remains architecturally independent and gains no Maestro dependency.

## Open Design Questions

- Aggregate or application-coordination representation and module placement of Project, Arrangement, InstrumentLibrary, and Revision
- Revision capture, storage, restoration, and comparison mechanisms
- Correspondence of musical elements across Arrangements and concrete diff algorithms
- Definition lifecycle/versioning and preservation of the definitions relevant to historical revisions
- Technical persistence/storage of the Project instrument library and its references
- Standalone Composition exchange format, packaging, and import/export APIs
