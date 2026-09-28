# ADR-0025 – Meter Does Not Define Measure Structure by Itself

**Status:** Accepted

## Context

Meter describes metric structure and, for a structured meter, supplies a derived nominal duration. Neither property fully determines actual measure boundaries.

Pickups or anacrusis measures, incomplete or irregular measures, explicit barline deviations, and meter changes that do not align trivially with nominal measure length require additional information. Later form and barline semantics may also affect that structure.

## Decision

Meter and measure/barline structure are separate domain concerns:

```text
Meter                     = metric structure
Measure / Barline structure = separate future domain concern
```

`ScorePosition` and effective meter alone are not sufficient to derive `MeasurePosition` in all cases. A future derivation may conceptually require:

```text
ScorePosition
+ effective Meter
+ MeasureStructure
    ↓
MeasurePosition
```

This is a conceptual dependency, not a finalized type or API design. The concrete `MeasureStructure`, `Measure`, `Barline`, and `MeasurePosition` models remain open and are not designed by this decision.

The accepted Meter scopes and hierarchical inheritance remain unchanged. Different metrical interpretations continue to share the same absolute score timeline.

## Consequences

- Documentation no longer implies that a meter map alone completely determines measure positions.
- Nominal meter duration must not be mistaken for the actual duration of every measure.
- Pickups and irregular measures can be addressed by a future measure model without distorting canonical meter semantics.
- Concrete barline structure, measure positioning, and their APIs remain open.
