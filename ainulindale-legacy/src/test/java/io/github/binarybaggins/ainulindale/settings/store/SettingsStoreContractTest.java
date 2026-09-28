package io.github.binarybaggins.ainulindale.settings.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.SettingConverter;
import io.github.binarybaggins.ainulindale.settings.SettingKey;
import io.github.binarybaggins.ainulindale.settings.SettingsException;
import io.github.binarybaggins.ainulindale.settings.SettingsStore;
import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class SettingsStoreContractTest {

    private SettingsStore store;

    protected abstract SettingsStore createStore();

    @BeforeEach
    void setUpStore() {
        store = createStore();
    }

    @Test
    void missingValueWithoutDefaultReturnsNull() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertNull(store.get(key));
    }

    @Test
    void missingValueWithDefaultReturnsDefault() {
        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        assertEquals("default", store.get(key));
    }

    @Test
    void defaultValueDoesNotCountAsStoredValue() {
        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        assertFalse(store.contains(key));
    }

    @Test
    void storesAndRetrievesValue() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "value");

        assertTrue(store.contains(key));
        assertEquals("value", store.get(key));
    }

    @Test
    void storedValueOverridesDefaultValue() {
        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        store.set(key, "stored");

        assertEquals("stored", store.get(key));
    }

    @Test
    void settingValueAgainReplacesPreviousValue() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "first");
        store.set(key, "second");

        assertEquals("second", store.get(key));
    }

    @Test
    void settingNullRemovesStoredValue() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "value");
        store.set(key, null);

        assertFalse(store.contains(key));
        assertNull(store.get(key));
    }

    @Test
    void settingNullRestoresDefaultValue() {
        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        store.set(key, "stored");
        store.set(key, null);

        assertFalse(store.contains(key));
        assertEquals("default", store.get(key));
    }

    @Test
    void removeDeletesStoredValue() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "value");
        store.remove(key);

        assertFalse(store.contains(key));
        assertNull(store.get(key));
    }

    @Test
    void removingMissingValueIsAllowed() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.remove(key);

        assertFalse(store.contains(key));
    }

    @Test
    void clearRemovesAllStoredValues() {
        SettingKey<String> first = SettingKey.of("test.first", DefaultConverters.STRING);
        SettingKey<Integer> second = SettingKey.of("test.second", DefaultConverters.INTEGER);

        store.set(first, "value");
        store.set(second, 42);

        store.clear();

        assertFalse(store.contains(first));
        assertFalse(store.contains(second));
    }

    @Test
    void serializationFailureIsWrappedInSettingsException() {
        SettingConverter<String> converter = new SettingConverter<>() {
            @Override
            public String serialize(String value) {
                throw new IllegalArgumentException("serialization failed");
            }

            @Override
            public String deserialize(String value) {
                return value;
            }
        };

        SettingKey<String> key = SettingKey.of("test.value", converter);

        assertThrows(SettingsException.class, () -> store.set(key, "value"));
    }

    @Test
    void deserializationFailureIsWrappedInSettingsException() {
        SettingConverter<String> converter = new SettingConverter<>() {
            @Override
            public String serialize(String value) {
                return value;
            }

            @Override
            public String deserialize(String value) {
                throw new IllegalArgumentException("deserialization failed");
            }
        };

        SettingKey<String> key = SettingKey.of("test.value", converter);

        store.set(key, "value");

        assertThrows(SettingsException.class, () -> store.get(key));
    }

    @Test
    void getRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> store.get(null));
    }

    @Test
    void containsRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> store.contains(null));
    }

    @Test
    void setRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> store.set(null, "value"));
    }

    @Test
    void removeRejectsNullKey() {
        assertThrows(NullPointerException.class, () -> store.remove(null));
    }
}
