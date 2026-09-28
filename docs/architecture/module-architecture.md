# Module Architecture

## Status

Maven multi-module is accepted as the target architecture for the implementation
phase. The actual restructuring is deferred until implementation begins.
See [ADR-0019](../decisions/0019-maven-multi-module-architecture.md).

## Planned Module Boundaries

```text
ainulindale-domain
ainulindale-editing
ainulindale-abc
ainulindale-midi
ainulindale-desktop
```

Additional modules require a concrete architectural responsibility and a justified module boundary.

The new domain and editing system must not depend on legacy models.
See [ADR-0001](../decisions/0001-greenfield-domain-rebuild.md).

## Reuse of Domain-Independent Infrastructure

Greenfield applies to the musical domain and editing architecture, not to mandatory
rewrites of suitable independent infrastructure. Existing Result, i18n, and settings
infrastructure should be reused when compatible with the new dependency boundaries.

Reuse must not require dependencies on legacy musical/editor types such as
EditorTrack, TrackEditorModel, or EditorWorkspace. Infrastructure may be moved,
minimally adapted, or separated from legacy dependencies during implementation;
its current physical location is not a requirement.

The existing Result abstraction is a candidate for explicit domain rejection,
carrying new domain-specific semantic errors without a second competing framework.
No exact generic signature is prescribed.

i18n remains application/UI infrastructure. It translates semantic domain errors
outside the domain; canonical musical types do not depend on localized strings,
ResourceBundle semantics, or UI locale. Settings likewise remain outside Composition:
UI behavior, editor preferences, locale choice, shortcuts, and application configuration
are application/user concerns.

Final Maven placement of Result, i18n, and settings remains open for module architecture
work. No generic `ainulindale-common`, `ainulindale-core`, or `ainulindale-utils` module
is accepted merely to collect shared utilities. Module boundaries should have focused
responsibilities.

See [ADR-0038](../decisions/0038-reuse-domain-independent-infrastructure.md).

## Open Questions

- Detailed module responsibilities and dependency graph
- Public interfaces and package boundaries
- Concrete Maven placement and any necessary adaptation of Result, i18n, and settings
- The concrete transition from the legacy application to the new application path
