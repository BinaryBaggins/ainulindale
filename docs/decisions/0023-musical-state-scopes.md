# ADR-0023 – Canonical Scopes for Tempo, Meter, KeySignature, and InstrumentAssignment

**Status:** Accepted

## Context

Musical information has both a semantic scope and a temporal shape. Sharing a timeline does not mean all musical information belongs to a voice or to one universal event hierarchy.

The canonical model needs explicit state scopes while preserving the shared absolute score timeline and explicit structural pitch values.

## Decision

The following scopes are accepted:

| State type | Allowed scope | Inheritance |
| --- | --- | --- |
| Tempo | Composition | Composition-wide only |
| Meter | Composition, Part, Voice | Hierarchical |
| KeySignature | Composition, Part, Voice | Hierarchical |
| InstrumentAssignment | Part | Part-local state |

All four types have temporal form `State`. They use the state timeline semantics in [ADR-0022](0022-scoped-musical-state-inheritance.md); meter and key signature also use its hierarchical fallback and persistent local override rules.

### Tempo

Tempo remains Composition-level state. All parts and voices share an absolute score timeline, so the mapping from `ScorePosition` through tempo state to `PlaybackTime` must remain composition-wide and unambiguous.

Part- and Voice-local canonical tempo are not allowed. Genuine polytempo would require a deliberate future revision of the shared-time model, rather than an implicit extension of allowed scopes. Tempo value representation and playback implementation remain open.

### Meter

Meter may be established at Composition, Part, or Voice scope. A child may persistently override its parent's meter and explicitly return to inheritance.

This permits polymetric structures. Different metrical interpretations do not create separate score timelines or change stored `ScorePosition` values. The concrete meter representation remains open.

### KeySignature

`KeySignature` may be established at Composition, Part, or Voice scope, using the same inheritance rules as meter.

Key signatures provide musical and notational context. They do not determine the stored canonical pitch of an already-resolved note. For example, `Pitch(F, +1, 4)` explicitly means F♯4 even in the context of G major; the key signature is not hidden input required to interpret the pitch.

Format-specific importers, including ABC importers, must resolve context-dependent source notation into explicit canonical `Pitch` values. The concrete key-signature representation remains open.

### InstrumentAssignment

`InstrumentAssignment` is currently Part-scoped state. It may change over score time, so the generic domain does not require one fixed instrument per part. A `TargetRuleset`, such as a LOTRO ruleset, may impose stricter constraints.

Voice-local assignment is deliberately not introduced. Voices requiring independently assigned instruments may indicate separate parts unless a concrete future domain requirement justifies voice-local assignment.

The detailed models for `Instrument`, `InstrumentAssignment`, `InstrumentTransposition`, and target-specific instrument mapping remain open.

### Boundaries and Unresolved Topics

`NoteEvent` remains a ranged `VoiceEvent` owned by exactly one voice. This description does not introduce a `RangedVoiceEvent` type.

Tie, slur, and tuplet-group concepts have relational form; their detailed models remain open. This decision does not define markers, tuning, playback implementation, or concrete APIs.

Dynamics remain unresolved in scope and temporal form. Possible distinctions include state-like `mf`, span-like crescendo, and note- or event-local accent. These examples are not accepted types or scopes, and MIDI velocity is not automatically equivalent to canonical musical dynamics.

## Consequences

- Tempo preserves one unambiguous composition-wide time mapping.
- Meter and key signature can vary locally without fragmenting the shared score timeline.
- Canonical pitch remains explicit and independent of key-signature context.
- Parts can change instruments without forcing a fixed-instrument domain constraint.
- State value representations, instrument details, dynamics, and concrete storage and APIs remain open.
