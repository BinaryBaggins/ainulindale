package io.github.binarybaggins.ainulindale.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.jupiter.api.Test;

class I18nTest {

    private static final String TEST_BASE_NAME = "io.github.binarybaggins.ainulindale.i18n.test-messages";

    @Test
    void loadsEnglishBundle() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertEquals("English", i18n.get("language"));
        }
    }

    @Test
    void loadsGermanBundle() {
        LocaleManager localeManager = createLocaleManager(Locale.GERMAN);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertEquals("Deutsch", i18n.get("language"));
        }
    }

    @Test
    void reloadsBundleWhenLocaleChanges() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertEquals("English", i18n.get("language"));

            localeManager.setLocale(Locale.GERMAN);

            assertEquals("Deutsch", i18n.get("language"));
        }
    }

    @Test
    void formatsMessageUsingCurrentBundle() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertEquals("Hello, Frodo!", i18n.format("greeting", "Frodo"));

            localeManager.setLocale(Locale.GERMAN);

            assertEquals("Hallo, Frodo!", i18n.format("greeting", "Frodo"));
        }
    }

    @Test
    void missingKeyThrowsMissingResourceException() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertThrows(MissingResourceException.class, () -> i18n.get("missing.key"));
        }
    }

    @Test
    void getRejectsNullKey() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertThrows(NullPointerException.class, () -> i18n.get(null));
        }
    }

    @Test
    void formatRejectsNullArgumentsArray() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        try (I18n i18n = new I18n(localeManager, TEST_BASE_NAME)) {
            assertThrows(NullPointerException.class, () -> i18n.format("greeting", (Object[]) null));
        }
    }

    @Test
    void constructorRejectsNullLocaleManager() {
        assertThrows(NullPointerException.class, () -> new I18n(null, TEST_BASE_NAME));
    }

    @Test
    void constructorRejectsNullBaseName() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        assertThrows(NullPointerException.class, () -> new I18n(localeManager, null));
    }

    @Test
    void constructorRejectsBlankBaseName() {
        LocaleManager localeManager = createLocaleManager(Locale.ENGLISH);

        assertThrows(IllegalArgumentException.class, () -> new I18n(localeManager, "   "));
    }

    private static LocaleManager createLocaleManager(Locale systemLocale) {
        Settings settings = new Settings(new InMemorySettingsStore());

        return new LocaleManager(settings, () -> systemLocale);
    }
}
