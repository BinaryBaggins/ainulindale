# ADR-0037 – Composition Exposes Intent-Based Mutations Through the Aggregate Root

**Status:** Accepted

## Context

Composition is already the aggregate root and mutation boundary. Its public API must protect invariants without forcing flat read APIs, generic collection CRUD, or editor/session concepts into canonical musical state.

## Decision

Composition is the sole public mutation boundary. Nested Part, Voice, VoiceEvent, NoteEvent, and MusicalRelation entities may be read externally, but must not expose unrestricted setters or mutable collections that bypass the root. Internal/package-restricted mutation is allowed; exact Java visibility remains open.

Reads may traverse the aggregate naturally. Queries such as `composition.parts()`, `part.voices()`, `voice.events()`, `note.pitch()`, and `note.range()` are illustrative; immutable views or snapshot mechanisms are not selected here.

Mutations express domain intent and address existing entities by stable typed IDs, not Java object identity or mutable entity references as their primary boundary. Creation makes the new identity available to the caller. Exact names, parameters, and return/result shapes remain open.

Normal creation generates new identities. Persistence rehydration restores existing identities and is a separate concern. Factories, builders, repository mappers, or dedicated reconstruction paths remain possibilities. Persistence-oriented methods such as `addPartWithId(...)` are not introduced into normal editing without implementation evidence justifying them.

### Operation Responsibility and Atomicity

Primitive domain operations preserve musical invariants for Part/Voice structure, notes, scoped state, relations, and instruments. These are categories, not a complete API catalog. A single operation leaves the aggregate valid atomically from the caller's perspective, without observable intermediate invalid state. It may affect multiple entities when required by domain semantics.

Selection-driven operations, paste, duplicate section, quantization, editing transactions, and undo/redo belong to editing. Selection is not canonical Composition state. Editing may resolve editor state into IDs and orchestrate multiple domain operations.

Composition exposes no generic begin/commit/rollback transaction API. Editing owns higher-level grouping; the domain owns consistency of individual operations. Grouped editing semantics remain open.

### Failures

Expected domain rejection is explicit result-style information: for example, unknown IDs, last-voice removal, invalid ties, referential-integrity rejection, or invalid state transitions. These examples do not choose a universal relation deletion policy.

Programming-contract violations and broken internal invariants are distinct and may use exceptions where appropriate. Domain errors are programmatically distinguishable semantic values, not localized message strings. The exact hierarchy remains open.

The existing Result abstraction should be reused where suitable and domain-independent, as specified in [ADR-0038](0038-reuse-domain-independent-infrastructure.md). No generic signature or new result framework is introduced.

## Consequences

- Read traversal remains natural while mutation stays centralized and invariant-safe.
- Identity-based intent avoids coupling commands to mutable object references.
- Persistence reconstruction and editing orchestration remain distinct from ordinary primitive domain operations.
- Exact Java APIs, read mechanisms, errors, rehydration, and editing transactions remain open.
