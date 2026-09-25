# Ainulindalë – Canonical Domain Model

## Status

This document describes the currently accepted state of Ainulindalë's canonical musical domain model.

The model is being developed as a greenfield architecture alongside the existing legacy system. Existing classes such as `EditorTrack`, `TrackEditorModel`, and `EditorWorkspace` impose no compatibility requirements on the new model.

Unresolved design questions are explicitly marked as open.

---

## 1. Core Principles

The canonical domain model describes the musical meaning of a composition.

It is independent of:

- Swing and other UI technologies
- MIDI-specific data structures
- ABC syntax
- LOTRO-specific constraints
- Editor session state
- Persistence formats
- File paths
- Undo/redo implementation

External formats and targets are mapped to the domain model or generated from it.

Canonical design starts from musical semantics and Ainulindalë's own domain requirements. ABC, MIDI, MusicXML, LOTRO, and other external systems help discover relevant concepts, test expressiveness, validate interoperability requirements, and guide future adapter design. They do not define canonical ownership, type hierarchies, or internal representation.

> External formats are test cases for expressiveness, not templates for the domain model.

An external construct such as ABC `K:` must not dictate an equivalent canonical field structure. A future adapter may map one external construct into multiple canonical concepts or combine multiple external constructs into one canonical concept. Format-specific syntax and preservation belong in dedicated adapter/source representations. Concrete mappings and adapters remain open.

See [ADR-0028](../decisions/0028-external-formats-as-expressiveness-references.md).

Core principle:

> Domain representability is not the same as target validity.

The canonical model may represent musical states that a particular target subsequently rejects or must transform.

---

## 2. Aggregate Structure

The fundamental musical ownership structure is:

```text
Composition
└── Part*
    └── Voice+
        └── VoiceEvent*
```

The following rules apply:

- `Composition` is the aggregate root.
- A `Part` belongs to exactly one `Composition`.
- A `Voice` belongs to exactly one `Part`.
- A `Part` has at least one `Voice`.
- A `Composition` may contain no parts.
- A `VoiceEvent` belongs to exactly one `Voice`.
- A `Voice` may contain no events.

---

## 3. Composition

`Composition` represents the complete canonical musical work.

Conceptually:

```text
Composition
├── metadata
├── ordered Parts
├── composition-level musical state
├── musical form                [later]
└── cross-entity relations      [later]
```

`Composition` answers the domain question:

> What is the musical work?

The following are outside `Composition`, among other things:

- Active part
- Active voice
- Visibility
- Selection
- Zoom
- Scroll position
- Swing components
- Undo/redo history
- Open files or file paths

These states belong in future editing, workspace, UI, or persistence layers.

### Invariant

An empty `Composition` is valid.

---

## 4. Part

A `Part` is:

> A logically coherent, independently addressable musical part within a composition.

Examples include:

```text
Lute
Harp
Flute
Theorbo
Piano
```

`Part` is explicitly not synonymous with:

- MIDI track
- MIDI channel
- ABC voice
- LOTRO player
- UI track lane

Such concepts may be mapped to one or more parts at system boundaries.

### Instrument Assignment

A `Part` is not inherently limited to exactly one instrument.

`InstrumentAssignment` is Part-scoped state and may change over score time. Its scope is defined in section 38; the detailed instrument and assignment models remain open.

A specific `TargetRuleset` may impose stricter rules.

Example:

```text
Generic composition:
A part may change instruments

LOTRO target:
A part might be restricted to a single instrument
```

---

## 5. Voice

A `Voice` is:

> A logically independent stream of musical events within a part.

Examples:

```text
Lute
└── Voice 1
```

or:

```text
Piano
├── Voice 1
└── Voice 2
```

`Voice` is a generic musical concept, not a direct representation of an ABC `V:` element.

### Polyphony

A voice is not inherently monophonic.

The following is valid within a voice:

```text
C4 ─────
E4 ─────
G4 ─────
```

Temporal overlaps therefore do not inherently violate voice invariants.

Target or editing rules may define additional constraints.

### Minimum Count

Every `Part` has at least one voice.

```text
part.voices.size() >= 1
```

Removing the last voice of a part is not allowed.

---

## 6. Ownership

Ownership is unambiguously directed downwards:

```text
Composition
   owns
     ↓
Part
   owns
     ↓
Voice
   owns
     ↓
VoiceEvent
```

An object must not belong to multiple parents at the same time.

This does not require bidirectional Java references.

In particular, the following is not required:

```java
voice.getPart().getComposition();
```

An event must never be owned by multiple voices. Other structures reference events through `EventId`, not shared object ownership.

External domain mutations enter through the aggregate root, as described in section 12.

---

## 7. Identity

Entities that can be persisted and referenced have explicit, stable, typed IDs.

At least the following are planned:

```java
record CompositionId(UUID value) {}
record PartId(UUID value) {}
record VoiceId(UUID value) {}
record EventId(UUID value) {}
```

The specific ID technology remains an implementation detail.

`EventId` is unique within the entire `Composition`, not merely within a voice. Identity and ownership are distinct: event identity is composition-wide, while each event is owned by exactly one voice.

