package io.github.binarybaggins.ainulindale.ui.shortcut;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionDefinition;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;

/**
 * Detects conflicts between keyboard shortcuts for different actions.
 */
public final class ShortcutConflictDetector {

    private final ShortcutManager shortcutManager;
    private final Map<ActionId, ActionDefinition> definitions = new EnumMap<>(ActionId.class);

    /**
     * Constructs a new shortcut conflict detector.
     *
     * @param shortcutManager the shortcut manager to use for detecting conflicts
     * @param catalog the action catalog containing action definitions to consider for conflicts
     * @throws NullPointerException if either argument is null or if the definitions iterable contains null
     * @throws IllegalArgumentException if there are duplicate action definitions
     */
    public ShortcutConflictDetector(ShortcutManager shortcutManager, ActionCatalog catalog) {
        this.shortcutManager = Objects.requireNonNull(shortcutManager, "shortcutManager must not be null");

        Objects.requireNonNull(catalog, "catalog must not be null");

        for (ActionDefinition definition : catalog.definitions()) {
            Objects.requireNonNull(definition, "definitions must not contain null");

            ActionDefinition previous = this.definitions.putIfAbsent(definition.id(), definition);

            if (previous != null) {
                throw new IllegalArgumentException("Duplicate action definition: " + definition.id());
            }
        }
    }

    /**
     * Finds all action IDs that conflict with the given shortcut for the specified action.
     *
     * @param actionId the action ID to check for conflicts
     * @param shortcut the shortcut to check for conflicts
     * @return a list of conflicting action IDs
     * @throws NullPointerException if either argument is null
     * @throws IllegalArgumentException if no action definition exists for the given action ID
     */
    public List<ActionId> findConflicts(ActionId actionId, Shortcut shortcut) {
        Objects.requireNonNull(actionId, "actionId must not be null");
        Objects.requireNonNull(shortcut, "shortcut must not be null");

        ActionDefinition target = requireDefinition(actionId);

        List<ActionId> conflicts = new ArrayList<>();

        for (ActionDefinition candidate : definitions.values()) {
            if (candidate.id() == actionId) {
                continue;
            }

            if (!target.scope().overlaps(candidate.scope())) {
                continue;
            }

            if (shortcutManager.getShortcut(candidate.id()).filter(shortcut::equals).isPresent()) {
                conflicts.add(candidate.id());
            }
        }

        return List.copyOf(conflicts);
    }

    /**
     * Requires that an action definition exists for the given action ID.
     *
     * @param id the action ID
     * @return the corresponding action definition
     * @throws IllegalArgumentException if no action definition exists for the given ID
     */
    private ActionDefinition requireDefinition(ActionId id) {
        ActionDefinition definition = definitions.get(id);

        if (definition == null) {
            throw new IllegalArgumentException("No action definition for: " + id);
        }

        return definition;
    }
}
