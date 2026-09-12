package io.github.binarybaggins.ainulindale.core.result;

import java.util.Objects;

public record ResultError(String code) {
    public ResultError {
        Objects.requireNonNull(code);

        if (code.isBlank()) {
            throw new IllegalArgumentException("code cannot be blank");
        }
    }
}
