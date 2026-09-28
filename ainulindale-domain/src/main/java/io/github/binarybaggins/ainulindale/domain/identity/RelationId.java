package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record RelationId(UUID value) {
    public RelationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static RelationId create() {
        return new RelationId(UUID.randomUUID());
    }

    public static RelationId of(UUID value) {
        return new RelationId(value);
    }
}
