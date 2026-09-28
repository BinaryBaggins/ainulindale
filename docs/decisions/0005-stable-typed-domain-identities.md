# ADR-0005 – Stable, Typed Domain Identities

**Status:** Accepted

## Decision

Persistable entities have stable, explicit IDs.

At least the following are planned:

```text
CompositionId
PartId
VoiceId
EventId
```

IDs are modeled as distinct types rather than passed through the domain as untyped strings or UUIDs.

## Consequences

- Saving and loading can preserve stable references.
- Relations can reference entities unambiguously.
- Static typing helps prevent assignments between incompatible ID types.
