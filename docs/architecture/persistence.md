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

[ADR-0041](../decisions/0041-project-arrangements-revisions-and-instrument-library.md)
accepts Project-scoped shared ownership of locally retained InstrumentDefinitions,
while leaving technical storage and aggregate boundaries open. Each Arrangement
has its own canonical Composition and may have its own revision history. Future
design must support preserving, comparing, and restoring Arrangement revisions,
as well as comparing different Arrangements.

Historical revisions must remain interpretable using the definitions relevant to
their recorded musical state. External catalog updates must not silently alter
locally retained semantics. A Composition must be transferable with its required
definitions without permanent access to its originating Project or a catalog.
These constraints choose no revision-storage or definition-versioning mechanism.

## Open Questions

- Persistence format and schema
- Serialization and reconstruction of the aggregate
- Storage of references and relations
- Persistence of state outside the composition
- Revision capture, storage, restoration, and comparison mechanisms
- Definition lifecycle/versioning and preservation of historical instrument semantics
- Technical storage of the shared Project instrument library and its references
- Standalone Composition exchange format, packaging, and import/export APIs
