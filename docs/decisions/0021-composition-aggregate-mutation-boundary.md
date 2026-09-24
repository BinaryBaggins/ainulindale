# ADR-0021 – Composition as the Aggregate Mutation Boundary

**Status:** Accepted

## Context

[ADR-0002](0002-composition-as-domain-root.md) establishes `Composition` as the canonical aggregate root, and [ADR-0006](0006-controlled-aggregate-mutation.md) permits controlled mutation of entities.

The composition-wide event identity and ownership rules in [ADR-0020](0020-voice-event-abstraction.md) require a mutation boundary that can enforce invariants across nested entities. Unrestricted external mutation of a part, voice, or event could bypass those checks.

## Decision

`Composition` remains the aggregate root and is the public mutation boundary for its aggregate. External structural and domain mutations enter through `Composition`.

Conceptually:

```java
composition.addVoice(partId, ...);
composition.addNote(voiceId, range, pitch);
composition.removeEvent(eventId);
composition.changeNotePitch(eventId, pitch);
```

These examples illustrate the boundary only. The exact API is not decided.

Internally, `Composition` may delegate to `Part` or `Voice`. Nested entities may have internal or package-private mutation operations, but they are not unrestricted public mutation boundaries. External callers must not bypass the aggregate root by directly mutating nested entities or their collections.

This boundary protects:

- Composition-wide `EventId` uniqueness
- Valid ownership
- Future relation integrity
- Future cross-entity invariants

The details of future relations and cross-entity rules are not decided here.

The domain provides primitive, invariant-safe mutations. Higher-level user editing operations, such as transpose, quantize, move selection, and duplicate section, belong to the future editing layer. Higher-level editing transactions and transactional undo/redo also remain outside the domain aggregate; their contracts and implementation remain open.

## Consequences

- Calling code uses the aggregate root for mutations even when a change affects a nested entity.
- Internal delegation remains possible without weakening the public boundary.
- Read access must not provide an unrestricted way to mutate nested entities.
- The earlier direct `part.addVoice(...)` example is refined to illustrate entry through `composition.addVoice(partId, ...)` instead.
- Exact method signatures, internal delegation mechanisms, and editing transaction design remain open.
