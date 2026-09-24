# ADR-0018 – The Domain Allows Overlapping and Duplicate Notes

**Status:** Accepted

## Decision

The fundamental composition model does not prohibit:

- Overlapping notes
- Identical pitches at the same time
- Multiple NoteEvents with identical ScoreRange and Pitch

## Consequences

Constraints of this kind belong in:

- Editing policies
- TargetRulesets
- Export validation

They do not belong in the rules governing fundamental domain representability.
