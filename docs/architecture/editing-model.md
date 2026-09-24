# Editing Model

## Status

This document collects the accepted editing constraints. The editing model is still
being designed; transactions and undo/redo remain open decisions.

## Accepted Constraints

- The new editing system is developed alongside the legacy system and must not
  depend on legacy models. See [ADR-0001](../decisions/0001-greenfield-domain-rebuild.md).
- Active state, visibility, and selection reside outside `Composition`.
  See [ADR-0002](../decisions/0002-composition-as-domain-root.md).
- Entities allow controlled mutation through explicit operations; value objects
  remain immutable. See [ADR-0006](../decisions/0006-controlled-aggregate-mutation.md).
- Editing grids are editing state rather than canonical domain state.
  See [ADR-0007](../decisions/0007-exact-rational-score-time.md).
- Overlap and duplicate-note restrictions may be imposed by editing policies.
  The canonical domain permits these states.
  See [ADR-0018](../decisions/0018-overlapping-and-duplicate-notes.md).

## Open Questions

- Editing transactions and their atomicity
- Undo/redo scope and implementation
- Editing policy contracts
- The concrete separation of editing, workspace, and UI state
