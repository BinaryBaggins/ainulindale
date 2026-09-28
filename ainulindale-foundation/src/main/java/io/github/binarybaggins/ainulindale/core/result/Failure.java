package io.github.binarybaggins.ainulindale.core.result;

import java.util.Objects;

public record Failure<T>(ResultError error) implements Result<T> {
    public Failure {
        Objects.requireNonNull(error);
    }
}
