# ADR-0008 – Shared Absolute Score Timeline

**Status:** Accepted

## Decision

All parts and voices in a composition share the same absolute score timeline.

Voices have no independent local timeline.

## Consequences

Synchronization between parts and voices can be determined directly through `ScorePosition`.
