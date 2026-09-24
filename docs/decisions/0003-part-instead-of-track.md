# ADR-0003 – Part Rather Than Track as the Canonical Musical Concept

**Status:** Accepted

## Context

The term `Track` has different meanings in:

- MIDI
- User interfaces
- Existing Ainulindalë models

These meanings do not necessarily correspond to a musical part.

## Decision

The canonical domain model uses `Part`.

`Track` is not used as a general domain type.

Technical or visual concepts may still use names such as:

```text
MidiTrack
UiTrackLane
```

## Consequences

The domain model is not coupled to MIDI track semantics.
