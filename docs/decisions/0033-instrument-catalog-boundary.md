# ADR-0033 – External Instrument Catalogs Provide Definitions Without Owning Composition Semantics

**Status:** Accepted

## Context

A future external InstrumentCatalog can support standard and LOTRO instrument definitions, reusable metadata, user-facing selection, and library integration. Live catalog dependency would make saved compositions vulnerable to unavailable services or changed entries.

## Decision

Catalogs provide definitions; they do not own the canonical meaning of an already-stored composition.

```text
External Instrument Catalog
    ↓ select/import
Locally retained InstrumentDefinition
    ↓ referenced by
InstrumentAssignment
```

The Composition or Project retains a local semantic snapshot/copy of the instrument definition it uses. Saved musical meaning must remain interpretable without catalog access, after an entry changes, and when newer catalog versions exist. Catalog updates must not silently alter that meaning.

Whether local definitions belong directly to Composition or to a future Project layer remains open. This decision does not prescribe persistence ownership or storage.

Optional provenance may later include source catalog, entry, or version identifiers. These are illustrative metadata possibilities, not a finalized schema, and are unnecessary for understanding the locally retained musical semantics.

> Catalog provides definitions; the Composition/Project retains the musical meaning it actually uses.

The architecture anticipates this boundary from the beginning, while full catalog implementation is deliberately deferred. No catalog module, persistence, network service, synchronization, version-resolution logic, or catalog-browser UI is introduced by this task.

## Consequences

- Existing compositions retain stable instrument semantics independently of catalog availability or updates.
- Catalogs remain reusable sources rather than live semantic dependencies.
- Local persistence ownership, provenance schema, catalog implementation, and target-specific mappings remain open.
- This boundary does not design adapters or temporary instrument setup models.
