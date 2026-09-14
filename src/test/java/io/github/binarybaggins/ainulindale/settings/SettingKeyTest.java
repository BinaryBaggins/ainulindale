package io.github.binarybaggins.ainulindale.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import org.junit.jupiter.api.Test;

class SettingKeyTest {

    @Test
    void createsKeyWithoutDefaultValue() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertEquals("test.value", key.getKey());
        assertFalse(key.hasDefaultValue());
        assertNull(key.getDefaultValue());
    }

    @Test
    void createsKeyWithDefaultValue() {
        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        assertEquals("test.value", key.getKey());
        assertTrue(key.hasDefaultValue());
        assertEquals("default", key.getDefaultValue());
    }

    @Test
    void rejectsNullKey() {
        assertThrows(NullPointerException.class, () -> SettingKey.of(null, DefaultConverters.STRING));
    }

    @Test
    void rejectsEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> SettingKey.of("", DefaultConverters.STRING));
    }

    @Test
    void rejectsBlankKey() {
        assertThrows(IllegalArgumentException.class, () -> SettingKey.of("   ", DefaultConverters.STRING));
    }

    @Test
    void rejectsNullConverter() {
        assertThrows(NullPointerException.class, () -> SettingKey.of("test.value", (SettingConverter<String>) null));
    }

    @Test
    void rejectsNullDefaultValue() {
        assertThrows(NullPointerException.class, () -> SettingKey.of("test.value", null, DefaultConverters.STRING));
    }

    @Test
    void serializesValueUsingConverter() {
        SettingKey<Integer> key = SettingKey.of("test.value", DefaultConverters.INTEGER);

        assertEquals("42", key.serialize(42));
    }

    @Test
    void deserializesValueUsingConverter() {
        SettingKey<Integer> key = SettingKey.of("test.value", DefaultConverters.INTEGER);

        assertEquals(42, key.deserialize("42"));
    }

    @Test
    void rejectsNullValueForSerialization() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertThrows(NullPointerException.class, () -> key.serialize(null));
    }

    @Test
    void rejectsNullValueForDeserialization() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertThrows(NullPointerException.class, () -> key.deserialize(null));
    }

    @Test
    void rejectsNullSerializationResultFromConverter() {
        SettingConverter<String> converter = new SettingConverter<>() {
            @Override
            public String serialize(String value) {
                return null;
            }

            @Override
            public String deserialize(String value) {
                return value;
            }
        };

        SettingKey<String> key = SettingKey.of("test.value", converter);

        assertThrows(NullPointerException.class, () -> key.serialize("value"));
    }

    @Test
    void rejectsNullDeserializationResultFromConverter() {
        SettingConverter<String> converter = new SettingConverter<>() {
            @Override
            public String serialize(String value) {
                return value;
            }

            @Override
            public String deserialize(String value) {
                return null;
            }
        };

        SettingKey<String> key = SettingKey.of("test.value", converter);

        assertThrows(NullPointerException.class, () -> key.deserialize("value"));
    }
}