The semantic requirements are:

- Identity is independent of Java object identity.
- Identity survives saving and loading.
- Reordering does not change identity.
- Metadata changes do not change identity.
- Different kinds of IDs are distinguished by the type system.

For example, the following must not be possible:

```java
PartId partId = voiceId;
```

---

## 8. Metadata and Names

Names are metadata, not identity.

Multiple parts may have the same name:

```text
Lute
Lute
Lute
```

A name may be absent where appropriate.

A part's identity is determined exclusively by its `PartId`.

The same principle applies to voices.

---

## 9. Ordering

Part and voice order are canonical domain state.

This does not imply a musical list order for events within a voice; event collection semantics are defined in section 36.

Example:

```text
Composition
1. Lute
2. Harp
3. Flute
4. Theorbo
```

Changing the order does not change identities:

```text
identity != order
```

The domain therefore includes:

- Part membership
- Part order
- Voice membership
- Voice order

It does not include:

- Visibility
- Active state
- Selection
- Editor-specific presentation

---

## 10. No Canonical Track Type

The canonical domain model initially contains no generic `Track` type.

The musical structure is:

```text
Composition
└── Part
    └── Voice
        └── VoiceEvent
```

The term `Track` may still be used at technical or visual boundaries, for example:

```text
MidiTrack
UiTrackLane
```

This avoids conflating MIDI, UI, and musical semantics under an ambiguous domain term.

---

## 11. No Staff Without a Domain Requirement

A `Staff` is not initially part of the domain model.

A possible notation model:

```text
Part: Piano
├── Staff: Treble
│   ├── Voice 1
│   └── Voice 2
└── Staff: Bass
    ├── Voice 3
    └── Voice 4
```

will only be introduced when Ainulindalë has a concrete domain requirement for it.

`Voice` is already being modeled because it carries musical semantics, not merely visual semantics.

---

## 12. Mutability and the Aggregate Mutation Boundary

The canonical aggregate allows controlled mutation.

Mutable collections are not exposed for unrestricted external modification.

The following is not intended:

```java
composition.parts().add(part);
part.voices().clear();
```

`Composition` is the public mutation boundary of the aggregate. External structural and domain mutations enter through it, including changes to nested entities.

Conceptually:

```java
composition.addPart(...);
composition.removePart(...);
composition.movePart(...);

composition.addVoice(partId, ...);
composition.addNote(voiceId, range, pitch);
composition.removeEvent(eventId);
composition.changeNotePitch(eventId, pitch);
```

These examples illustrate the mutation boundary, not a finalized API.

Internally, `Composition` may delegate to `Part` or `Voice`. Nested entities may have internal or package-private mutation operations, but callers must not bypass the aggregate root through unrestricted public mutation of `Part`, `Voice`, or `VoiceEvent`.

This protects composition-wide invariants, including `EventId` uniqueness and valid ownership, and provides the boundary for future relation integrity and cross-entity invariants. The details of those future invariants remain undecided.

The domain provides primitive, invariant-safe mutations. Higher-level user operations such as transpose, quantize, move selection, and duplicate section, together with editing transactions and undo/redo, belong to the future editing layer. Their design remains open.

Value objects remain immutable.

See [ADR-0021](../decisions/0021-composition-aggregate-mutation-boundary.md).

---

## 13. Canonical Time Model

The domain distinguishes three fundamentally different concepts of time:

```text
Score time
≠
Metrical interpretation
≠
Playback time
```

Score time describes a position within the canonical, unexpanded musical structure.

Meter provides metric structure for interpreting that position. Actual measure and barline structure is a separate concern; meter alone does not fully determine measure positions.

Tempo and form will later be used to derive playback time and playback order.

---

## 14. Shared Score Timeline

All parts and voices in a composition share the same absolute score timeline.

```text
Composition timeline

0           1/4           1/2           3/4            1
│-------------│-------------│-------------│-------------│

Part A / Voice 1     C
Part A / Voice 2     E
Part B / Voice 1     G
                     ↑
              same ScorePosition
```

A voice has no local timeline of its own.

Events with the same `ScorePosition` occur at the same musical instant.

---

## 15. Unit of Time

The canonical unit is the whole note.

```text
whole note       = 1
half note        = 1/2
quarter note     = 1/4
eighth note      = 1/8
dotted eighth    = 3/16
```

The abstract unit "beat" is deliberately avoided because its meaning depends on the metrical context.

Meter interprets the timeline but does not define it.

---

## 16. Rational Representation

Canonical musical time uses only exact, normalized rational values.

Floating-point numbers are excluded as a canonical time representation.

Conceptually:

```java
record Rational(
    BigInteger numerator,
    BigInteger denominator
) {}
```

The specific implementation may be optimized later.

A `Rational` is normalized:

```text
2/4    → 1/2
3/9    → 1/3
-2/-4  → 1/2
```

The denominator is positive.

---

## 17. Time Types

Three distinct semantic types are used:

```text
ScorePosition
MusicalDuration
MusicalOffset
```

### ScorePosition

An absolute position on the score timeline.

