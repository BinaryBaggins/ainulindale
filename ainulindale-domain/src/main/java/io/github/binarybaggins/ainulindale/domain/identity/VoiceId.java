package io.github.binarybaggins.ainulindale.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record VoiceId(UUID value) {
    public VoiceId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static VoiceId create() {
        return new VoiceId(UUID.randomUUID());
    }

    public static VoiceId of(UUID value) {
        return new VoiceId(value);
    }
}
