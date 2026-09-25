# ADR-0028 – External Formats Are Expressiveness References, Not Domain Templates

**Status:** Accepted

## Context

External formats and targets package musical information according to their own syntax, history, and constraints. Copying those structures into the canonical model would couple Ainulindalë's ownership and representation to particular external systems.

Interoperability still requires understanding what those systems can express and preserve.

## Decision

Canonical design starts from musical semantics and Ainulindalë's own domain requirements.

> External formats are test cases for expressiveness, not templates for the domain model.

ABC, MIDI, MusicXML, LOTRO, and other external systems may be used to:

- Discover relevant musical concepts
- Test whether the canonical model is sufficiently expressive
- Validate interoperability requirements
- Guide future adapter design

They do not define canonical ownership, type hierarchies, or internal representation. External syntax and ownership structures must not enter the canonical domain merely because a format uses them.

A future adapter may map one external construct into several canonical concepts or combine several external constructs into one canonical concept. A one-to-one correspondence is not required. For example, the existence of ABC `K:` does not require a canonical object that reproduces that field's structure.

Format-specific syntax, source spelling, and preservation requirements belong in dedicated adapter/source models. Concrete mappings and adapter designs remain outside this decision.

## Consequences

- External systems provide evidence about expressiveness without dictating internal architecture.
- Canonical concepts retain musical meaning independently of format-specific packaging.
- Adapters can reconcile different conceptual boundaries without distorting canonical ownership or identity.
- Source-preservation concerns remain explicit at format boundaries rather than becoming incidental domain fields.
- Concrete interoperability mappings and implementations remain open.
