# Architecture Decisions

This directory records the accepted architecture decisions for Ainulindalë. Existing decision numbers are preserved from the original decision log.

The current domain specification is in [domain-model.md](../architecture/domain-model.md).

## Accepted Decisions

- [ADR-0001 – Greenfield Rebuild Alongside the Legacy System](0001-greenfield-domain-rebuild.md)
- [ADR-0002 – Composition as the Canonical Aggregate Root](0002-composition-as-domain-root.md)
- [ADR-0003 – Part Rather Than Track as the Canonical Musical Concept](0003-part-instead-of-track.md)
- [ADR-0004 – Voice as a Fundamental Musical Layer](0004-voice-as-musical-layer.md)
- [ADR-0005 – Stable, Typed Domain Identities](0005-stable-typed-domain-identities.md)
- [ADR-0006 – Controlled Aggregate Mutation](0006-controlled-aggregate-mutation.md)
- [ADR-0007 – Exact Rational Score Time](0007-exact-rational-score-time.md)
- [ADR-0008 – Shared Absolute Score Timeline](0008-shared-absolute-score-timeline.md)
- [ADR-0009 – Score Time Is Separate from Meter and Playback Time](0009-score-time-meter-and-playback.md)
- [ADR-0010 – Unexpanded Score Timeline](0010-unexpanded-score-timeline.md)
- [ADR-0011 – Structural Pitch Rather Than MIDI Pitch](0011-structural-pitch-model.md)
- [ADR-0012 – Scientific Pitch Notation](0012-scientific-pitch-notation.md)
- [ADR-0013 – PitchAlteration as a Rational Chromatic Unit](0013-rational-pitch-alteration.md)
- [ADR-0014 – Enharmonic Spelling Is Structural Semantics](0014-enharmonic-spelling.md)
- [ADR-0015 – PitchAlteration Is Not Accidental](0015-pitch-alteration-and-accidental.md)
- [ADR-0016 – Microtonal Pitch Semantics Remain Canonical](0016-canonical-microtonal-pitch.md)
- [ADR-0017 – NoteEvent as an Entity](0017-note-event-entity.md)
- [ADR-0018 – The Domain Allows Overlapping and Duplicate Notes](0018-overlapping-and-duplicate-notes.md)
- [ADR-0019 – Maven Multi-Module as the Target Architecture](0019-maven-multi-module-architecture.md)

## Open Decisions

In particular, the following have not yet been finalized:

- `VoiceEvent` abstraction
- Storage and indexing of events within a voice
- General event hierarchy
- Musical relations such as ties, slurs, and tuplets
- Global musical state versus state local to a part or voice
- InstrumentAssignment and transposition
- Tuning and SoundingPitch
- Tempo and meter models
- MusicalForm
- PlaybackPlan
- Editing transactions and undo/redo
- Persistence model
- TargetRuleset contract
