package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EventId create() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId of(UUID value) {
        return new EventId(value);
    }
}
