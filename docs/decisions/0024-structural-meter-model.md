# ADR-0024 – Meter Preserves Structural Metric Grouping

**Status:** Accepted

## Context

Equal total durations do not imply equal metric structures. A duration-only model would conflate meters such as `6/8` and `3/4`, and would lose explicit additive grouping. A format-specific denominator model would also unnecessarily restrict canonical representation.

The canonical model must distinguish explicit free meter from missing local state, while keeping notation symbols separate from musical semantics.

## Decision

`Meter` is a canonical value object with semantic variants `StructuredMeter` and `FreeMeter`. The exact Java inheritance mechanism and APIs remain implementation details.

### Structured Components

A `StructuredMeter` contains a non-empty ordered sequence of `MeterComponent`s. Each component represents a positive whole-number count of equal musical-duration units forming one explicit component of the meter structure.

Each component has `count > 0` and `unit > 0`. The unit has `MusicalDuration` semantics, not a format-specific denominator integer. The concrete collection and numeric implementation types remain open.

```text
4/4       → [(4, 1/4)]
3/4       → [(3, 1/4)]
6/8       → [(6, 1/8)]
7/8       → [(7, 1/8)]
2+3+2/8   → [(2, 1/8), (3, 1/8), (2, 1/8)]
3+2+2/8   → [(3, 1/8), (2, 1/8), (2, 1/8)]
2/4 + 3/8 → [(2, 1/4), (3, 1/8)]
```

Units are not restricted to powers of two or interchange-format denominator rules. Format-specific restrictions belong to parsers, exporters, validation, or `TargetRuleset`s. Components need not share a unit.

### Grouping and Equality

Explicit component order carries metric grouping and accent semantics and participates in structural equality. `6/8` is not automatically rewritten as `3+3/8`. Conventional grouping may later be derived by an interpretation layer, but must not be stored as if explicitly specified.

The nominal duration is derived as `sum(component.count × component.unit)`. It must not become a redundantly stored independent source of truth.

`Meter.equals` conceptually expresses structural metric equality, preserving component order, counts, and units. Therefore:

```text
6/8 != 3/4
7/8 != 2+3+2/8
7/8 != 3+2+2/8
2+3+2/8 != 3+2+2/8
```

Equal nominal duration does not justify normalization of structurally distinct meters.

### Explicit Free Meter

`FreeMeter` explicitly means no regular meter or free rhythm. It is not represented by `null`, missing state, or absence from the timeline.

With Composition meter `4/4`, a part without local meter inherits `4/4`; a part with local `FreeMeter` explicitly overrides it with free meter. The scope and persistent inheritance rules from [ADR-0022](0022-scoped-musical-state-inheritance.md) and [ADR-0023](0023-musical-state-scopes.md) remain unchanged.

### Notation and Deferred Questions

Common-time and cut-time symbols, graphical time-signature choices, and source spelling are separate from canonical meter. A common-time symbol and numeric `4/4` may map to the same structured meter. ABC syntax preservation belongs in the separate ABC representation; future notation metadata must remain separate from `Meter`.

Interchangeable or alternate meters such as `3/4 (6/8)` are not embedded in the basic value object. Their semantic or notation model remains open, as do conventional grouping interpretation rules.

## Consequences

- Explicit grouping and mixed-unit structures remain representable without format constraints.
- Structural equality preserves distinctions that nominal duration cannot express.
- Free meter remains an explicit state rather than a fallback to inherited meter.
- Java implementation, APIs, timeline storage, and deferred interpretation models remain open.
- Actual measure and barline structure remains separate, as described in [ADR-0025](0025-meter-and-measure-structure.md).
