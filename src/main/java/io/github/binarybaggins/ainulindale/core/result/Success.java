package io.github.binarybaggins.ainulindale.core.result;

import java.util.Objects;

public record Success<T>(T value) implements Result<T> {
    public Success {
        Objects.requireNonNull(value);
    }
}
