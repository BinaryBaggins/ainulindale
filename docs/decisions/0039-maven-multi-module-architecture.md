# ADR-0039 – Greenfield Architecture Uses Focused Maven Modules Alongside Temporary Legacy

**Status:** Accepted

## Context

The greenfield domain needs physical dependency boundaries while the current application remains buildable. The initial direction in ADR-0019 and reuse principles in ADR-0038 now have a concrete first-phase module structure.

## Decision

The root becomes a Maven parent/reactor aggregator with `pom` packaging. Shared configuration includes groupId, project version, Java 25, encoding, JUnit and plugin versions, and appropriate dependency/plugin management. Child POMs remain small; exact POM content is not frozen.

The initial modules are:

| Module | Responsibility |
| --- | --- |
| ainulindale-foundation | Minimal independent shared primitives, initially reusable Result |
| ainulindale-domain | Canonical musical model, typed IDs, value objects, invariants, and domain errors |
| ainulindale-editing | Higher-level editing, policies, transaction grouping, and undo/redo |
| ainulindale-abc | ABC source/document boundary and semantic mapping |
| ainulindale-midi | MIDI representation and semantic mapping boundary |
| ainulindale-desktop | Swing application and composition root, sessions, settings, i18n, actions, and orchestration |
| ainulindale-legacy | Temporary existing application and migration quarantine |

Domain may depend on foundation if Result is required. Editing, ABC, and MIDI depend toward domain. Desktop may depend on domain, editing, ABC, MIDI, and foundation where directly needed. Legacy may depend on foundation after suitable infrastructure extraction. Foundation depends on no higher-level project module.

No greenfield module may depend on legacy. Domain must not depend on editing, ABC, MIDI, desktop, settings, i18n, filesystem/JSON persistence, LOTRO target rules, undo/redo, or session/selection state. Editing and adapters must not depend on desktop. Maven dependencies should physically enforce these boundaries.

### Migration and Application Assembly

Move current `src/` mechanically into `ainulindale-legacy/src/` during implementation, preserving packages and behavior. Verify the application and existing tests, then establish foundation and domain, implement value objects/IDs and the aggregate, and add instruments and relations. Substantial editing follows a stable domain core; adapters and application integration grow incrementally.

Legacy is a reference and temporary executable path, never a reason to reshape canonical APIs around EditorTrack, TrackEditorModel, or EditorWorkspace. Its deletion after replacement is an explicit outcome.

Desktop uses ordinary Java construction as composition root. No DI framework or event bus is introduced merely for module wiring or decoupling. Existing settings and i18n are initially reused in desktop; dedicated modules require genuine cross-application need.

No JPMS or module-info.java is introduced in the first migration. No speculative LOTRO, persistence, catalog, playback, MusicXML, CLI, settings, i18n, or generic utility modules are created. Future boundaries require concrete responsibilities.

## Consequences

- The canonical domain stays independent of UI, formats, and legacy musical models.
- The legacy application can remain buildable during parallel implementation and is removed after replacement.
- Packages, complete class inventories, exact APIs/errors, child POMs, adapter internals, and persistence layout remain implementation decisions.
- The full target structure and initial sequence are maintained in [module-architecture.md](../architecture/module-architecture.md).
- Foundation admission is constrained by [ADR-0040](0040-foundation-module-boundary.md).
