# ADR-0022 – Scoped Musical State Uses Hierarchical Inheritance

**Status:** Accepted

## Context

Musical state may apply at different scopes. Copying a parent value into every child would duplicate canonical state and require synchronization whenever the parent changes. Local exceptions also need to survive later parent changes without losing their meaning.

Semantic scope and temporal shape are independent questions. `State`, `Point`, `Span`, and `Relation` describe temporal shapes; they do not define a universal musical-event hierarchy. Domain-specific types remain explicit.

## Decision

For state types permitted at multiple hierarchical scopes, fallback follows `Composition → Part → Voice`. At a position, a scope uses its active local value; otherwise, it inherits the effective parent value.

State changes conceptually map `ScorePosition → value`. Within the same timeline, scope, and state type, at most one value may be established at a position. The effective local value is the latest local change at or before the requested position, unless the local override has explicitly ended.

A value at `ScorePosition.ZERO` is not required. Absent state is valid and may be inherited or remain unspecified. This decision introduces no default value.

### Persistent Local Overrides

A local value remains an override until changed or explicitly ended. Later parent changes do not replace an active local override.

For example, if a composition establishes C major at position 0 and D major at position 20, a part with no override follows both changes. A part that establishes G major at position 10 retains G major after position 20.

### Explicit Return to Inheritance

An override must be explicitly removable. After it ends, the child dynamically inherits the effective parent value at that position and follows subsequent parent changes until another local override becomes active.

The representation of an override ending and the exact API remain undecided.

### Derived Inheritance

Inherited state is derived, not persisted as duplicated child state. A parent change is stored once in its own timeline. Child timelines do not materialize inherited values.

Concrete data structures and APIs remain implementation details. An internal helper such as `StateTimeline<T>` may be considered later, but it is not canonical musical vocabulary. No universal `ScopedEvent`, `MusicalContextEvent`, or equivalent untyped abstraction is introduced.

The permitted scopes for each accepted state type are defined in [ADR-0023](0023-musical-state-scopes.md). Inheritance does not grant additional scopes to a type.

## Consequences

- Parent changes propagate through inheritance without synchronizing duplicated child state.
- Active local overrides remain stable despite parent changes.
- Ending an override restores dynamic inheritance rather than a copied parent snapshot.
- State resolution must account for position, local override lifetime, and effective parent state.
- Storage, concrete APIs, and the representation of override endings remain open.
