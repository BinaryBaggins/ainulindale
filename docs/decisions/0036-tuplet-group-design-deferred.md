# ADR-0036 – TupletGroup Semantics Are Deferred Until Group Membership Requirements Are Clear

**Status:** Accepted

## Context

Canonical rational score time already represents durations such as `1/12` exactly. A tuplet object is not required merely to make such durations representable.

Tuplets nevertheless carry additional musical grouping and structural meaning. Freezing their membership around existing note entities would risk excluding relevant structures.

## Decision

TupletGroup is recognized as relational/grouping semantics, but its detailed structure is deliberately deferred. A future model would preserve grouping information beyond already-resolved rational durations.

`TupletGroup = List<NoteEventId>` is not accepted. Rests are a concrete reason it is too narrow, and the canonical model has no accepted explicit RestEvent. No rest type is introduced by this decision.

The following remain open:

- Group membership
- Ratio representation
- Nesting
- Rests within tuplets
- Relationship to notation
- Whether references identify events, ranges, or other future structures

Detailed TupletGroup design is not required before the first implementation phase. This records a deliberate deferral, not a complete grouping model.

## Consequences

- Exact canonical durations remain usable independently of tuplet grouping objects.
- Future grouping can account for rests and other justified structures without a prematurely fixed member type.
- No membership, ratio, nesting, notation, or rest-representation design is implied.
- The Composition-owned relation framework in [ADR-0034](0034-composition-owned-musical-relations.md) remains available without forcing a concrete TupletGroup shape.
