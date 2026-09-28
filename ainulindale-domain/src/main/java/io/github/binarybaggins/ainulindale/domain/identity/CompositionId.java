package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record CompositionId(UUID value) {
    public CompositionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CompositionId create() {
        return new CompositionId(UUID.randomUUID());
    }

    public static CompositionId of(UUID value) {
        return new CompositionId(value);
    }
}
