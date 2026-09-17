package io.github.binarybaggins.ainulindale.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import org.junit.jupiter.api.Test;

class SettingsTest {

    @Test
    void constructorRejectsNullStore() {
        assertThrows(NullPointerException.class, () -> new Settings(null));
    }

    @Test
    void setAndGetValue() {
        Settings settings = new Settings(new InMemorySettingsStore());

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        settings.set(key, "value");

        assertEquals("value", settings.get(key));
    }

    @Test
    void containsReflectsStoredValue() {
        Settings settings = new Settings(new InMemorySettingsStore());

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertFalse(settings.contains(key));

        settings.set(key, "value");

        assertTrue(settings.contains(key));
    }

    @Test
    void removeDeletesStoredValue() {
        Settings settings = new Settings(new InMemorySettingsStore());

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        settings.set(key, "value");
        settings.remove(key);

        assertFalse(settings.contains(key));
    }

    @Test
    void clearRemovesAllStoredValues() {
        Settings settings = new Settings(new InMemorySettingsStore());

        SettingKey<String> first = SettingKey.of("test.first", DefaultConverters.STRING);
        SettingKey<Integer> second = SettingKey.of("test.second", DefaultConverters.INTEGER);

        settings.set(first, "value");
        settings.set(second, 42);

        settings.clear();

        assertFalse(settings.contains(first));
        assertFalse(settings.contains(second));
    }

    @Test
    void getReturnsDefaultValue() {
        Settings settings = new Settings(new InMemorySettingsStore());

        SettingKey<String> key = SettingKey.of("test.value", "default", DefaultConverters.STRING);

        assertEquals("default", settings.get(key));
    }

    @Test
    void flushCanBeCalled() {
        Settings settings = new Settings(new InMemorySettingsStore());

        settings.flush();
    }
}
