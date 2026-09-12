package io.github.binarybaggins.ainulindale.core.result;

public record ResultError(String errorCode) {
    public ResultError {
        java.util.Objects.requireNonNull(errorCode);
        
        errorCode = errorCode.trim();
        if (errorCode.isBlank()) {
            throw new IllegalArgumentException("errorCode cannot be blank");
        }
    }
}
