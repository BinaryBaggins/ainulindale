package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record PartId(UUID value) {
    public PartId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PartId create() {
        return new PartId(UUID.randomUUID());
    }

    public static PartId of(UUID value) {
        return new PartId(value);
    }
}
