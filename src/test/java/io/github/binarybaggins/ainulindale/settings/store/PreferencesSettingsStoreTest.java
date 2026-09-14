package io.github.binarybaggins.ainulindale.settings.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.binarybaggins.ainulindale.settings.SettingKey;
import io.github.binarybaggins.ainulindale.settings.SettingsStore;
import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class PreferencesSettingsStoreTest extends SettingsStoreContractTest {

    private final String nodePath = "/io/github/binarybaggins/ainulindale/tests/" + UUID.randomUUID();

    private final Preferences preferences = Preferences.userRoot().node(nodePath);

    @Override
    protected SettingsStore createStore() {
        return new PreferencesSettingsStore(preferences);
    }

    @AfterEach
    void removeTestNode() throws BackingStoreException {
        preferences.removeNode();
        Preferences.userRoot().flush();
    }

    @Test
    void valuesAreAvailableToAnotherStoreUsingSamePreferencesNode() {
        SettingKey<String> key = SettingKey.of("test.value", DefaultConverters.STRING);

        SettingsStore firstStore = new PreferencesSettingsStore(preferences);

        firstStore.set(key, "value");
        firstStore.flush();

        SettingsStore secondStore = new PreferencesSettingsStore(Preferences.userRoot().node(nodePath));

        assertEquals("value", secondStore.get(key));
    }
}
