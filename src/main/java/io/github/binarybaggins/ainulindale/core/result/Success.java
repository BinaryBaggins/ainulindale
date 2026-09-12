package io.github.binarybaggins.ainulindale.core.result;

public record Success<T>(T value) implements ResultT<T> {
    
}
