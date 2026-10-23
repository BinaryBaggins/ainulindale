# Module Architecture

## Status

This document describes the accepted Maven architecture and current implementation. The seven-module reactor and legacy source relocation are implemented. Foundation and domain contain production code; editing, ABC, MIDI, and desktop currently provide module scaffolding without Java implementation.

See [ADR-0039](../decisions/0039-maven-multi-module-architecture.md) and [ADR-0040](../decisions/0040-foundation-module-boundary.md). These refine the initial module direction in [ADR-0019](../decisions/0019-maven-multi-module-architecture.md) and infrastructure reuse in [ADR-0038](../decisions/0038-reuse-domain-independent-infrastructure.md).

## Target Structure and Root Project

```text
ainulindale/
├── pom.xml
├── ainulindale-foundation/
├── ainulindale-domain/
├── ainulindale-editing/
├── ainulindale-abc/
├── ainulindale-midi/
├── ainulindale-desktop/
└── ainulindale-legacy/
```

The [root POM](../../pom.xml) is the Maven parent and reactor aggregator with packaging `pom`. It centralizes groupId, project version, Java 25, source encoding, JUnit dependency management, and Surefire plugin management. All seven child POMs exist. The initial POM structure is no longer an open implementation task.

## Module Responsibilities

### ainulindale-foundation

Foundation contains only domain-independent and application-independent technical primitives genuinely needed across architectural boundaries. Its implemented content is the extracted Result abstraction, including Result, Success, Failure, ResultError, and Unit, with tests and without legacy domain/editor dependencies. No replacement Result framework is introduced.

Admission requires all four properties: domain independence, application independence, UI independence, and genuine cross-module sharing. Foundation must remain very small; it is not a generic core/common/utils module.

ScorePosition, Pitch, DomainError, instrument IDs, musical validation, settings, i18n, Swing actions, and application configuration are not automatic foundation candidates. Musical concepts belong in domain; application infrastructure initially belongs in desktop.

### ainulindale-domain

Domain contains the canonical musical model. Illustrative responsibilities include:

- Composition, Part, Voice, VoiceEvent, NoteEvent, and typed domain IDs
- Rational, ScorePosition, MusicalDuration, MusicalOffset, and ScoreRange
- Pitch, PitchClass, PitchAlteration, PitchInterval, ScaleDegree, ScaleStructure, and TonalContext
- Meter, Tempo, and KeySignature
- InstrumentDefinition, InstrumentAssignment, and WrittenToSoundingTransposition
- MusicalRelation, Tie, Slur, and domain-specific errors

