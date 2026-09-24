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
```

The following rules apply:

- `Composition` is the aggregate root.
- A `Part` belongs to exactly one `Composition`.
- A `Voice` belongs to exactly one `Part`.
- A `Part` has at least one `Voice`.
- A `Composition` may contain no parts.

---

## 3. Composition

`Composition` represents the complete canonical musical work.

Conceptually:

```text
Composition
├── metadata
├── ordered Parts
├── global musical state        [later]
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

Instrument assignments and instrument changes will be modeled separately later.

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
```

An object must not belong to multiple parents at the same time.

This does not require bidirectional Java references.

In particular, the following is not required:

```java
voice.getPart().getComposition();
```

Operations that need the context of the entire composition can start at the aggregate root.

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

## 12. Mutability

The canonical aggregate allows controlled mutation.

Mutable collections are not exposed for unrestricted external modification.

The following is not intended:

```java
composition.parts().add(part);
part.voices().clear();
```

Instead, structural changes use defined domain operations:

```java
composition.addPart(...);
composition.removePart(...);
composition.movePart(...);

part.addVoice(...);
part.removeVoice(...);
part.moveVoice(...);
```

This keeps domain invariants centrally protected.

Value objects remain immutable.

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

Meter provides a metrical interpretation of that position.

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

Conceptually:

```text
ScorePosition
    ↓ meter map
MeasurePosition
```

Tempo and meter interpret the timeline without moving existing events.

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

A `NoteEvent` is an entity within exactly one voice.

It has at least:

```text
EventId
ScoreRange
Pitch
```

Conceptually:

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

## Open Design Questions

The following points have not yet been settled for the canonical model:

### VoiceEvent Abstraction

It remains to be decided whether a voice generally contains:

```text
VoiceEvent
```

with `NoteEvent` as merely the first concrete event type.

In particular, the following need clarification:

- Which event types actually have voice-level semantics
- Whether all VoiceEvents have a time position
- Whether a further abstraction such as `RangedVoiceEvent` is useful

### Event Storage

The following remain open:

- Collection semantics within a voice
- ID lookup
- Temporal indexing
- Deterministic ordering of simultaneous events

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
