package io.github.binarybaggins.ainulindale.i18n;

import io.github.binarybaggins.ainulindale.settings.ApplicationSettingKeys;
import io.github.binarybaggins.ainulindale.settings.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LocaleManager {

    private final Supplier<Locale> systemLocaleSupplier;
    private final List<Consumer<Locale>> localeChangeListeners = new ArrayList<>();

    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale GERMAN = Locale.GERMAN;

    private final Settings settings;

    private Locale locale;

    public LocaleManager(Settings settings) {
        this(settings, Locale::getDefault);
    }

    /**
     * Constructs a LocaleManager with the given settings and system locale supplier.
     * Package-private constructor for testing purposes.
     *
     * @param settings the application settings
     * @param systemLocaleSupplier the supplier for the system locale
     */
    LocaleManager(Settings settings, Supplier<Locale> systemLocaleSupplier) {
        this.settings = Objects.requireNonNull(settings, "settings must not be null");
        this.systemLocaleSupplier = Objects.requireNonNull(
            systemLocaleSupplier,
            "systemLocaleSupplier must not be null"
        );
        this.locale = resolveInitialLocale();
    }

    /**
     * Adds a listener that will be notified whenever the locale changes.
     * @param listener the listener to add
     */
    public void addLocaleChangeListener(Consumer<Locale> listener) {
        localeChangeListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * Removes a previously added locale change listener.
     * @param listener the listener to remove
     */
    public void removeLocaleChangeListener(Consumer<Locale> listener) {
        localeChangeListeners.remove(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * Returns the currently active locale.
     * @return the currently active locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns whether the system locale is currently being used.
     * @return true if the system locale is being used, false otherwise
     */
    public boolean isUsingSystemLocale() {
        return !settings.contains(ApplicationSettingKeys.LOCALE);
    }

    /**
     * Sets the currently active locale.
     * @param locale the locale to set
     * @throws NullPointerException if the locale is null
     * @throws IllegalArgumentException if the locale is not supported
     */
    public void setLocale(Locale locale) {
        Locale supportedLocale = requireSupported(locale);

        settings.set(ApplicationSettingKeys.LOCALE, supportedLocale);
        updateLocale(supportedLocale);
    }

    /**
     * Switches to using the system locale.
     */
    public void useSystemLocale() {
        settings.remove(ApplicationSettingKeys.LOCALE);
        updateLocale(resolveSystemLocale(systemLocaleSupplier.get()));
    }

    /**
     * Resolves the initial locale to be used by the application. It checks for a stored locale
     * first and falls back to the system locale if none is found.
     * @return the initial locale
     */
    private Locale resolveInitialLocale() {
        Locale storedLocale = settings.get(ApplicationSettingKeys.LOCALE);

        if (storedLocale != null) {
            return requireSupported(storedLocale);
        }

        return resolveSystemLocale(systemLocaleSupplier.get());
    }

    /**
     * Resolves the system locale to one of the supported locales.
     * @param systemLocale the system locale
     * @return the resolved locale
     */
    static Locale resolveSystemLocale(Locale systemLocale) {
        Objects.requireNonNull(systemLocale, "systemLocale must not be null");

        if (GERMAN.getLanguage().equals(systemLocale.getLanguage())) {
            return GERMAN;
        }

        if (ENGLISH.getLanguage().equals(systemLocale.getLanguage())) {
            return ENGLISH;
        }

        return ENGLISH;
    }

    /**
     * Ensures that the given locale is supported by the application.
     * @param locale the locale to check
     * @return the supported locale
     * @throws NullPointerException if the locale is null
     * @throws IllegalArgumentException if the locale is not supported
     */
    private static Locale requireSupported(Locale locale) {
        Objects.requireNonNull(locale, "locale must not be null");

        if (GERMAN.getLanguage().equals(locale.getLanguage())) {
            return GERMAN;
        }

        if (ENGLISH.getLanguage().equals(locale.getLanguage())) {
            return ENGLISH;
        }

        throw new IllegalArgumentException("Unsupported locale: " + locale.toLanguageTag());
    }

    /**
     * Updates the current locale and notifies all registered listeners.
     * @param newLocale the new locale to set
     */
    private void updateLocale(Locale newLocale) {
        if (Objects.equals(this.locale, newLocale)) {
            return;
        }
        this.locale = newLocale;
        for (Consumer<Locale> listener : List.copyOf(localeChangeListeners)) {
            listener.accept(locale);
        }
    }
}