```text
ScorePosition >= 0
```

Negative absolute score positions are not allowed.

A pickup also begins at `ScorePosition.ZERO`.

### MusicalDuration

A nonnegative musical duration.

```text
MusicalDuration >= 0
```

`MusicalDuration.ZERO` is valid at the primitive level.

Specific event types may impose stricter requirements.

### MusicalOffset

A relative displacement in musical time.

```text
MusicalOffset ∈ ℚ
```

A `MusicalOffset` may be positive, zero, or negative.

Example:

```java
move(note, MusicalOffset.of(-1, 8));
```

---

## 18. Typed Time Operations

Examples of meaningful operations:

```text
ScorePosition + MusicalOffset
    → ScorePosition

ScorePosition + MusicalDuration
    → ScorePosition

ScorePosition - ScorePosition
    → MusicalOffset
```

A result must still satisfy the invariants of its target type.

---

## 19. No Global Time Grid

The composition has no global:

- Tick grid
- PPQ
- Quantization grid
- Minimum grid resolution

The following values may coexist:

```text
1/8
1/12
1/20
7/96
```

An editing grid is editing state.

PPQ is MIDI state.

Target-specific minimum durations are target rules.

None of these properties determines what the domain can fundamentally represent.

---

## 20. Tempo and Meter

Tempo does not change `ScorePosition`.

Conceptually:

```text
ScorePosition
    ↓ tempo map
PlaybackTime
```

Meter does not change `ScorePosition` either.

Meter describes metric structure, but does not by itself determine actual measure boundaries. A future derivation may conceptually require:

```text
ScorePosition
+ effective Meter
+ MeasureStructure
    ↓
MeasurePosition
```

`MeasureStructure`, barlines, and `MeasurePosition` remain future domain concerns, not finalized types or APIs. See section 40.

Tempo and meter interpret the timeline without moving existing events.

Tempo is Composition-level state. Meter may be overridden at Part or Voice scope without creating a separate score timeline. The state and inheritance semantics are defined in section 38.

---

## 21. ScoreRange

Domain objects with a temporal extent can use a `ScoreRange`.

Conceptually:

```java
record ScoreRange(
    ScorePosition start,
    MusicalDuration duration
) {}
```

A range is half-open:

```text
[start, end)
```

The following ranges therefore do not overlap:

```text
[0, 1/4)
[1/4, 1/2)
```

`end` is derived from `start + duration` and is not stored separately.

---

## 22. Score Time and Musical Form

The score timeline describes the unexpanded musical structure.

A repeat is not materialized by duplicating events.

For example:

```text
A |: B :| C
```

remains structurally:

```text
A B C
```

with additional form information.

A future `PlaybackPlan` can produce:

```text
A B B C
```

Core principle:

```text
Score timeline
+
MusicalForm
    ↓
PlaybackPlan
```

---

## 23. Pitch

`Pitch` is an immutable value object.

Its structural form is:

```java
record Pitch(
    DiatonicStep step,
    PitchAlteration alteration,
    int octave
) {}
```

`DiatonicStep` has exactly these values:

```java
C, D, E, F, G, A, B
```

---

## 24. Scientific Pitch Notation

The octave number of a `Pitch` follows scientific pitch notation.

`C4` denotes middle C.

Under the standard MIDI mapping, middle C corresponds to MIDI note number 60.

However, the MIDI note number is not part of the canonical `Pitch`.

The octave number has no domain-defined, MIDI-like range.

Semantically:

```text
octave ∈ ℤ
```

The concrete Java representation may use `int`, for example.

---

## 25. PitchAlteration

`PitchAlteration` represents the semantic chromatic alteration of a diatonic base note.

It is expressed as a normalized rational number in the abstract unit "semitone".

```text
natural        =  0
sharp          = +1
flat           = -1
double sharp   = +2
quarter sharp  = +1/2
quarter flat   = -1/2
```

Conceptually:

```java
record PitchAlteration(
    Rational semitones
) {}
```

Floating-point values are excluded.

`PitchAlteration` initially has no domain-level range constraint.

---

## 26. PitchAlteration Does Not Define Frequency

The unit "semitone" describes an abstract chromatic alteration.

It defines neither a specific frequency ratio nor a tuning.

In particular:

```text
PitchAlteration(+1)
```

does not automatically mean multiplying the frequency by:

```text
2^(1/12)
```

A `TuningSystem` will provide the acoustic interpretation later.

---

## 27. Enharmonic Identity

Enharmonically equivalent-sounding pitches remain structurally distinct.

Examples:

```text
C♯4 != D♭4

B♯4 != C5

C♭5 != B4
```

`Pitch.equals()` expresses structural musical equality, not acoustic equivalence.

Possible acoustic equivalence under a particular tuning is a separately derived relation.

---

## 28. No Pitch Normalization

`Pitch` performs no automatic enharmonic or octave normalization.

For example:

```text
Pitch(C, +2, 4)
```

remains C double-sharp and does not automatically become D4.

Likewise:

```text
Pitch(C, +12, 4)
```

remains structurally unchanged and does not automatically become C5.

