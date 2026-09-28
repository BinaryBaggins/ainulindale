package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for a composition.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record CompositionId(UUID value) {
    /**
     * Constructs a new composition ID with the specified UUID value.
     *
     * @param value the UUID value of the composition ID
     * @throws NullPointerException if the value is null
     */
    public CompositionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new composition ID with the specified UUID value.
     * @param value the UUID value of the composition ID
     * @return the newly created composition ID
     */
    public static CompositionId of(UUID value) {
        return new CompositionId(value);
    }

    /**
     * Creates a new composition ID with a randomly generated UUID value.
     * @return the newly created composition ID
     */
    public static CompositionId create() {
        return of(UUID.randomUUID());
    }
}
