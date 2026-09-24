# ADR-0019 – Maven Multi-Module as the Target Architecture

**Status:** Accepted for implementation phase

## Decision

When implementation begins, the new architecture is to be set up as a Maven multi-module project.

The initially planned logical module boundaries are:

```text
ainulindale-domain
ainulindale-editing
ainulindale-abc
ainulindale-midi
ainulindale-desktop
```

Additional modules will only be introduced for concrete domain requirements.

## Consequences

Module boundaries are already considered during planning.

The actual Maven restructuring will take place when moving from planning to implementation.
