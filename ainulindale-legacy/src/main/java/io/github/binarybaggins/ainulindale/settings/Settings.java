package io.github.binarybaggins.ainulindale.settings;

import java.util.Objects;

/**
 * Provides access to application settings through a backing store.
 */
public final class Settings {

    private final SettingsStore store;

    /**
     * Creates a new Settings instance with the specified store.
     *
     * @param store the settings store
     * @throws NullPointerException if {@code store} is null
     */
    public Settings(SettingsStore store) {
        this.store = Objects.requireNonNull(store);
    }

    /**
     * Returns the value associated with the specified key.
     *
     * @param key the setting key
     * @param <T> the type of the setting value
     * @return the value associated with the key
     */
    public <T> T get(SettingKey<T> key) {
        return store.get(key);
    }

    /**
     * Returns true if the store contains a value for the specified key.
     *
     * @param key the setting key
     * @return true if the store contains a value for the key, false otherwise
     */
    public boolean contains(SettingKey<?> key) {
        return store.contains(key);
    }

    /**
     * Sets the value associated with the specified key.
     * A {@code null} value removes the stored value. A key's default is used
     * when reading an absent value, but is not persisted automatically.
     *
     * @param key   the setting key
     * @param value the value to set
     * @param <T>   the type of the setting value
     */
    public <T> void set(SettingKey<T> key, T value) {
        store.set(key, value);
    }

    /**
     * Removes the value associated with the specified key.
     *
     * @param key the setting key
     */
    public void remove(SettingKey<?> key) {
        store.remove(key);
    }

    /**
     * Clears all settings.
     */
    public void clear() {
        store.clear();
    }

    /**
     * Flushes any changes by delegating durability to the underlying store.
     */
    public void flush() {
        store.flush();
    }
}
