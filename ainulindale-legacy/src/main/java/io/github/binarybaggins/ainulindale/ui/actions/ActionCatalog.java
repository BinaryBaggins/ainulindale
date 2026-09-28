package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A catalog of action definitions.
 */
public final class ActionCatalog {

    private final Map<ActionId, ActionDefinition> definitions = new EnumMap<>(ActionId.class);

    /**
     * Constructs an action catalog with the given action definitions.
     * @param definitions the action definitions
     * @throws NullPointerException if the definitions iterable is null or contains null elements
     * @throws IllegalArgumentException if there are duplicate action definitions
     */
    public ActionCatalog(Iterable<ActionDefinition> definitions) {
        Objects.requireNonNull(definitions, "definitions must not be null");

        for (ActionDefinition definition : definitions) {
            Objects.requireNonNull(definition, "definitions must not contain null");

            ActionDefinition previous = this.definitions.putIfAbsent(definition.id(), definition);

            if (previous != null) {
                throw new IllegalArgumentException("Duplicate action definition: " + definition.id());
            }
        }
    }

    /**
     * Returns the action definition for the given action ID.
     * @param id the action ID
     * @return the action definition for the given action ID
     * @throws NullPointerException if the action ID is null
     * @throws IllegalArgumentException if there is no action definition for the given action ID
     */
    public ActionDefinition get(ActionId id) {
        Objects.requireNonNull(id, "id must not be null");

        ActionDefinition definition = definitions.get(id);

        if (definition == null) {
            throw new IllegalArgumentException("No action definition for: " + id);
        }

        return definition;
    }

    /**
     * Returns an unmodifiable collection of all action definitions.
     * @return an unmodifiable collection of all action definitions
     */
    public Collection<ActionDefinition> definitions() {
        return List.copyOf(definitions.values());
    }
}
