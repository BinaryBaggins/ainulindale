# ADR-0006 – Controlled Aggregate Mutation

**Status:** Accepted

## Decision

The domain is not modeled as a fully immutable object graph.

Entities allow controlled mutation.

Value objects remain immutable.

Structural collections are not exposed for unrestricted mutation.

## Consequences

Explicit mutation operations can protect domain invariants without requiring the entire object graph to be replaced on every edit.