The octave belongs to the diatonic base note.

---

## 29. PitchAlteration and Accidental

`PitchAlteration` and a displayed `Accidental` are distinct concepts.

```text
PitchAlteration
    = semantic chromatic alteration

Accidental
    = notation information
```

For example, a pitch may structurally be F♯ even when a key signature means no visible ♯ is displayed before the note.

Notation symbols therefore do not belong directly in `Pitch`.

---

## 30. No MIDI or Frequency Representation in Pitch

The following are not part of `Pitch`:

- MIDI note number
- Pitch bend
- Frequency in hertz
- MIDI range
- Specific tuning

Future processing may, for example, take this form:

```text
Pitch
+
InstrumentTransposition
    ↓
SoundingPitch
+
TuningSystem
    ↓
Frequency
```

or:

```text
SoundingPitch
+
MidiMappingPolicy
    ↓
MidiNoteNumber + additional MIDI data
```

---

## 31. Microtonality

Microtonal alterations may be part of canonical pitch semantics.

They are not categorically relegated to playback or view state.

For example:

```text
C + 1/2 semitone
```

is a structurally different pitch from:

```text
C natural
```

The specific acoustic interpretation remains the responsibility of the tuning system.

---

## 32. NoteEvent

A `NoteEvent` is the first concrete `VoiceEvent` and is an entity within exactly one voice.

It has at least:

```text
EventId
ScoreRange
Pitch
```

Its event position corresponds to `ScoreRange.start()`; it is not a separate source of temporal truth.

Conceptually, without prescribing the final Java class or interface shape:

```java
final class NoteEvent {

    private final EventId id;

    private ScoreRange range;
    private Pitch pitch;
}
```

### Invariants

```text
id != null
range != null
pitch != null
range.start >= 0
range.duration > 0
```

Although `MusicalDuration.ZERO` is valid as a general value, a regular `NoteEvent` always has a positive duration.

Grace notes are not simulated using zero-duration NoteEvents.

---

## 33. Entities and Value Objects

`NoteEvent` is an entity.

Two NoteEvents may have identical musical values while still representing distinct entities.

```text
Note A:
C4 @ [0, 1/4)

Note B:
C4 @ [0, 1/4)

A.id != B.id
```

The following types, in contrast, are value objects:

```text
Pitch
PitchAlteration
ScorePosition
MusicalDuration
MusicalOffset
ScoreRange
```

---

## 34. Overlapping and Identical Notes

The canonical domain model fundamentally permits multiple notes with:

- The same pitch
- The same start
- The same range
- Temporal overlap

For example:

```text
C4 ─────
C4 ─────
```

can represent two distinct `NoteEvent` entities.

Editing policies or a specific `TargetRuleset` may restrict such states.

However, fundamental domain representability does not prohibit them.

---

## 35. VoiceEvent

`VoiceEvent` is the common abstraction for musical events owned by exactly one `Voice`.

Every `VoiceEvent` has a stable `EventId` and is anchored at a `ScorePosition`. Conceptually:

```java
public interface VoiceEvent {

    EventId id();

    ScorePosition position();
}
```

This example describes the semantic contract; the exact Java interface or class shape remains an implementation choice.

A `VoiceEvent` does not necessarily have a duration or `ScoreRange`. `NoteEvent` is the first concrete event type and retains its positive-duration range and pitch. Its position is the start of that range.

`RangedVoiceEvent` is deliberately deferred: only `NoteEvent` currently justifies a range. A shared ranged abstraction may be introduced later if multiple concrete event types need it.

Relations, tempo, meter, key signatures, and instrument assignments do not automatically become `VoiceEvent`s. A shared musical context alone does not establish voice-level event semantics. Section 38 defines the accepted state scopes; detailed relation models remain open.

See [ADR-0020](../decisions/0020-voice-event-abstraction.md).

---

## 36. Event Collection and Simultaneous Events

A voice owns a collection of uniquely identified, temporally anchored events. It has no intrinsic musical list order. Insertion order and collection index do not define musical ordering.

Musical temporal ordering is derived from `VoiceEvent.position()`.

Events with the same `ScorePosition` are simultaneous and have no intrinsic ordering unless future domain semantics explicitly introduce one.

Serialization, testing, export, or other technical processing may require deterministic ordering. Such an order is not automatically domain semantics; no particular technical comparator is mandated here.

---

## 37. Event Storage and Derived Indexes

The canonical contract is independent of the concrete storage strategy and must not be defined as `List<VoiceEvent>`.

An implementation may initially use an ID-based store:

```text
EventId → VoiceEvent
```

This is an option, not a canonical requirement.

Temporal indexes or composition-wide event lookup indexes may be introduced later for performance. Such indexes must remain derived and must not become a second source of truth.

Canonical ownership remains:

```text
Composition
└── Part
    └── Voice
        └── VoiceEvent
```

---

## 38. Musical State Scope and Temporal Semantics

### Semantic Scope and Temporal Shape

Musical information is described by two independent questions:

1. What is its semantic scope?
2. What is its temporal shape?

