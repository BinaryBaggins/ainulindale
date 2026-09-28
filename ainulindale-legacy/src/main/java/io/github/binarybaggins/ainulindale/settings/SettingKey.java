package io.github.binarybaggins.ainulindale.settings;

import java.util.Objects;

/**
 * A class representing a key for a setting, including its default
 * value and its converter.
 */
public final class SettingKey<T> {

    private final String key;
    private final T defaultValue;
    private final SettingConverter<T> converter;
    private final boolean hasDefaultValue;

    /**
     * Constructs a new SettingKey with the specified key, default value, converter,
     * and a flag indicating whether it has a default value.
     *
     * @param key             the key for the setting
     * @param defaultValue    the default value for the setting
     * @param converter       the converter for the setting
     * @param hasDefaultValue true if this SettingKey has a default value, false
     *                        otherwise
     * @throws NullPointerException if key or converter is null
     * @throws IllegalArgumentException if key is blank
     */
    private SettingKey(String key, T defaultValue, SettingConverter<T> converter, boolean hasDefaultValue) {
        this.key = Objects.requireNonNull(key, "key must not be null");
        this.converter = Objects.requireNonNull(converter, "converter must not be null");

        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }

        // If hasDefaultValue is true, ensure that defaultValue is not null
        if (hasDefaultValue) {
            Objects.requireNonNull(defaultValue, "defaultValue must not be null");
        }

        this.defaultValue = defaultValue;
        this.hasDefaultValue = hasDefaultValue;
    }

    /**
     * Creates a new SettingKey without a default value.
     *
     * @param key       the key for the setting
     * @param converter the converter for the setting
     * @return a new SettingKey without a default value
     */
    public static <T> SettingKey<T> of(String key, SettingConverter<T> converter) {
        return new SettingKey<>(key, null, converter, false);
    }

    /**
     * Creates a new SettingKey with the specified default value.
     *
     * @param key          the key for the setting
     * @param converter    the converter for the setting
     * @param defaultValue the default value for the setting
     * @param <T>          the type of the setting value
     * @return a new SettingKey with the specified default value
     */
    public static <T> SettingKey<T> of(String key, T defaultValue, SettingConverter<T> converter) {
        return new SettingKey<>(key, defaultValue, converter, true);
    }

    /**
     * Returns the key for this SettingKey.
     *
     * @return the key for this SettingKey
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default value for this SettingKey.
     *
     * @return the default value for this SettingKey, or null if no default value is
     *         set
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Checks if this SettingKey has a default value.
     *
     * @return true if this SettingKey has a default value, false otherwise
     */
    public boolean hasDefaultValue() {
        return hasDefaultValue;
    }

    /**
     * Serializes the given value to a string using the converter.
     *
     * @param value the value to serialize
     * @return the serialized string representation of the value
     * @throws NullPointerException if the value is null or if the converter returns null
     */
    public String serialize(T value) {
        Objects.requireNonNull(value, "value must not be null");
        return Objects.requireNonNull(converter.serialize(value), "converter must not serialize to null");
    }

    /**
     * Deserializes the given string to a value using the converter.
     *
     * @param value the string to deserialize
     * @return the deserialized value
     * @throws NullPointerException if the value is null or if the converter returns null
     */
    public T deserialize(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return Objects.requireNonNull(converter.deserialize(value), "converter must not deserialize to null");
    }
}
