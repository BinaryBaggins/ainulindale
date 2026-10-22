package io.github.binarybaggins.ainulindale.domain.composition;

sealed interface StateTransition<T> {

    record Set<T>(T value) implements StateTransition<T> {}

    record End<T>() implements StateTransition<T> {}

}
