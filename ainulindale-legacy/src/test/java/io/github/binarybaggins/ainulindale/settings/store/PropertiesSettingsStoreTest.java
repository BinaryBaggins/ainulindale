package io.github.binarybaggins.ainulindale.settings.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.SettingKey;
import io.github.binarybaggins.ainulindale.settings.SettingsStore;
import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PropertiesSettingsStoreTest extends SettingsStoreContractTest {

    @TempDir
    Path tempDir;

    private Path settingsFile;

    @Override
    protected SettingsStore createStore() {
        settingsFile = tempDir.resolve("settings.properties");
        return new PropertiesSettingsStore(settingsFile);
    }

    @Test
    void missingFileStartsEmpty() {
        Path file = tempDir.resolve("missing.properties");

        SettingsStore store = new PropertiesSettingsStore(file);

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        assertFalse(store.contains(key));
    }

    @Test
    void flushCreatesSettingsFile() {
        SettingsStore store = createStore();

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "value");
        store.flush();

        assertTrue(Files.exists(settingsFile));
    }

    @Test
    void flushCreatesMissingParentDirectories() {
        Path file = tempDir.resolve("nested").resolve("directory").resolve("settings.properties");

        SettingsStore store = new PropertiesSettingsStore(file);

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        store.set(key, "value");
        store.flush();

        assertTrue(Files.exists(file));
    }

    @Test
    void valuesAreLoadedByAnotherStoreFromSameFile() {
        SettingsStore firstStore = createStore();

        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        firstStore.set(key, "value");
        firstStore.flush();

        SettingsStore secondStore = new PropertiesSettingsStore(settingsFile);

        assertEquals("value", secondStore.get(key));
    }
}
