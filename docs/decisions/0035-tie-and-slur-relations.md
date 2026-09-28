# ADR-0035 – Tie and Slur Use Explicit NoteEvent References

**Status:** Accepted

## Context

Tie and Slur have different musical semantics and invariants. Explicit typed references preserve those differences without shared event ownership or a universal relation member list.

## Decision

Both types are Composition-owned entities with RelationId under [ADR-0034](0034-composition-owned-musical-relations.md). Their EventId references must resolve to NoteEvents within the same Composition.

### Tie

Tie contains RelationId, sourceNoteId, and targetNoteId. It is a directed relation between exactly two distinct NoteEvents:

```text
source.id != target.id
source.pitch == target.pitch
source.range.end() == target.range.start()
```

Pitch equality is canonical structural equality, not MIDI-number or sounding-frequency equivalence. Enharmonic acoustic equivalence alone is insufficient.

Exact adjacency expresses continuous continuation: gaps and overlaps are invalid. The two notes retain independent identities and are not merged by the Tie.

No same-Voice restriction is currently imposed. Future notation rules, editing policies, target validation, or a refined relation decision may impose a restriction if justified by concrete requirements.

### Slur

Slur contains RelationId, startNoteId, and endNoteId. Both anchors reference distinct existing NoteEvents, and the start note occurs before the end note in canonical score time. Tie-like adjacency is not required.

Temporally intermediate events are not redundantly stored as a mandatory membership list. They may be determined from canonical score structure where needed; the detailed selection semantics are not designed here.

Same-Voice requirements, cross-Voice restrictions, nesting, overlapping Slur restrictions, notation placement, engraving direction, and playback articulation remain open. Visual curve data and engraving geometry are not part of canonical Slur.

## Consequences

- Tie preserves explicit pitch continuity and exact temporal adjacency.
- Slur preserves its two anchors without duplicating intermediate membership state.
- Neither relation introduces shared ownership or current same-Voice restrictions.
- Detailed interpretation, editing APIs, and deletion/cascade behavior remain open.