Temporal shapes include `State`, `Point`, `Span`, and `Relation`. These are semantic distinctions, not a universal event hierarchy. Domain-specific types remain explicit; no generic `MusicalContextEvent`, universal `ScopedEvent`, or similar untyped abstraction is introduced.

`Tempo`, `Meter`, `KeySignature`, and `InstrumentAssignment` are stateful. A state value becomes effective at a `ScorePosition` and remains effective until changed or until its local override explicitly ends.

### State Timelines

State changes conceptually have the form:

```text
ScorePosition → value
```

Within the same state timeline, scope, and state type, at most one value may be established at a given `ScorePosition`.

The effective local state at a position is the latest local state change at or before that position, unless that local override has explicitly ended.

For example, a canonical quantitative tempo timeline is:

```text
0   → Tempo(30)
1   → Tempo(45/2)
3/2 → Tempo(55/2)
```

These rates are in whole notes per minute, equivalent to quarter-note metronome indications of 120, 90, and 110 per minute respectively. The canonical Tempo value model is defined in section 41.

A value at `ScorePosition.ZERO` is not required. An absent initial value is valid: state may be unspecified or inherited from an allowed parent scope. No canonical default value is introduced.

Concrete data structures and APIs remain implementation details. A helper such as `StateTimeline<T>` may be considered internally later, but it is not part of the canonical musical vocabulary.

### Hierarchical Inheritance and Persistent Overrides

For state types allowed at multiple hierarchical scopes, fallback follows:

```text
Composition
    ↓ fallback
Part
    ↓ fallback
Voice
```

At a given position, a scope uses its active local state if one exists. Otherwise, it inherits the effective parent state. If no value is available through the allowed scope hierarchy, the state remains unspecified.

A local value is a persistent override. Later parent changes do not replace an active child override.

For example:

```text
Composition KeySignature:
0  → all steps 0
20 → F: +1, C: +1; all other steps 0

Part A:
(no override)

Part B:
10 → F: +1; all other steps 0
```

The effective values are:

| Score interval | Part A | Part B |
| --- | --- | --- |
| [0, 10) | All steps 0 | All steps 0 |
| [10, 20) | All steps 0 | F: +1; others 0 |
| [20, onward) | F: +1, C: +1; others 0 | F: +1; others 0 |

The Composition change at position 20 is stored only once.

> Inherited state is derived, not persisted as duplicated child state.

Child timelines contain local state, not materialized copies of inherited values. This avoids maintaining synchronized duplicate state across scopes.

### Explicit Return to Inheritance

A persistent local override must be explicitly removable. Once it ends, the child inherits the effective parent value at that position and follows subsequent parent changes, unless another local override becomes active.

Conceptually:

```text
No local state        → inherit parent
Active local state    → override parent
Local override ended → inherit parent again
```

The representation of an override ending and the API used to express it remain open. No concrete method or storage mechanism is prescribed.

### Tempo

Tempo is canonical Composition-level state. Part- and Voice-local tempo are not currently allowed.

All parts and voices share one absolute score timeline. The canonical mapping must remain composition-wide and unambiguous:

```text
ScorePosition
    ↓ tempo state
PlaybackTime
```

Genuine polytempo would require a deliberate future revision of the shared-time model. It is not introduced by the scope model. Playback implementation remains open.

### Meter

Meter is stateful at Composition, Part, or Voice scope and uses hierarchical inheritance with persistent local overrides.

This permits polymetric structures while preserving the shared absolute `ScorePosition` timeline. Different metrical interpretations do not imply different score timelines.

### KeySignature

`KeySignature` is stateful at Composition, Part, or Voice scope and uses the same hierarchical inheritance model as meter.

It provides musical and notational context, but does not determine the canonical pitch of an already-resolved `NoteEvent`.

```text
KeySignature: F → +1; all other steps → 0

NoteEvent:
Pitch(F, +1, 4)
```

The stored pitch explicitly represents F♯4. A key signature is not hidden input needed to interpret that value. Format-specific importers, such as ABC importers, must resolve context-dependent source notation into explicit canonical `Pitch` values.

### InstrumentAssignment

`InstrumentAssignment` is currently Part-scoped state. A part may change instruments over score time; the generic domain does not require one fixed instrument per part.

A concrete target such as LOTRO may impose stricter constraints through a `TargetRuleset`.

Voice-local instrument assignment is deliberately deferred. Independently assigned instruments in different voices may indicate separate parts unless a concrete future domain requirement justifies voice-local assignment.

The detailed models for `Instrument`, `InstrumentAssignment`, `InstrumentTransposition`, and target-specific instrument mapping remain open.

### Accepted Scope Matrix

| Information | Temporal form | Allowed scope / ownership | Inheritance or status |
| --- | --- | --- | --- |
| Tempo | State | Composition | Composition-wide only |
| Meter | State | Composition, Part, Voice | Hierarchical |
| KeySignature | State | Composition, Part, Voice | Hierarchical |
| InstrumentAssignment | State | Part | Part-local state |
| NoteEvent | Ranged VoiceEvent | Voice | Owned by exactly one voice |
| Tie / Slur / TupletGroup | Relation | Detailed model open | Detailed model open |
| Dynamics | Open | Open | Scope and temporal model open |

