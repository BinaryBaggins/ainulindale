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

### Initial Representation and Creation

The first implementation uses a separate ID type backed by a non-null UUID for each entity identity. UUID is a technical representation, not musical meaning. Initial generation uses UUIDv4 via `UUID.randomUUID()`; no central numeric sequence is introduced.

No common `DomainId` base class is introduced solely for technical uniformity. Public identities remain typed values rather than raw UUIDs or untyped strings.

Accepted validity and uniqueness scopes remain unchanged. In particular, EventId uniqueness is composition-wide; using UUIDs does not promote local uniqueness requirements into a new global domain invariant.

Normal domain creation generates new identities. Persistence rehydration restores existing identities. Exact method names and reconstruction mechanisms remain implementation details, not decisions of this slice.

## Consequences

- Saving and loading can preserve stable references.
- Relations can reference entities unambiguously.
- Static typing helps prevent assignments between incompatible ID types.