This is not a completed class inventory. The [domain model](domain-model.md#current-implementation-status) distinguishes implemented concepts from accepted future concepts. Domain currently depends on foundation for Result.

Domain must not depend on editing, ABC, MIDI, desktop/Swing, legacy, settings, i18n, filesystem access, JSON persistence, LOTRO target rules, undo/redo, or selection/session state. It remains usable independently of formats and UI technology.

### ainulindale-editing

Editing depends on domain and owns higher-level user editing semantics: selection-driven editing, multi-event operations, quantization, editing policies, transaction grouping, undo/redo, and editing commands.

Domain changes canonical entities while preserving invariants. Editing performs user intentions that may involve multiple domain mutations. Domain never depends back on editing. Editing may initially remain small or nearly empty until the domain core is stable. See [editing-model.md](editing-model.md).

### ainulindale-abc

ABC is an adapter/boundary module that may depend on domain:

```text
ABC source/document representation
    ↕
ABC semantic mapping
    ↕
canonical Composition
```

Source-preserving document/AST concerns stay inside this boundary when implemented. ABC syntax and storage must not shape canonical domain ownership or representation. Domain never depends on ABC. Concrete adapter internals remain open.

### ainulindale-midi

MIDI is an adapter/boundary module mapping MIDI representations to and from canonical musical semantics. It may depend on domain; domain never depends on MIDI.

MIDI note numbers, PPQ, channels, tracks, and playback-specific concerns do not become canonical domain primitives merely because an adapter needs them. Detailed mapping and playback integration remain open.

### ainulindale-desktop

Desktop is the application composition root. It may depend on domain, editing, ABC, MIDI, and foundation where directly needed. It owns Swing UI, bootstrap, workspace/session concerns, actions, shortcuts, settings, i18n integration, and desktop orchestration.

Existing suitable settings and i18n infrastructure should initially be reused here. They currently remain in legacy, along with Swing UI and application bootstrap; greenfield desktop integration is pending. Localization maps semantic domain errors to presentation messages outside domain; settings and locale are not Composition state.

Dedicated settings or i18n modules are not introduced without a concrete cross-application dependency reason. Extraction can be reconsidered if a CLI, server, or other frontend genuinely needs the same infrastructure.

Ordinary Java construction can wire the application. No Spring, Guice, or other DI framework is required merely for wiring, and no event bus is introduced merely for decoupling without a concrete use case.

### ainulindale-legacy

Legacy is a temporary migration/quarantine module that keeps the current application buildable alongside the greenfield implementation. The initial source relocation is complete:

```text
former root src/ → ainulindale-legacy/src/
```

Preserve current packages and behavior initially; do not redesign internals merely to clean up the module. Legacy remains a reference and temporary executable path, not a greenfield dependency. It is explicitly expected to be deleted after the new application replaces it.

Legacy now depends on foundation for the extracted Result abstraction. Canonical APIs must not be adapted simply to ease migration from EditorTrack, TrackEditorModel, or EditorWorkspace.

## Dependency Direction

Arrows indicate allowed consumer-to-dependency direction, not a requirement to declare every dependency immediately:

```text
foundation

domain  → foundation (if Result is needed)
editing → domain
abc     → domain
midi    → domain
desktop → editing, abc, midi, domain
        → foundation (where directly needed)
legacy  → foundation (where extracted infrastructure is needed)
```

Foundation has no dependency on higher-level project modules. Maven dependency declarations should physically enforce the boundaries rather than relying only on convention.

Forbidden directions include:

- Domain → editing, ABC, MIDI, desktop, or legacy
- Editing, ABC, or MIDI → desktop
- Any greenfield module → legacy

There is no reverse domain dependency on the modules that use it. Reusing existing infrastructure must not preserve legacy musical/editor coupling; suitable infrastructure may move or be minimally adapted for these boundaries.

## Deferred Modules and JPMS

The Project, Arrangement, Revision, and shared InstrumentLibrary responsibilities in [ADR-0041](../decisions/0041-project-arrangements-revisions-and-instrument-library.md) establish conceptual organization, not new Maven modules or dependencies. Their module placement and aggregate or application-coordination representation remain open. Composition remains the canonical musical editing aggregate; Project is not required to be one transactional aggregate containing all musical content and history.

Do not create speculative modules for LOTRO targets, persistence, instrument catalog, playback, MusicXML, CLI, settings, i18n, or generic utilities. Future modules require a concrete responsibility and dependency boundary. A future LOTRO adapter/rules module may depend on domain, but is deferred.

The first migration does not introduce JPMS or `module-info.java`. Maven modules provide useful compile-time dependency boundaries without adding simultaneous JPMS migration complexity. JPMS may be reconsidered for a concrete future requirement.

## Implementation Progress and Remaining Work

| Work                                        | Status                                                                                                                                                                        |
| ------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Parent/reactor and seven child modules      | Implemented, including initial dependency declarations.                                                                                                                       |
| Legacy source and test relocation           | Completed under ainulindale-legacy. Build verification remains a development check, not an open architecture decision; this documentation review does not rerun it.           |
| Result extraction into foundation           | Implemented with tests; reused by domain and legacy.                                                                                                                          |
| Fundamental values and typed IDs            | Rational, score-time values, Pitch/PitchAlteration, six typed IDs, Tempo, Meter, and KeySignature are implemented.                                                            |
| Basic Composition aggregate                 | Structure, ordering, note editing/movement, Tempo, and hierarchical Meter/KeySignature state are implemented with tests. Metadata, instruments, and relations remain pending. |
| Instruments and relations                   | Pending; their identity types alone do not constitute entity implementations.                                                                                                 |
| Editing, adapters, and desktop              | Module scaffolding exists; substantive implementation and settings/i18n integration remain pending.                                                                           |
| Project, Arrangement, Revision, and library | Accepted conceptual direction only; implementation remains pending.                                                                                                           |

Detailed coding order may evolve with implementation feedback; dependency direction remains stable. See the [domain implementation status](domain-model.md#current-implementation-status) for source references.

## Open Implementation Details

- Package layout and APIs for unimplemented concepts
- Additional Composition operations and errors for future capabilities
- Necessary adaptations while reusing settings/i18n in desktop
- Adapter internals and integration details
- Future persistence layout/module and LOTRO/TargetRuleset module
- Possible future settings/i18n extraction for genuine cross-application use
- Detailed capability transition and eventual legacy removal

These choices may evolve during implementation while preserving the accepted responsibilities and dependency boundaries.
