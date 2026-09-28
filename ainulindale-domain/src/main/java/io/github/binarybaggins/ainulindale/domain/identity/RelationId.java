package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for a relation.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record RelationId(UUID value) {
    /**
     * Constructs a new relation ID with the specified UUID value.
     *
     * @param value the UUID value of the relation ID
     * @throws NullPointerException if the value is null
     */
    public RelationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new relation ID with the specified UUID value.
     * @param value the UUID value of the relation ID
     * @return the newly created relation ID
     */
    public static RelationId of(UUID value) {
        return new RelationId(value);
    }

    /**
     * Creates a new relation ID with a randomly generated UUID value.
     * @return the newly created relation ID
     */
    public static RelationId create() {
        return of(UUID.randomUUID());
    }
}
