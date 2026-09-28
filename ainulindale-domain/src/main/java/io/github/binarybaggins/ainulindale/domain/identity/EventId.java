package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for an event.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record EventId(UUID value) {
    /**
     * Constructs a new event ID with the specified UUID value.
     * @param value the UUID value of the event ID
     * @throws NullPointerException if the value is null
     */
    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new event ID with the specified UUID value.
     * @param value the UUID value of the event ID
     * @return the newly created event ID
     */
    public static EventId of(UUID value) {
        return new EventId(value);
    }

    /**
     * Creates a new event ID with a randomly generated UUID value.
     * @return the newly created event ID
     */
    public static EventId create() {
        return of(UUID.randomUUID());
    }
}