“Ranged VoiceEvent” describes the temporal form of `NoteEvent`; it does not introduce `RangedVoiceEvent` as a type.

See [ADR-0022](../decisions/0022-scoped-musical-state-inheritance.md) and [ADR-0023](../decisions/0023-musical-state-scopes.md).

---

## 39. Structural Meter Model

### Value Object and Semantic Variants

`Meter` is a canonical value object used as musical state. Its accepted semantic variants are:

```text
Meter
├── StructuredMeter
└── FreeMeter
```

The exact Java inheritance mechanism, collection types, and APIs remain implementation details. Meter retains the Composition, Part, and Voice scopes and inheritance rules defined in section 38.

### StructuredMeter and MeterComponent

A `StructuredMeter` contains a non-empty ordered sequence of `MeterComponent`s. Each component represents a positive number of equal musical-duration units forming one explicit component of the meter structure.

Conceptually:

```text
StructuredMeter:
    ordered components

MeterComponent:
    count
    unit: MusicalDuration
```

The invariants are:

```text
components is non-empty
component.count > 0
component.unit > 0
```

The count is a positive whole number. The unit has positive `MusicalDuration` semantics rather than being a format-specific denominator integer. Although the general duration value permits zero, a meter component's unit must be strictly positive.

Examples:

```text
4/4 → [(4, 1/4)]
3/4 → [(3, 1/4)]
6/8 → [(6, 1/8)]
```

Canonical units are not constrained to powers of two or interchange-format denominator rules. Such restrictions belong to parsers, exporters, validation, or `TargetRuleset`s.

### Explicit Grouping and Mixed Units

The explicit component sequence carries metric grouping and accent semantics. Its order participates in structural identity.

```text
7/8     → [(7, 1/8)]
2+3+2/8 → [(2, 1/8), (3, 1/8), (2, 1/8)]
3+2+2/8 → [(3, 1/8), (2, 1/8), (2, 1/8)]
```

These three meters are structurally distinct.

Grouping must not be inferred and stored as if explicitly specified. In particular, `6/8` does not automatically become `3+3/8`. A future interpretation layer may derive conventional grouping when explicit grouping is absent; its rules remain open, and derived grouping is not canonical explicit state.

Components need not share the same unit. Mixed-unit meters are representable:

```text
2/4 + 3/8 → [(2, 1/4), (3, 1/8)]
```

### Derived Nominal Duration and Structural Equality

The nominal duration of a `StructuredMeter` is derived:

```text
nominalDuration = sum(component.count × component.unit)

2+3+2/8 → 2/8 + 3/8 + 2/8 → 7/8
```

Nominal duration must not be stored redundantly as an independent source of truth. Equal nominal durations do not imply equal meters:

```text
6/8 != 3/4
7/8 != 2+3+2/8
7/8 != 3+2+2/8
2+3+2/8 != 3+2+2/8
```

`Meter.equals` conceptually expresses structural metric equality. It preserves the ordered components, their counts, and their musical-duration units. Structurally distinct meters must not be normalized merely because their nominal durations are equal.

### FreeMeter and Absent State

`FreeMeter` is an explicit meterless or free-rhythm state. It is not `null`, missing state, or absence from a timeline.

```text
Composition Meter: 4/4

Part A: no local Meter state
    → inherits 4/4

Part B: FreeMeter
    → explicitly no regular meter
```

Absent local meter means unspecified state or inheritance from the parent. `FreeMeter` is an explicit local value and therefore participates in the persistent override rules; it does not fall back to the parent's structured meter while active.

### Notation and Source Representation

Canonical `Meter` describes musical metric structure. Common-time and cut-time symbols, graphical time-signature choices, and source spelling belong to notation or source representation.

For example, a common-time symbol and numeric `4/4` may map to the same canonical structured meter. ABC syntax preservation belongs in the separate ABC representation. Any future notation-specific metadata must be modeled separately from `Meter`.

### Interchangeable Meter

Relationships such as `3/4 (6/8)` are not introduced into the basic `Meter` value object. Interchangeable or alternate meter may need a separate semantic or notation model; this remains open.

See [ADR-0024](../decisions/0024-structural-meter-model.md).

---

## 40. Meter and Measure Structure

Meter describes metric structure. Actual measure and barline structure is a separate future domain concern.

Meter alone does not fully determine measure boundaries. Additional information is needed for:

- Pickup or anacrusis measures
- Incomplete measures
- Irregular measures
- Explicit barline deviations
- Meter changes that do not align trivially with nominal measure length
- Later form and barline semantics

A future derivation may conceptually depend on:

```text
ScorePosition
+ effective Meter
+ MeasureStructure
    ↓
MeasurePosition
```

This names the conceptual inputs only. The exact `MeasureStructure`, `Measure`, `Barline`, and `MeasurePosition` models remain open. The derived nominal duration of a structured meter does not by itself establish actual measure boundaries.

See [ADR-0025](../decisions/0025-meter-and-measure-structure.md).

