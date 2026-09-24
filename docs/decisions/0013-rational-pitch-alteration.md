# ADR-0013 – PitchAlteration as a Rational Chromatic Unit

**Status:** Accepted

## Decision

`PitchAlteration` uses a normalized rational number in the abstract unit semitone.

Examples:

```text
0     natural
+1    sharp
-1    flat
+2    double sharp
+1/2  quarter sharp
```

Floating-point numbers are not used.

## Consequences

Microtonal alterations can be represented structurally.

Their specific frequency meaning is only determined by a tuning system.
