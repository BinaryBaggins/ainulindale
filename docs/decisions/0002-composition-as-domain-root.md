# ADR-0002 – Composition as the Canonical Aggregate Root

**Status:** Accepted

## Context

The previous model was heavily focused on tracks and editor state.

Future requirements call for a clear musical root entity.

## Decision

`Composition` becomes the aggregate root of the canonical musical domain model.

The fundamental hierarchy is:

```text
Composition
└── Part
    └── Voice
```

`Composition` owns the persistent musical structure.

Editor and session state do not belong in this aggregate.

## Consequences

- Part membership and part order are domain state.
- Voice membership and voice order are domain state.
- Active state, visibility, and selection reside outside the composition.
- Future global musical structures have a clear parent.
