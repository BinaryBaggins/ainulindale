# ADR-0042 – Atomic Batch Pitch Changes Within Composition

**Status:** Accepted until superseded

## Context

ADR-0037 establishes `Composition` as the sole public mutation boundary for canonical musical state. Each domain operation must preserve aggregate invariants atomically.

Currently, `Composition.changeNotePitch(EventId, Pitch)` changes one note at a time. Existing Tie invariants require tied notes to have structurally equal pitches.

This prevents a valid editing operation in which both notes of a Tie are transposed together. Changing either note individually would temporarily violate the Tie invariant, even though the intended final state is valid.

Editing must not bypass aggregate validation or expose temporarily invalid musical state.

## Decision

### Atomic Batch Pitch Changes

`Composition` provides an additional operation:

```java
Result<Unit> changeNotePitches(Map<EventId, Pitch> changes);
```

The map associates existing note identities with their intended replacement pitches.

The operation validates the complete intended final state before applying any mutations.

All requested changes succeed together, or the entire operation fails without modifying the Composition.

### Validation

The operation must:

1. Reject a null map or null keys and values with `NullPointerException`.
2. Verify that every referenced `EventId` identifies an existing NoteEvent.
3. Evaluate affected Tie pitch-equality invariants against the complete proposed final state.
4. Reject the entire operation if any required invariant would be violated.
5. Apply all requested pitch changes only after successful validation.

Expected domain failures use the existing `Result` and semantic `CompositionErrors` contracts.

Validation is fail-fast. Where multiple failures exist, no universal error precedence is required.

### Semantics

- An empty map succeeds without changing the Composition.
- Setting a note to its existing pitch succeeds without changing that note.
- Note identities, score ranges, and Voice ownership remain unchanged.
- The ordering of map entries does not affect the resulting musical state.
- Enharmonic equivalence does not replace structural Pitch equality.
- Affected Tie invariants are evaluated using the final pitches of both endpoints, regardless of whether one or both are included in the map.
- Failed operations leave all notes and relations unchanged.
- The operation does not modify or remove Ties to make an otherwise invalid change succeed.

### Relationship to Single-Note Changes

The existing operation remains supported:

```java
Result<Unit> changeNotePitch(EventId noteId, Pitch newPitch);
```

It retains its current public contract, including Tie validation.

Both operations should share validation and mutation logic where practical, avoiding divergent invariant enforcement.

Delegating the single-note operation to the batch operation is an acceptable implementation strategy.

### Scope and Responsibility

The batch operation belongs to `Composition` because it must validate invariants spanning multiple NoteEvents within the aggregate.

The editing layer remains responsible for interpreting user selections and editing intentions, such as transposing selected notes by a given interval.

The editing layer provides the intended replacement pitches; it does not mutate nested domain entities directly.

This decision introduces no generic transaction API, snapshot mechanism, rollback engine, or undo/redo implementation.

## Consequences

- Tied notes can be transposed together without exposing an invalid intermediate state.
- Multi-note pitch changes have explicit all-or-nothing semantics.
- Existing stable EventIds are preserved.
- Composition retains responsibility for invariant enforcement.
- Editing can orchestrate multi-note pitch operations without bypassing the aggregate boundary.
- Future batch operations may be introduced when concrete use cases require them, but are not mandated by this decision.

## Open Questions

- General editing transaction grouping across different operation types.
- Undo/redo implementation and history semantics.
- Snapshot, reconstruction, or copy-on-write mechanisms.
- Concurrency and coordination of edits from multiple callers.
- Whether future complex mutations justify a general controlled aggregate mutation mechanism.

## References

- [ADR-0021 – Composition Aggregate Mutation Boundary](0021-composition-aggregate-mutation-boundary.md)
- [ADR-0034 – Composition-Owned Musical Relations](0034-composition-owned-musical-relations.md)
- [ADR-0035 – Tie and Slur Relations](0035-tie-and-slur-relations.md)
- [ADR-0037 – Composition Public API Boundary](0037-composition-public-api-boundary.md)
- [Editing Model](`docs/architecture/editing-model.md`)
