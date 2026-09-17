package io.github.binarybaggins.ainulindale.settings.converter;

import io.github.binarybaggins.ainulindale.settings.SettingConverter;
import java.util.Locale;

/** Standard converters for common setting value types. */
public final class DefaultConverters {

    private DefaultConverters() {
        // Prevent instantiation
    }

    /** Stores strings unchanged. */
    public static final SettingConverter<String> STRING = new SettingConverter<>() {
        @Override
        public String deserialize(String value) {
            return value;
        }

        @Override
        public String serialize(String value) {
            return value;
        }
    };

    /** Stores integers using their decimal string representation. */
    public static final SettingConverter<Integer> INTEGER = new SettingConverter<>() {
        @Override
        public Integer deserialize(String value) {
            return Integer.valueOf(value);
        }

        @Override
        public String serialize(Integer value) {
            return String.valueOf(value);
        }
    };

    /** Stores booleans as {@code true} or {@code false}; parsing is case-insensitive. */
    public static final SettingConverter<Boolean> BOOLEAN = new SettingConverter<Boolean>() {
        @Override
        public String serialize(Boolean value) {
            return Boolean.toString(value);
        }

        @Override
        public Boolean deserialize(String value) {
            if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            if ("true".equalsIgnoreCase(value)) {
                return true;
            } else {
                throw new IllegalArgumentException("Invalid boolean value: " + value);
            }
        }
    };

    /** Stores doubles using their standard string representation. */
    public static final SettingConverter<Double> DOUBLE = new SettingConverter<>() {
        @Override
        public Double deserialize(String value) {
            return Double.valueOf(value);
        }

        @Override
        public String serialize(Double value) {
            return String.valueOf(value);
        }
    };

    /** Stores locales as BCP 47 language tags. */
    public static final SettingConverter<Locale> LOCALE = new SettingConverter<>() {
        @Override
        public Locale deserialize(String value) {
            return Locale.forLanguageTag(value);
        }

        @Override
        public String serialize(Locale value) {
            return value.toLanguageTag();
        }
    };
}
