# ADR-0020 – VoiceEvent as the Voice-Level Event Abstraction

**Status:** Accepted

## Context

The canonical hierarchy is `Composition → Part → Voice`, with `NoteEvent` as the first concrete musical event. Defining a voice solely in terms of notes would unnecessarily restrict future event types.

A common abstraction is justified now by shared voice ownership, stable identity, and temporal anchoring. It must not force every future event to have the positive score duration required by a regular note.

## Decision

`VoiceEvent` is the common abstraction for musical events owned by exactly one `Voice`.

Every `VoiceEvent` has:

- A stable `EventId`, unique within the entire `Composition`
- A `ScorePosition` that anchors it on the shared score timeline
- Exactly one owning voice

Conceptually:

```java
public interface VoiceEvent {

    EventId id();

    ScorePosition position();
}
```

This example illustrates the semantic contract only. It does not prescribe the final Java interface or class shape.

Identity permits unambiguous references across the composition. Temporal anchoring identifies where the event occurs without requiring it to occupy a duration. A `VoiceEvent` therefore need not have a `ScoreRange`.

`NoteEvent` is the first concrete type because it is already justified by the accepted note model. It retains `EventId`, `ScoreRange`, and `Pitch`, with a strictly positive duration. Its event position corresponds to `ScoreRange.start()`.

`RangedVoiceEvent` is deliberately deferred. A common ranged abstraction may be introduced when multiple concrete event types justify it. No additional event type is accepted here.

An event must never be owned by multiple voices. Other structures refer to it through `EventId`, not shared object ownership.

### Collection Semantics

The event collection has no intrinsic musical list order. Insertion order and collection index do not define musical ordering; temporal ordering is derived from event positions.

Events at the same `ScorePosition` are simultaneous and have no intrinsic order unless future domain semantics explicitly introduce one. Deterministic ordering for serialization, testing, export, or other technical processing does not automatically become musical semantics.

### Storage and Indexing

The canonical contract is independent of storage and is not defined as `List<VoiceEvent>`. An ID-based store such as `EventId → VoiceEvent` is a possible starting point, not a requirement.

Temporal or composition-wide lookup indexes may be added for performance. They must remain derived indexes, never a second source of truth. Canonical ownership remains `Composition → Part → Voice → VoiceEvent`.

### Scope

Relations, tempo, meter, key signatures, and instrument assignments are not automatically `VoiceEvent`s. Their musical relevance alone does not establish ownership by exactly one voice or voice-level event semantics. Their respective scopes and models require separate decisions.

## Consequences

- The domain has an extension point for justified voice-level event types without a speculative event hierarchy.
- Event identity is composition-wide while ownership remains local to exactly one voice.
- Event types can share temporal anchoring without sharing duration semantics.
- Storage can evolve without changing canonical collection semantics.
- Concrete storage, indexing, technical ordering policies, and Java API choices remain open.

The aggregate mutation boundary protecting these invariants is defined in [ADR-0021](0021-composition-aggregate-mutation-boundary.md).
