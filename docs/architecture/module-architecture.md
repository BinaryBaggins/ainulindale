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

Additional modules require a concrete domain requirement.

The new domain and editing system must not depend on legacy models.
See [ADR-0001](../decisions/0001-greenfield-domain-rebuild.md).

## Open Questions

- Detailed module responsibilities and dependency graph
- Public interfaces and package boundaries
- The concrete transition from the legacy application to the new application path
