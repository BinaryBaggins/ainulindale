package io.github.binarybaggins.ainulindale.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.ApplicationSettingKeys;
import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class LocaleManagerTest {

    @Test
    void usesGermanSystemLocaleWhenNoLocaleIsStored() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.GERMANY);

        assertEquals(Locale.GERMAN, manager.getLocale());
        assertTrue(manager.isUsingSystemLocale());
    }

    @Test
    void usesEnglishSystemLocaleWhenNoLocaleIsStored() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.UK);

        assertEquals(Locale.ENGLISH, manager.getLocale());
        assertTrue(manager.isUsingSystemLocale());
    }

    @Test
    void fallsBackToEnglishForUnsupportedSystemLocale() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.FRENCH);

        assertEquals(Locale.ENGLISH, manager.getLocale());
        assertTrue(manager.isUsingSystemLocale());
    }

    @Test
    void storedLocaleOverridesSystemLocale() {
        Settings settings = createSettings();
        settings.set(ApplicationSettingKeys.LOCALE, Locale.GERMAN);

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        assertEquals(Locale.GERMAN, manager.getLocale());
        assertFalse(manager.isUsingSystemLocale());
    }

    @Test
    void storedLocaleIsNormalized() {
        Settings settings = createSettings();
        settings.set(ApplicationSettingKeys.LOCALE, Locale.GERMANY);

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        assertEquals(Locale.GERMAN, manager.getLocale());
    }

    @Test
    void setLocaleStoresNormalizedGermanLocale() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        manager.setLocale(Locale.GERMANY);

        assertEquals(Locale.GERMAN, manager.getLocale());
        assertEquals(Locale.GERMAN, settings.get(ApplicationSettingKeys.LOCALE));
        assertFalse(manager.isUsingSystemLocale());
    }

    @Test
    void setLocaleStoresNormalizedEnglishLocale() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.GERMAN);

        manager.setLocale(Locale.US);

        assertEquals(Locale.ENGLISH, manager.getLocale());
        assertEquals(Locale.ENGLISH, settings.get(ApplicationSettingKeys.LOCALE));
        assertFalse(manager.isUsingSystemLocale());
    }

    @Test
    void setLocaleRejectsUnsupportedLocale() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        assertThrows(IllegalArgumentException.class, () -> manager.setLocale(Locale.FRENCH));
    }

    @Test
    void setLocaleRejectsNull() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        assertThrows(NullPointerException.class, () -> manager.setLocale(null));
    }

    @Test
    void useSystemLocaleRemovesStoredLocale() {
        Settings settings = createSettings();
        settings.set(ApplicationSettingKeys.LOCALE, Locale.GERMAN);

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        manager.useSystemLocale();

        assertFalse(settings.contains(ApplicationSettingKeys.LOCALE));
        assertTrue(manager.isUsingSystemLocale());
        assertEquals(Locale.ENGLISH, manager.getLocale());
    }

    @Test
    void useSystemLocaleReadsCurrentSystemLocaleAgain() {
        Settings settings = createSettings();

        AtomicReference<Locale> systemLocale = new AtomicReference<>(Locale.ENGLISH);

        LocaleManager manager = new LocaleManager(settings, systemLocale::get);

        assertEquals(Locale.ENGLISH, manager.getLocale());

        manager.setLocale(Locale.GERMAN);

        systemLocale.set(Locale.UK);
        manager.useSystemLocale();

        assertEquals(Locale.ENGLISH, manager.getLocale());
        assertTrue(manager.isUsingSystemLocale());
    }

    @Test
    void resolvesSwissGermanAsGerman() {
        assertEquals(Locale.GERMAN, LocaleManager.resolveSystemLocale(Locale.forLanguageTag("de-CH")));
    }

    @Test
    void resolvesEnglishVariantAsEnglish() {
        assertEquals(Locale.ENGLISH, LocaleManager.resolveSystemLocale(Locale.US));
    }

    @Test
    void resolveSystemLocaleRejectsNull() {
        assertThrows(NullPointerException.class, () -> LocaleManager.resolveSystemLocale(null));
    }

    @Test
    void constructorRejectsNullSettings() {
        assertThrows(NullPointerException.class, () -> new LocaleManager(null, () -> Locale.ENGLISH));
    }

    @Test
    void constructorRejectsNullSystemLocaleSupplier() {
        Settings settings = createSettings();

        assertThrows(NullPointerException.class, () -> new LocaleManager(settings, null));
    }

    @Test
    void setLocaleNotifiesListenerWhenLocaleChanges() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        AtomicReference<Locale> notifiedLocale = new AtomicReference<>();

        manager.addLocaleChangeListener(notifiedLocale::set);

        manager.setLocale(Locale.GERMAN);

        assertEquals(Locale.GERMAN, notifiedLocale.get());
    }

    @Test
    void setLocaleDoesNotNotifyListenerWhenEffectiveLocaleDoesNotChange() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.GERMAN);

        AtomicReference<Locale> notifiedLocale = new AtomicReference<>();

        manager.addLocaleChangeListener(notifiedLocale::set);

        manager.setLocale(Locale.GERMANY);

        assertNull(notifiedLocale.get());
    }

    @Test
    void useSystemLocaleNotifiesListenerWhenLocaleChanges() {
        Settings settings = createSettings();

        settings.set(ApplicationSettingKeys.LOCALE, Locale.GERMAN);

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        AtomicReference<Locale> notifiedLocale = new AtomicReference<>();

        manager.addLocaleChangeListener(notifiedLocale::set);

        manager.useSystemLocale();

        assertEquals(Locale.ENGLISH, notifiedLocale.get());
    }

    @Test
    void removedListenerIsNotNotified() {
        Settings settings = createSettings();

        LocaleManager manager = new LocaleManager(settings, () -> Locale.ENGLISH);

        AtomicReference<Locale> notifiedLocale = new AtomicReference<>();

        Consumer<Locale> listener = notifiedLocale::set;

        manager.addLocaleChangeListener(listener);
        manager.removeLocaleChangeListener(listener);

        manager.setLocale(Locale.GERMAN);

        assertNull(notifiedLocale.get());
    }

    @Test
    void addLocaleChangeListenerRejectsNull() {
        LocaleManager manager = new LocaleManager(createSettings(), () -> Locale.ENGLISH);

        assertThrows(NullPointerException.class, () -> manager.addLocaleChangeListener(null));
    }

    @Test
    void removeLocaleChangeListenerRejectsNull() {
        LocaleManager manager = new LocaleManager(createSettings(), () -> Locale.ENGLISH);

        assertThrows(NullPointerException.class, () -> manager.removeLocaleChangeListener(null));
    }

    private static Settings createSettings() {
        return new Settings(new InMemorySettingsStore());
    }
}
