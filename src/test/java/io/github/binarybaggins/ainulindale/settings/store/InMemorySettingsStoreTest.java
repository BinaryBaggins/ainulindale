package io.github.binarybaggins.ainulindale.settings.store;

import io.github.binarybaggins.ainulindale.settings.SettingsStore;
import org.junit.jupiter.api.Test;

public class InMemorySettingsStoreTest extends SettingsStoreContractTest {

    @Override
    protected SettingsStore createStore() {
        return new InMemorySettingsStore();
    }

    @Test
    void flushDoesNothing() {
        SettingsStore store = createStore();

        store.flush();
    }
}
