package io.github.binarybaggins.ainulindale.domain.composition;

import java.util.Objects;

/**
 * Represents a state transition, which can either be a set operation with a value or an end operation.
 *
 * @param <T> the type of the state value
 */
sealed interface StateTransition<T> {
    /**
     * Represents a set operation with a value.
     *
     * @param <T> the type of the state value
     * @throws NullPointerException if value is null
     */
    record Set<T>(T value) implements StateTransition<T> {
        public Set {
            Objects.requireNonNull(value, "value must not be null");
        }
    }

    /**
     * Represents an end operation, indicating the state has ended.
     *
     * @param <T> the type of the state value
     */
    record End<T>() implements StateTransition<T> {}
}
