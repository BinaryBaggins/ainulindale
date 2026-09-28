package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.Action;

/**
 * A registry for managing actions identified by ActionId.
 * Provides methods to register, retrieve, and check for actions.
 */
public final class ActionRegistry {

    private final Map<ActionId, Action> actions = new EnumMap<>(ActionId.class);

    /**
     * Registers an action with the specified id. If an action with the same id is already registered, an exception is thrown.
     * @param id the id of the action to register
     * @param action the action to register
     * @throws NullPointerException if id or action is null
     * @throws IllegalStateException if an action with the same id is already registered
     */
    public void register(ActionId id, Action action) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(action, "action must not be null");

        Action existing = actions.putIfAbsent(id, action);

        if (existing != null) {
            throw new IllegalStateException("Action with id " + id + " is already registered");
        }
    }

    /**
     * Retrieves the action registered with the specified id.
     * @param id the id of the action to retrieve
     * @return the action registered with the specified id
     * @throws NullPointerException if id is null
     * @throws IllegalStateException if no action is registered with the specified id
     */
    public Action get(ActionId id) {
        Objects.requireNonNull(id, "id must not be null");
        Action action = actions.get(id);
        if (action == null) {
            throw new IllegalStateException("No action registered with id " + id);
        }
        return action;
    }

    /**
     * Checks if an action with the specified id is registered.
     * @param id the id of the action to check
     * @return true if an action with the specified id is registered, false otherwise
     * @throws NullPointerException if id is null
     */
    public boolean contains(ActionId id) {
        Objects.requireNonNull(id, "id must not be null");
        return actions.containsKey(id);
    }
}
