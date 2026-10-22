package io.github.binarybaggins.ainulindale.domain.meter;

/**
 * Represents a musical meter, which can be either structured or free.
 */
public sealed interface Meter permits StructuredMeter, FreeMeter {}
