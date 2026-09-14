package io.github.binarybaggins.ainulindale.ui.actions;

import io.github.binarybaggins.ainulindale.settings.SettingKey;
import io.github.binarybaggins.ainulindale.settings.Settings;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages keyboard shortcuts for actions, including retrieving, setting, and disabling shortcuts. Only ShortcutOverrides are stored in the settings. Defaults are defined in the action definitions.
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
     * Retrieves the effective shortcut for the given action id, considering any overrides.
     *
     * @param id the action id
     * @return an Optional containing the effective shortcut, or empty if none is set
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
     * Checks if there is an override for the given action id.
     *
     * @param id the action id
     * @return true if an override exists, false otherwise
     */
    public boolean hasOverride(ActionId id) {
        requireDefinition(id);
        return settings.contains(settingKey(id));
    }

    /**
     * Sets the shortcut for the given action id.
     *
     * @param id the action id
     * @param shortcut the new shortcut
     */
    public void setShortcut(ActionId id, Shortcut shortcut) {
        requireDefinition(id);
        settings.set(settingKey(id), ShortcutOverride.of(shortcut));
    }

    /**
     * Disables the shortcut for the given action id.
     *
     * @param id the action id
     */
    public void disableShortcut(ActionId id) {
        requireDefinition(id);
        settings.set(settingKey(id), ShortcutOverride.disabled());
    }

    /**
     * Resets the shortcut for the given action id to its default value.
     *
     * @param id the action id
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
