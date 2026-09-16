package io.github.binarybaggins.ainulindale.ui.shortcut;

import io.github.binarybaggins.ainulindale.settings.SettingKey;
import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionDefinition;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages keyboard shortcuts for actions.
 *
 * <p>Action definitions provide defaults; only explicit
 * {@link ShortcutOverride shortcut overrides} are stored in settings. An
 * override may replace the default with another shortcut or explicitly disable
 * the action. Removing the override restores the definition's default.</p>
 */
public final class ShortcutManager {

    private static final String SETTING_PREFIX = "shortcut.";

    private final Settings settings;
    private final Map<ActionId, ActionDefinition> definitions;
    private final Map<ActionId, SettingKey<ShortcutOverride>> settingKeys;

    /**
     * Constructs a ShortcutManager with the given settings and action catalog.
     *
     * @param settings the settings instance
     * @param catalog the action catalog
     * @throws NullPointerException if settings or catalog are null
     * @throws IllegalArgumentException if there are duplicate action definitions in the catalog
     */
    public ShortcutManager(Settings settings, ActionCatalog catalog) {
        this.settings = Objects.requireNonNull(settings, "settings must not be null");
        Objects.requireNonNull(catalog, "catalog must not be null");

        this.definitions = new EnumMap<>(ActionId.class);
        this.settingKeys = new EnumMap<>(ActionId.class);

        Iterable<ActionDefinition> definitions = catalog.definitions();

        ShortcutOverrideConverter converter = new ShortcutOverrideConverter();
        for (ActionDefinition definition : definitions) {
            Objects.requireNonNull(definition, "definition must not be null");
            ActionDefinition previous = this.definitions.putIfAbsent(definition.id(), definition);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate definition for action id: " + definition.id());
            }
            settingKeys.put(definition.id(), SettingKey.of(SETTING_PREFIX + definition.id().persistentId(), converter));
        }
    }

    /**
     * Retrieves the effective shortcut for the given action ID.
     *
     * <p>An explicit override takes precedence over the action definition's
     * default. An explicitly disabled override produces an empty result, as
     * does an action with neither an override nor a default shortcut.</p>
     *
     * @param id the action ID
     * @return an optional containing the effective shortcut, or empty if the
     *         action is disabled or has no shortcut
     * @throws NullPointerException if {@code id} is null
     * @throws IllegalArgumentException if no definition exists for {@code id}
     */
    public Optional<Shortcut> getShortcut(ActionId id) {
        ActionDefinition definition = requireDefinition(id);
        SettingKey<ShortcutOverride> key = settingKey(id);

        ShortcutOverride override = settings.get(key);

        if (override != null) {
            return override.shortcut();
        }

        return definition.defaultShortcut();
    }

    /**
     * Checks whether an explicit override is stored for the given action ID.
     * A disabled override counts as an override.
     *
     * @param id the action ID
     * @return true if an override exists, false otherwise
     * @throws NullPointerException if {@code id} is null
     * @throws IllegalArgumentException if no definition exists for {@code id}
     */
    public boolean hasOverride(ActionId id) {
        requireDefinition(id);
        return settings.contains(settingKey(id));
    }

    /**
     * Stores an explicit shortcut override for the given action ID.
     *
     * @param id the action ID
     * @param shortcut the shortcut to use instead of the default
     * @throws NullPointerException if {@code id} or {@code shortcut} is null
     * @throws IllegalArgumentException if no definition exists for {@code id}
     */
    public void setShortcut(ActionId id, Shortcut shortcut) {
        requireDefinition(id);
        settings.set(settingKey(id), ShortcutOverride.of(shortcut));
    }

    /**
     * Stores an explicit disabled override for the given action ID.
     *
     * @param id the action ID
     * @throws NullPointerException if {@code id} is null
     * @throws IllegalArgumentException if no definition exists for {@code id}
     */
    public void disableShortcut(ActionId id) {
        requireDefinition(id);
        settings.set(settingKey(id), ShortcutOverride.disabled());
    }

    /**
     * Removes the explicit override for the given action ID, restoring its
     * action definition's default shortcut.
     *
     * @param id the action ID
     * @throws NullPointerException if {@code id} is null
     * @throws IllegalArgumentException if no definition exists for {@code id}
     */
    public void resetShortcut(ActionId id) {
        requireDefinition(id);
        settings.remove(settingKey(id));
    }

    /**
     * Ensures that a definition exists for the given action id.
     *
     * @param id the action id
     * @return the action definition
     * @throws IllegalArgumentException if no definition is found for the action id
     */
    private ActionDefinition requireDefinition(ActionId id) {
        Objects.requireNonNull(id, "id must not be null");
        ActionDefinition definition = definitions.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("No definition found for action id: " + id);
        }
        return definition;
    }

    /**
     * Retrieves the setting key associated with the given action id.
     *
     * @param id the action id
     * @return the setting key for the action id
     */
    private SettingKey<ShortcutOverride> settingKey(ActionId id) {
        return settingKeys.get(id);
    }
}
