package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Represents a timeline of state transitions, allowing states to be set, ended, and queried at specific score positions.
 *
 * @param <T> the type of the state value
 */
final class StateTimeline<T> {

    private final NavigableMap<ScorePosition, StateTransition<T>> transitions = new TreeMap<>();

    /**
     * Sets the state at the specified position.
     *
     * @param position the score position where the state is set
     * @param value the value of the state
     * @throws NullPointerException if position or value is null
     */
    void set(ScorePosition position, T value) {
        Objects.requireNonNull(position, "position must not be null");
        transitions.put(position, new StateTransition.Set<>(value));
    }

    /**
     * Marks the end of the state at the specified position.
     *
     * @param position the score position where the state ends
     * @throws NullPointerException if position is null
     */
    void end(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        transitions.put(position, new StateTransition.End<>());
    }

    /**
     * Removes the transition at the specified position, if it exists.
     *
     * @param position the score position of the transition to remove
     * @return true if a transition was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean remove(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return transitions.remove(position) != null;
    }

    /**
     * Returns the latest transition at or before the specified position, if it exists.
     *
     * @param position the score position to check
     * @return an optional containing the latest transition at or before the specified position, or empty if none exists
     * @throws NullPointerException if position is null
     */
    Optional<StateTransition<T>> latestTransitionAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        Map.Entry<ScorePosition, StateTransition<T>> entry = transitions.floorEntry(position);

        return entry == null ? Optional.empty() : Optional.of(entry.getValue());
    }

    /**
     * Returns the transition at the specified position, if it exists.
     *
     * @param position the score position to check
     * @return an optional containing the transition at the specified position, or empty if none exists
     * @throws NullPointerException if position is null
     */
    Optional<StateTransition<T>> transitionAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return Optional.ofNullable(transitions.get(position));
    }

    /**
     * Returns an unmodifiable snapshot of the transitions in this timeline.
     *
     * @return an unmodifiable navigable map of transitions
     */
    NavigableMap<ScorePosition, StateTransition<T>> transitions() {
        return Collections.unmodifiableNavigableMap(new TreeMap<>(transitions));
    }
}
