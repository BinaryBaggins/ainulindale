package io.github.binarybaggins.ainulindale.core.result;

public record Failure<T>(ResultError error) implements ResultT<T> {}
