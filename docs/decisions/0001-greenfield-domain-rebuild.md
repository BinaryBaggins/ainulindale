# ADR-0001 – Greenfield Rebuild Alongside the Legacy System

**Status:** Accepted

## Context

The existing Ainulindalë model already contains working editor and workspace structures such as:

```text
EditorTrack
TrackEditorModel
EditorWorkspace
```

However, future requirements for Ainulindalë extend well beyond the current track-centric MIDI editor model.

In particular, long-term support is planned for:

- A generic musical domain model
- Comprehensive ABC semantics
- MIDI import and export
- LOTRO-specific target rules
- Editing across the entire composition
- Musical form
- Multiple voices
- Global musical events

## Decision

The new domain and editing system will be developed as a greenfield architecture alongside the existing legacy code.

Existing classes do not have to remain easy to migrate.

New code must not depend on legacy models.

Legacy code serves only as:

- A reference for existing behavior
- A reference for UI experience
- A source of known edge cases
- A baseline for test comparisons

The legacy portion will be removed entirely once the new application path provides the required capabilities.

## Consequences

Positive:

- No artificial compatibility constraints
- A clean new ownership structure is possible
- Old architectural mistakes do not have to be preserved
- Module boundaries can be designed around the new domain

Negative:

- Two architectures will temporarily coexist
- Some functionality must be reimplemented
- The eventual transition must be deliberately managed at the capability level
