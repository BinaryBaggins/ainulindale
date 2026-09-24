# Persistence

## Status

The persistence model has not yet been decided. This document records the accepted
constraints that a future persistence design must satisfy.

## Accepted Constraints

- Persistable entities have explicit, stable, typed IDs.
- Identity survives saving and loading.
- Names, metadata changes, and reordering do not change entity identity.

See [ADR-0005](../decisions/0005-stable-typed-domain-identities.md) and the
[domain model](domain-model.md).

The canonical domain model is independent of persistence formats and file paths.
Open files and file paths are outside the `Composition` aggregate.

## Open Questions

- Persistence format and schema
- Serialization and reconstruction of the aggregate
- Storage of references and relations
- Persistence of state outside the composition
