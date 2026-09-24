# ADR-0004 – Voice as a Fundamental Musical Layer

**Status:** Accepted

## Decision

Every part has at least one `Voice`.

A voice represents a logically independent stream of musical events.

A voice is not inherently monophonic.

## Consequences

- Chords within a voice are possible.
- Multiple voices per part can later be edited independently.
- ABC voices can be mapped to domain voices without requiring the two concepts to be identical.
