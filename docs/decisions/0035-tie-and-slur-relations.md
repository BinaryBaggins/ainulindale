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

Any operation that would violate an existing Tie's invariants must be rejected. Changes that preserve these invariants remain allowed. Removing either referenced note, directly or through removal of its containing Voice or Part, must be rejected while the Tie exists.

Same-Voice restriction is currently imposed.

Future notation rules, editing policies, target validation, or a refined relation decision may lift this restriction if justified by concrete requirements.

### Slur

Slur contains RelationId, startNoteId, and endNoteId. Both anchors reference distinct existing NoteEvents within the same Composition, and the start note occurs strictly before the end note in canonical score time. Tie-like adjacency and pitch equality are not required.

Any operation that would violate an existing Slur's invariants must be rejected. Changes that preserve these invariants remain allowed. Removing either referenced note, directly or through removal of its containing Voice or Part, must be rejected while the Slur exists.

Changing an anchor's pitch or duration is permitted provided all applicable domain invariants remain satisfied. Changing its score position must preserve the strict chronological ordering of the Slur's anchors.

Temporally intermediate events are not redundantly stored as a mandatory membership list. They may be determined from canonical score structure where needed; the detailed selection semantics are not designed here.

Cross-Voice Slurs are currently permitted by the domain implementation, including moving an existing anchor between Voices. This is a provisional implementation policy, not a finalized musical requirement.

Same-Voice requirements, cross-Voice restrictions, nesting, overlapping Slur restrictions, notation placement, engraving direction, and playback articulation remain open. Visual curve data and engraving geometry are not part of canonical Slur.

## Consequences

- Tie preserves explicit pitch continuity and exact temporal adjacency.
- Slur preserves its two anchors without duplicating intermediate membership state.
- Neither relation introduces shared ownership. Ties are restricted to notes within the same Voice.
- Some detailed interpretations and behaviors remain open.
