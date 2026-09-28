package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for a voice.
 * Encapsulates a UUID value and provides methods for creation and validation.
 */
public record VoiceId(UUID value) {
    /**
     * Constructs a new voice ID with the specified UUID value.
     *
     * @param value the UUID value of the voice ID
     * @throws NullPointerException if the value is null
     */
    public VoiceId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new voice ID with the specified UUID value.
     * @param value the UUID value of the voice ID
     * @return the newly created voice ID
     */
    public static VoiceId of(UUID value) {
        return new VoiceId(value);
    }

    /**
     * Creates a new voice ID with a randomly generated UUID value.
     * @return the newly created voice ID
     */
    public static VoiceId create() {
        return of(UUID.randomUUID());
    }
}
