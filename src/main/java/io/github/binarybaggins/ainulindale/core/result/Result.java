package io.github.binarybaggins.ainulindale.core.result;

public sealed interface Result<T> permits Success, Failure {
    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> failure(ResultError error) {
        return new Failure<>(error);
    }
}
