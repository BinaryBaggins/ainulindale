package io.github.binarybaggins.ainulindale.domain.meter;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import java.util.List;
import java.util.Objects;

/**
 * Represents a structured musical meter, which consists of a list of meter components.
 */
public record StructuredMeter(List<MeterComponent> components) implements Meter {
    /**
     * Constructs a structured meter with the given list of meter components.
     * @param components the list of meter components
     * @throws NullPointerException if components is null
     * @throws IllegalArgumentException if components is empty or contains null elements
     */
    public StructuredMeter {
        Objects.requireNonNull(components, "components must not be null");
        if (components.isEmpty()) {
            throw new IllegalArgumentException("components must not be empty");
        }

        for (MeterComponent component : components) {
            if (component == null) {
                throw new IllegalArgumentException("components must not contain null elements");
            }
        }

        components = List.copyOf(components);
    }

    /**
     * Calculates the nominal duration of this structured meter.
     * @return the nominal duration of this structured meter
     */
    public MusicalDuration nominalDuration() {
        Rational total = Rational.ZERO;
        for (MeterComponent component : components) {
            Rational duration = component.unit().value().times(Rational.of(component.count()));
            total = total.plus(duration);
        }
        return MusicalDuration.of(total);
    }
}
