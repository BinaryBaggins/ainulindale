# ADR-0040 – Foundation Contains Only Minimal Domain-Independent Shared Primitives

**Status:** Accepted

## Context

The existing Result abstraction can serve multiple architectural modules without belonging to musical semantics. Placing it in domain would force otherwise independent consumers, including legacy after extraction, to depend on the new musical model solely to obtain a technical primitive.

A shared module is justified only with strict limits that prevent a generic utility collection.

## Decision

Introduce `ainulindale-foundation` as a deliberately minimal module. Initial intended content is the existing Result abstraction, reused/extracted rather than replaced, provided legacy musical/editor coupling is absent or removed through suitable minimal adaptation.

Admission requires all of:

- Domain independence
- Application independence
- UI independence
- Genuine sharing across architectural boundaries

The accepted name foundation denotes this focused responsibility, not vague core/common/utils semantics. It must remain very small and depend on no higher-level project module.

ScorePosition, Pitch, DomainError, instrument IDs, and musical validation belong to musical concerns, not automatically to foundation. Settings, i18n, Swing actions, and application configuration are also not automatic candidates. Settings and i18n initially remain desktop application infrastructure under [ADR-0039](0039-maven-multi-module-architecture.md).

Result remains generic infrastructure carrying domain-specific semantic errors defined in domain. No replacement framework or exact generic signature is introduced.

## Consequences

- Domain may use Result without depending on legacy, UI, or application infrastructure.
- Legacy may depend on foundation after extraction without depending on canonical musical types.
- Shared code does not qualify merely because several classes use it; all admission criteria apply.
- Exact packages, extraction adaptations, and POM implementation remain open.
- Settings/i18n extraction can be reconsidered only for a concrete cross-application dependency need, not speculative reuse.
