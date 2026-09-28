package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for a part.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record PartId(UUID value) {
    /**
     * Constructs a new part ID with the specified UUID value.
     *
     * @param value the UUID value of the part ID
     * @throws NullPointerException if the value is null
     */
    public PartId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new part ID with a randomly generated UUID value.
     * @return the newly created part ID
     */
    public static PartId create() {
        return new PartId(UUID.randomUUID());
    }

    /**
     * Creates a new part ID with the specified UUID value.
     * @param value the UUID value of the part ID
     * @return the newly created part ID
     */
    public static PartId of(UUID value) {
        return new PartId(value);
    }
}
