# Editing Model

## Status

This document collects the accepted editing constraints. The editing model is still
being designed; transactions and undo/redo remain open decisions.
Their placement in editing is accepted; their exact semantics and implementation remain open.

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

## Domain Operations and Editing Orchestration

Selection-driven operations belong to editing. Selection is not Composition state.
Transpose or quantize selection, move selected events, duplicate section, paste,
and undo/redo are higher-level operations outside the canonical aggregate.

Editing may resolve session state into stable domain IDs and orchestrate multiple
primitive Composition operations. Each public domain operation preserves aggregate
validity atomically and may affect multiple entities when required by its semantics.
This does not determine the transaction semantics of a group of editing operations.

Transaction grouping and undo/redo belong to editing. Composition does not expose
a generic begin/commit/rollback transaction API. Exact grouping, failure recovery,
and undo/redo implementation remain open.

See [ADR-0037](../decisions/0037-composition-public-api-boundary.md).

## Open Questions

- Editing transactions and their atomicity
- Undo/redo scope and implementation
- Editing policy contracts
- The concrete separation of editing, workspace, and UI state
