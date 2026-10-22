package io.github.binarybaggins.ainulindale.domain.meter;

import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import java.util.Objects;

/**
 * Represents a component of a musical meter, consisting of a count and a unit duration.
 */
public record MeterComponent(int count, MusicalDuration unit) {
    public MeterComponent {
        Objects.requireNonNull(unit, "unit must not be null");

        if (count <= 0) {
            throw new IllegalArgumentException("count must be strictly positive");
        }

        if (unit.value().signum() <= 0) {
            throw new IllegalArgumentException("unit must be strictly positive");
        }
    }
}
