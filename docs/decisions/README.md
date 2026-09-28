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
- [ADR-0020 – VoiceEvent as the Voice-Level Event Abstraction](0020-voice-event-abstraction.md)
- [ADR-0021 – Composition as the Aggregate Mutation Boundary](0021-composition-aggregate-mutation-boundary.md)
- [ADR-0022 – Scoped Musical State Uses Hierarchical Inheritance](0022-scoped-musical-state-inheritance.md)
- [ADR-0023 – Canonical Scopes for Tempo, Meter, KeySignature, and InstrumentAssignment](0023-musical-state-scopes.md)
- [ADR-0024 – Meter Preserves Structural Metric Grouping](0024-structural-meter-model.md)
- [ADR-0025 – Meter Does Not Define Measure Structure by Itself](0025-meter-and-measure-structure.md)
- [ADR-0026 – Tempo Uses a Canonical Rational Score-to-Time Rate](0026-canonical-tempo-rate.md)
- [ADR-0027 – KeySignature Represents Diatonic-Step Alteration Defaults](0027-canonical-key-signature.md)
- [ADR-0028 – External Formats Are Expressiveness References, Not Domain Templates](0028-external-formats-as-expressiveness-references.md)
- [ADR-0029 – ScaleStructure Preserves Diatonic and Chromatic Degree Semantics](0029-pitch-class-and-scale-structure.md)
- [ADR-0030 – TonalContext Combines PitchClass Center and ScaleStructure](0030-tonal-context.md)
- [ADR-0031 – Instrument Identity Is Separate from Part Assignment and Performance Setup](0031-instrument-definition-and-assignment.md)
- [ADR-0032 – Instrument Transposition Uses Directed Diatonic and Chromatic Interval Semantics](0032-written-to-sounding-transposition.md)
- [ADR-0033 – External Instrument Catalogs Provide Definitions Without Owning Composition Semantics](0033-instrument-catalog-boundary.md)
- [ADR-0034 – Musical Relations Are Composition-Owned Entities](0034-composition-owned-musical-relations.md)
- [ADR-0035 – Tie and Slur Use Explicit NoteEvent References](0035-tie-and-slur-relations.md)
- [ADR-0036 – TupletGroup Semantics Are Deferred Until Group Membership Requirements Are Clear](0036-tuplet-group-design-deferred.md)
- [ADR-0037 – Composition Exposes Intent-Based Mutations Through the Aggregate Root](0037-composition-public-api-boundary.md)
- [ADR-0038 – Reuse Suitable Domain-Independent Infrastructure Across the Greenfield Architecture](0038-reuse-domain-independent-infrastructure.md)

## Open Decisions

In particular, the following have not yet been finalized:

- Concrete event storage and derived index implementations
- Technical ordering policies for simultaneous events
- Exact aggregate mutation API and internal delegation mechanisms
- Query views/snapshots and command result shapes
- Exact domain-error hierarchy
- Persistence rehydration mechanism
- Concrete Maven placement of Result, i18n, and settings
- Additional event types and any further event hierarchy
- TupletGroup concrete model, including membership, ratios, nesting, and notation relationships
- Explicit rest representation
- Detailed Slur restrictions and any future justified same-Voice Tie restriction
- Relation-specific deletion, cascade, or transformation policies
- Relation storage, indexing, and technical ordering
- Playback and notation interpretation of relations
- Lyrics association
- Dynamics scope and temporal model
- Exact InstrumentDefinition metadata
- Performance/setup models, including capo, scordatura, and alternate tuning
- Composition-versus-Project persistence ownership of local instrument definitions
- Catalog provenance schema and catalog implementation
- Target-specific instrument mappings
- Concrete instrument, assignment, and interval APIs/storage
- InstrumentAssignment timeline storage
- Tuning system and technical pitch mapping
- Concrete Java implementation of the accepted Tempo value model
- MetronomeMark and source/notation representation
- Textual tempo indications and their relationship to quantitative Tempo
- Gradual tempo changes, transitions, and tempo curves
- Fallback policies for unspecified quantitative tempo
- Playback implementation and PlaybackTime representation
- Technical rounding, clock units, scheduling, MIDI timing, and audio timing
- Concrete Java implementation of the accepted Meter value model
- Measure and barline model, including MeasureStructure
- MeasurePosition model and derivation
- Interchangeable or alternate meter relationships
- Conventional meter-grouping interpretation rules
- Concrete Java implementation of the accepted KeySignature value model
- Named mode classification and its relationship to ScaleStructure
- Whether additional tonal or harmonic concepts are needed
- TonalContext scope and timeline semantics
- Explicit relationships between TonalContext and KeySignature beyond their independence
- Non-octave-periodic systems
- Concrete Java APIs and storage for the accepted tonal and scale concepts
- Concrete format mappings and adapters
- Concrete state timeline implementation and APIs
- Representation and API for explicitly ending local overrides
- MusicalForm
- PlaybackPlan
- Editing transactions and undo/redo
- Persistence model
- TargetRuleset contract
