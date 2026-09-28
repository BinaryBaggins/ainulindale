package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for an instrument definition.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record InstrumentDefinitionId(UUID value) {
    /**
     * Constructs a new instrument definition ID with the specified UUID value.
     *
     * @param value the UUID value of the instrument definition ID
     * @throws NullPointerException if the value is null
     */
    public InstrumentDefinitionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new instrument definition ID with a randomly generated UUID value.
     * @return the newly created instrument definition ID
     */
    public static InstrumentDefinitionId create() {
        return new InstrumentDefinitionId(UUID.randomUUID());
    }

    /**
     * Creates a new instrument definition ID with the specified UUID value.
     * @param value the UUID value of the instrument definition ID
     * @return the newly created instrument definition ID
     */
    public static InstrumentDefinitionId of(UUID value) {
        return new InstrumentDefinitionId(value);
    }
}
