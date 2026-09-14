package io.github.binarybaggins.ainulindale.settings;

/**
 * A store for application settings. Implementations of this interface are
 * responsible for persisting settings to a storage medium (e.g., file,
 * database).
 */
public interface SettingsStore {
    /**
     * Returns the value of the setting for the given key, and either defaults to the key's default value or null if no value is
     * set.
     *
     * @param key the setting key
     * @param <T> the type of the setting value
     * @return the value of the setting, or null if no value is set
     * @throws SettingsException if an error occurs while retrieving the setting
     */
    <T> T get(SettingKey<T> key);

    /**
     * Returns true if the store contains a value for the given key. A default value does not count as a stored value.
     *
     * @param key the setting key
     * @return true if the store contains a value for the key, false otherwise
     */
    boolean contains(SettingKey<?> key);

    /**
     * Sets the value of the setting for the given key. If the value is null, the setting may be removed depending on the implementation.
     *
     * @param key   the setting key
     * @param value the value to set
     * @param <T>   the type of the setting value
     * @throws SettingsException if an error occurs while setting the value
     */
    <T> void set(SettingKey<T> key, T value);

    /**
     * Removes the setting for the given key.
     *
     * @param key the setting key
     * @throws SettingsException if an error occurs while removing the setting
     */
    void remove(SettingKey<?> key);

    /**
     * Removes all settings.
     *
     * @throws SettingsException if an error occurs while clearing the settings
     */
    void clear();

    /**
     * Flushes any changes to the underlying storage.
     *
     * @throws SettingsException if an error occurs while flushing the settings
     */
    void flush();
}
