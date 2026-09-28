# ADR-0038 – Reuse Suitable Domain-Independent Infrastructure Across the Greenfield Architecture

**Status:** Accepted

## Context

The greenfield musical domain and editing design must not depend on legacy models such as EditorTrack, TrackEditorModel, or EditorWorkspace. That requirement does not justify rebuilding infrastructure that is already suitable and independent.

## Decision

> Rebuild the domain architecture, not infrastructure that is already suitable and independent.

Existing Result, i18n, and settings infrastructure are explicit reuse candidates. Reuse is conditional on fitting the new dependency boundaries without legacy musical/editor coupling. Components may be moved, minimally adapted, or separated from legacy dependencies; current physical placement need not be preserved.

### Result and Semantic Errors

Reuse the existing Result abstraction for predictable domain rejection where suitable. Do not introduce a competing result framework merely because the domain is rebuilt, and do not assume an exact generic signature.

The new domain defines its own programmatically distinguishable semantic error values. LastVoiceRemoval, EntityNotFound, InvalidTie, and ReferencedEntity are illustrative categories, not a finalized hierarchy. Expected rejection differs from programming errors or broken internal invariants, for which exceptions may be appropriate.

### Localization and Settings

Existing i18n infrastructure is reused for application/UI concerns. Mapping domain errors to localized messages happens outside the domain. Localized strings, ResourceBundle semantics, UI locale, and translated messages are not canonical error contracts or musical-domain dependencies.

Existing settings infrastructure should likewise be reused where suitable. UI behavior, editor preferences, locale, shortcuts, and application configuration are application/user settings, not Composition state.

### Module Boundaries

Concrete Maven placement is deferred to module architecture work. This decision accepts no generic common/core/utils module to host shared code. Reuse should fit focused responsibilities and justified dependency boundaries.

## Consequences

- Greenfield musical design can retain proven independent infrastructure without retaining legacy domain coupling.
- Domain failures remain semantic and independently localizable.
- Settings and i18n remain outside canonical Composition state.
- Exact error types, necessary infrastructure adaptations, and Maven placement remain open.