---

## 41. Canonical Quantitative Tempo

### Immutable Value Object and Rate Unit

`Tempo` is an immutable value object describing the quantitative rate between canonical score time and real time. Because score time uses the whole note as its base unit, tempo is normalized to whole notes per minute.

Conceptually:

```java
record Tempo(Rational wholeNotesPerMinute) {}
```

This illustrates the semantic value only; the exact Java representation and API remain implementation details.

The invariant is:

```text
wholeNotesPerMinute > 0
```

Zero and negative rates are invalid. The generic domain imposes no arbitrary upper rate limit.

### Exact Rational Normalization

Canonical tempo uses exact `Rational` values, not floating-point values. A conventional metronome indication resolves to a rate by multiplying its reference note duration by its rate per minute:

```text
quarter note = 120 per minute
120 × 1/4 = 30 whole notes per minute
→ Tempo(30)

dotted quarter = 60 per minute
60 × 3/8 = 180/8 = 45/2 whole notes per minute
→ Tempo(45/2)
```

The rate remains rational for as long as possible. Rounding needed for playback timestamps, MIDI scheduling, audio clocks, nanoseconds, or other technical representations belongs at the corresponding technical boundary, not inside canonical `Tempo`.

### Equality and Metronome Representation

Canonical `Tempo` equality expresses equality of the normalized quantitative rate:

```text
quarter = 120 → 120 × 1/4 → 30 whole notes per minute
half = 60     → 60 × 1/2  → 30 whole notes per minute
```

Both indications resolve to the same `Tempo(30)`. Their metronome reference durations do not participate in `Tempo.equals()` semantics. This intentionally differs from `Meter`, whose explicit structural grouping remains significant even when nominal durations are equal.

The conceptual distinction is:

```text
Tempo         = canonical quantitative musical rate
MetronomeMark = notation / presentation / source representation
```

A future metronome representation might contain a reference `MusicalDuration` and a rate per minute, but no concrete `MetronomeMark` type is accepted here. Source spelling or display choices must not be embedded in canonical `Tempo`. Preserved notation belongs in the corresponding source or notation model, such as the separate ABC representation.

### Unspecified State and Scope

Absent Tempo state is valid and means that no canonical quantitative tempo has been specified. It is neither `Tempo.ZERO` nor an implicit canonical default such as 120 BPM. A value at `ScorePosition.ZERO` remains optional.

A player, importer, exporter, target, or UI may later apply an appropriate fallback policy without materializing that fallback as canonical Composition state unless explicitly requested. The policies themselves remain open.

Tempo remains Composition-scoped state. Part- and Voice-local tempo are not introduced. The shared-time relationship remains:

```text
ScorePosition
    ↓ effective Tempo
PlaybackTime
```

Polytempo remains outside the current architecture and would require a deliberate revision of the shared-time model.

### State Changes and Separate Tempo Expressions

The accepted timeline contains state changes:

```text
position 0 → Tempo A
position 8 → Tempo B
```

Between state changes, the specified quantitative tempo is constant. Gradual changes such as ritardando, accelerando, and tempo curves have a different temporal character and remain open. The basic `Tempo` value object has no curve, interpolation, or transition-duration fields.

Textual indications such as Largo, Andante, Allegro, and Presto do not define a unique mathematical score-to-real-time rate and are not themselves quantitative `Tempo` values. Possible future `TempoIndication` or `TempoExpression` concepts, and their relationship to quantitative tempo, remain undecided. No requirement about carrying either, both, or neither is introduced here.

### Derived Real-Time Duration

For a score duration over which the specified tempo is constant:

```text
real duration = score duration / wholeNotesPerMinute × one minute

Tempo = 30 whole notes per minute

1 whole note → 1/30 minute → 2 seconds
1/4 whole note → 1/2 second
```

This defines the semantic relationship only. Playback clock types, time units, scheduling APIs, rounding policies, MIDI timing, and audio-engine timing remain technical boundary concerns. No conversion algorithm across changing tempo states or playback implementation is designed here.

See [ADR-0026](../decisions/0026-canonical-tempo-rate.md).

---

## 42. Canonical KeySignature

### Immutable Value Object and Total Mapping

`KeySignature` is an immutable value object used as canonical musical state. It represents default chromatic alterations by diatonic step:

```text
DiatonicStep → PitchAlteration
```

This mapping is semantically total: each of `C`, `D`, `E`, `F`, `G`, `A`, and `B` has exactly one effective alteration. The alteration uses the already accepted rational `PitchAlteration` value model.

Steps omitted during construction or parsing have effective value `PitchAlteration.ZERO`. For example:

| Step | No alterations | F-sharp-style signature |
| --- | --- | --- |
| C | 0 | 0 |
| D | 0 | 0 |
| E | 0 | 0 |
| F | 0 | +1 |
| G | 0 | 0 |
| A | 0 | 0 |
| B | 0 | 0 |

The exact Java representation, constructors, and storage remain implementation details. Sparse input may be supported, but sparse storage is not part of the canonical contract.

