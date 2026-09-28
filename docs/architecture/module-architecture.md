# Module Architecture

## Status

This document defines the accepted Maven target architecture for the first greenfield implementation phase. Restructuring is an implementation task; no POM or source movement is performed by this documentation decision.

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

The root becomes the Maven parent and reactor aggregator, with packaging `pom`. It will centralize shared configuration such as groupId, project version, Java 25, source encoding, JUnit and Surefire/plugin versions, and dependency/plugin management where appropriate. Child POMs should remain small and unsurprising. Complete plugin inventories and exact POM implementation remain open.

## Module Responsibilities

### ainulindale-foundation

Foundation contains only domain-independent and application-independent technical primitives genuinely needed across architectural boundaries. Its initial intended content is the existing reusable Result abstraction, extracted or minimally adapted where necessary without legacy domain/editor dependencies. No replacement Result framework is introduced.

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

This is not a frozen class or package inventory. The only initially expected project-level dependency is foundation, if Result is required.

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

Existing suitable settings and i18n infrastructure should initially be reused here. Localization maps semantic domain errors to presentation messages outside domain; settings and locale are not Composition state.

Dedicated settings or i18n modules are not introduced without a concrete cross-application dependency reason. Extraction can be reconsidered if a CLI, server, or other frontend genuinely needs the same infrastructure.

Ordinary Java construction can wire the application. No Spring, Guice, or other DI framework is required merely for wiring, and no event bus is introduced merely for decoupling without a concrete use case.

### ainulindale-legacy

Legacy is a temporary migration/quarantine module that keeps the current application buildable alongside the greenfield implementation. The initial movement is intended to be largely mechanical:

```text
current src/ → ainulindale-legacy/src/
```

Preserve current packages and behavior initially; do not redesign internals merely to clean up the module. Legacy remains a reference and temporary executable path, not a greenfield dependency. It is explicitly expected to be deleted after the new application replaces it.

If Result is extracted into foundation, legacy may depend on foundation where needed. Canonical APIs must not be adapted simply to ease migration from EditorTrack, TrackEditorModel, or EditorWorkspace.

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

Do not create speculative modules for LOTRO targets, persistence, instrument catalog, playback, MusicXML, CLI, settings, i18n, or generic utilities. Future modules require a concrete responsibility and dependency boundary. A future LOTRO adapter/rules module may depend on domain, but is deferred.

The first migration does not introduce JPMS or `module-info.java`. Maven modules provide useful compile-time dependency boundaries without adding simultaneous JPMS migration complexity. JPMS may be reconsidered for a concrete future requirement.

## Initial Implementation Sequence

1. Convert the root POM into the parent/reactor aggregator.
2. Move the current implementation mechanically into ainulindale-legacy.
3. Verify that the legacy application and existing tests still build.
4. Create ainulindale-foundation.
5. Extract/reuse the existing Result abstraction in foundation where suitable.
6. Create ainulindale-domain.
7. Implement fundamental value objects and IDs first.
8. Build canonical entities and the Composition aggregate.
9. Add instrument and relation implementations.
10. Begin substantial editing implementation once the domain core is stable.
11. Build adapter and application integration incrementally afterward.

Detailed coding order may evolve with tests and implementation feedback; dependency direction remains stable.

## Open Implementation Details

- Exact package layout, class inventories, and public APIs
- Exact Composition signatures and DomainError hierarchy
- Complete child-POM content and build-plugin configuration
- Necessary adaptations while extracting Result and reusing settings/i18n
- Adapter internals and integration details
- Future persistence layout/module and LOTRO/TargetRuleset module
- Possible future settings/i18n extraction for genuine cross-application use
- Detailed capability transition and eventual legacy removal

These choices may evolve during implementation while preserving the accepted responsibilities and dependency boundaries.
