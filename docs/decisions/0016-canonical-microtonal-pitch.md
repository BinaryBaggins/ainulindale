# ADR-0016 – Microtonal Pitch Semantics Remain Canonical

**Status:** Accepted

## Decision

Microtonal alterations are not categorically relegated to playback or view state.

If an alteration is part of the musical meaning, it belongs in the canonical `Pitch`.

## Consequences

Targets without corresponding support must validate, transform, or reject such pitches.
