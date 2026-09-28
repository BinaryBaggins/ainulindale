# ADR-0034 – Musical Relations Are Composition-Owned Entities

**Status:** Accepted

## Context

Musical relations may refer across Voice or Part boundaries. Making a voice their general owner would conflate the ownership of events with the structure connecting them.

Different relations also require different references and invariants rather than one untyped member list.

## Decision

Musical relations are canonical entities owned directly by Composition. Each has its own stable typed RelationId within the Composition, independent of referenced entity identities. The exact ID implementation remains open.

Relations refer to existing canonical entities through stable typed IDs. They never acquire shared ownership; a Voice remains the owner of a NoteEvent referenced by a relation.

A minimal MusicalRelation abstraction may expose only identity. This is a semantic boundary, not a finalized Java interface. Concrete relation types model their own references and invariants. A universal `RelationType + List<EventId> members` structure is not introduced.

Every referenced entity must exist in the same Composition. Dangling references are invalid canonical state. Composition protects this integrity by validating existence, correct referenced types, and relation-specific invariants at its mutation boundary.

External callers must not insert or mutate relations through unrestricted mutable collections. Exact aggregate APIs remain open.

Removing a referenced entity must preserve canonical integrity, but there is no universal deletion policy. Rejection, deletion of affected relations, or semantically valid transformation remain relation-specific future choices.

Collection/index order does not establish musical ordering. Deterministic serialization, testing, export, or persistence ordering is technical rather than automatically canonical semantics. Storage and indexing remain implementation details.

## Consequences

- Relations can refer across lower-level ownership boundaries without becoming event owners.
- Stable relation identity is independent of endpoints or other references.
- Composition enforces referential integrity alongside type-specific invariants.
- Mutation APIs, deletion policies, storage, and technical ordering remain open.

Tie and Slur are defined in [ADR-0035](0035-tie-and-slur-relations.md); detailed TupletGroup design is deferred by [ADR-0036](0036-tuplet-group-design-deferred.md).