The zero default applies to unspecified steps within a KeySignature value. It does not create a KeySignature state when none was specified. An absent local state still follows the existing inheritance rules; an explicit all-zero signature is a value.

### Rational Alterations

Rational and microtonal defaults are representable:

```text
F → +1/2
B → -1
all other steps → 0
```

The generic domain does not restrict signatures to traditional sharps and flats, integer alterations, seven sharps or flats, major/minor conventions, or a particular notation system. Format- and target-specific limitations belong to adapters, exporters, validation, or `TargetRuleset`s.

### Equality

Two KeySignature values are equal when all seven effective step alterations are equal.

Sparse input specifying only `F → +1` and total input explicitly assigning zero to the other six steps represent the same value. Equality does not depend on construction syntax, source format, display glyphs, entry order, or sparse versus total storage.

Distinct alterations must not be normalized merely because an external notation might render them similarly.

### Separation from Tonal Interpretation

`KeySignature` describes chromatic defaults, not tonal center, tonic, mode, scale, major/minor identity, or harmonic function. Different tonal interpretations may share one signature.

No tonic, mode, major/minor, or scale fields are introduced. Tonal center, mode, scale, and their relationships remain separate open questions; no finalized types for them are defined here.

### Explicit Pitch and Notation Boundaries

An already-resolved `NoteEvent` retains a fully explicit `Pitch`, independent of signature context:

```text
KeySignature: F → +1; all other steps → 0

F-sharp note → Pitch(F, +1, 4)
F-natural note → Pitch(F, 0, 4)
```

The sharp note must not be stored as `Pitch(F, 0, 4)` with an implicit dependency on the signature. Context-dependent source notation must be resolved at the format boundary into explicit canonical pitches.

The existing distinction between semantic `PitchAlteration` and displayed `Accidental` remains unchanged. Likewise, KeySignature state is separate from displayed key-signature glyphs and accidental ordering.

Glyph order, layout, source spelling, and format-specific tokens are not stored in canonical KeySignature or included in its identity. They belong in notation/source representations when needed.

The domain question is: what `PitchAlteration` applies by default to a given `DiatonicStep`? An operation such as `alterationFor(step)` would illustrate that question only; no public API is finalized.

### Scope and Inheritance

KeySignature remains state at Composition, Part, or Voice scope, with hierarchical inheritance, persistent local overrides, and explicit return to inheritance. This decision defines the value, not timeline storage or override APIs.

See [ADR-0027](../decisions/0027-canonical-key-signature.md).

---

## Open Design Questions

The following points remain unresolved:

### State Representations and Implementation

- Concrete Java implementation of the accepted Tempo value model
- Concrete Java implementation of the accepted Meter value model
- Concrete Java implementation of the accepted KeySignature value model
- Concrete state timeline storage and APIs
- Representation and API for explicitly ending a local override
- Detailed Instrument and InstrumentAssignment models
- Instrument transposition and target-specific instrument mapping

The accepted state scopes and inheritance semantics do not decide these details.

### Tonal Semantics and Format Boundaries

- Tonal center semantics
- Mode semantics
- Scale semantics
- Relationships between tonal center, mode, scale, and key-signature context
- Concrete format mappings and adapters, including ABC, MIDI, MusicXML, and LOTRO

No tonal types or adapter designs are established by the KeySignature decision.

### Tempo Notation, Expressions, and Playback

- MetronomeMark and source/notation representation
- Textual tempo indications and their relationship to quantitative Tempo
- Gradual tempo changes, transitions, and tempo curves
- Fallback policies when quantitative tempo is unspecified
- Playback implementation and PlaybackTime representation
- Technical rounding, clock units, scheduling, MIDI timing, and audio timing

The canonical positive rational rate is accepted; these representations and policies remain open.

### Meter Interpretation and Measure Structure

- Measure and barline model, including `MeasureStructure`
- `MeasurePosition` model and its derivation
- Interchangeable or alternate meter relationships
- Conventional grouping interpretation when explicit grouping is absent

The structural Meter model is accepted; these related models and interpretation rules are not.

### Dynamics

Dynamics remain open in both scope and temporal model. They may involve different temporal forms: `mf` may be state-like, a crescendo span-like, and an accent note- or event-local. These examples do not establish accepted types or scopes.

MIDI velocity is not automatically equivalent to canonical musical dynamics.

### Additional Event Types

Further event types require concrete voice-level musical semantics. No additional event type or ranged abstraction is accepted by this decision.

### Event Storage

The following remain open:

- Concrete event storage implementation
- ID lookup and temporal indexing strategies, including whether and when indexes are needed
- Technical ordering policies for serialization, testing, export, and other processing
- Exact public mutation API and internal delegation mechanisms

### Relations

The following, among others, still need to be modeled separately:

```text
Tie
Slur
TupletGroup
Lyrics association
```

The current intention is to model these structures as relations between events rather than as a deeply nested event hierarchy.

### Sounding Pitch and Tuning

The following still need to be defined:

```text
InstrumentTransposition
SoundingPitch
TuningSystem
MidiMappingPolicy
```

These concepts will not be part of the basic `Pitch` value object.
