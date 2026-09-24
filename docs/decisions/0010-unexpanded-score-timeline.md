# ADR-0010 – Unexpanded Score Timeline

**Status:** Accepted

## Decision

Repeats and other form structures do not automatically duplicate events in the canonical score timeline.

A future `PlaybackPlan` expands:

```text
Score timeline
+
MusicalForm
```

into a concrete performance order.

## Consequences

The notated structure and the playback structure remain distinguishable.
