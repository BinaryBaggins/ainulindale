package io.github.binarybaggins.ainulindale.core.result;

public record ResultError(String code) {
    public ResultError {
        java.util.Objects.requireNonNull(code);

        if (code.isBlank()) {
            throw new IllegalArgumentException("code cannot be blank");
        }
    }
}
