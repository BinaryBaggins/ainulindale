package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record InstrumentDefinitionId(UUID value) {
    public InstrumentDefinitionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static InstrumentDefinitionId create() {
        return new InstrumentDefinitionId(UUID.randomUUID());
    }

    public static InstrumentDefinitionId of(UUID value) {
        return new InstrumentDefinitionId(value);
    }
}
