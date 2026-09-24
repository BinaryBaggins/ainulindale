# ADR-0017 – NoteEvent as an Entity

**Status:** Accepted

## Decision

A `NoteEvent` is an entity within a voice.

It has at least:

```text
EventId
ScoreRange
Pitch
```

A regular NoteEvent has a strictly positive duration.

## Consequences

Multiple musically identical NoteEvents can be distinguished by their different IDs.
