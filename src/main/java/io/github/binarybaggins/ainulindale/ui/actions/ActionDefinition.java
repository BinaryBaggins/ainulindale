package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.Objects;
import java.util.Optional;

import io.github.binarybaggins.ainulindale.ui.shortcut.Shortcut;

/**
 * A record representing the definition of an action, including its ID, scope, name, description, and optional default shortcut.
 */
public record ActionDefinition(
    ActionId id,
    ActionScope scope,
    String nameKey,
    String descriptionKey,
    Optional<Shortcut> defaultShortcut
) {
    /**
     * Constructs a new ActionDefinition with the specified parameters.
     * @param id the action ID
     * @param scope the action scope
     * @param nameKey the key for the action's name
     * @param descriptionKey the key for the action's description
     * @param defaultShortcut the optional default shortcut for the action
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if nameKey or descriptionKey is blank
     */
    public ActionDefinition {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(nameKey, "nameKey must not be null");
        Objects.requireNonNull(descriptionKey, "descriptionKey must not be null");
        Objects.requireNonNull(defaultShortcut, "defaultShortcut must not be null");

        if (nameKey.isBlank()) {
            throw new IllegalArgumentException("nameKey must not be blank");
        }

        if (descriptionKey.isBlank()) {
            throw new IllegalArgumentException("descriptionKey must not be blank");
        }
    }

    /**
     * Creates an ActionDefinition with a default shortcut.
     * @param id the action ID
     * @param scope the action scope
     * @param nameKey the key for the action's name
     * @param descriptionKey the key for the action's description
     * @param defaultShortcut the default shortcut for the action
     */
    public ActionDefinition(
        ActionId id,
        ActionScope scope,
        String nameKey,
        String descriptionKey,
        Shortcut defaultShortcut
    ) {
        this(
            id,
            scope,
            nameKey,
            descriptionKey,
            Optional.of(Objects.requireNonNull(defaultShortcut, "defaultShortcut must not be null"))
        );
    }

    /**
     * Creates an ActionDefinition without a default shortcut.
     * @param id the action ID
     * @param scope the action scope
     * @param nameKey the key for the action's name
     * @param descriptionKey the key for the action's description
     * @return a new ActionDefinition without a default shortcut
     */
    public static ActionDefinition withoutShortcut(
        ActionId id,
        ActionScope scope,
        String nameKey,
        String descriptionKey
    ) {
        return new ActionDefinition(id, scope, nameKey, descriptionKey, Optional.empty());
    }
}
