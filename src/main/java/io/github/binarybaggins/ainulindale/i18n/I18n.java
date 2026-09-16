package io.github.binarybaggins.ainulindale.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Provides localized messages from a resource bundle managed by a
 * {@link LocaleManager}.
 *
 * <p>The instance registers a locale-change listener and automatically reloads
 * its bundle when the active locale changes. It must be closed when no longer
 * needed so that listener can be removed. Missing bundles or message keys are
 * reported by the underlying {@link ResourceBundle}; formatting uses the
 * locale currently reported by the locale manager.</p>
 */
public final class I18n implements AutoCloseable {

    private static final String DEFAULT_BASE_NAME = "io.github.binarybaggins.ainulindale.i18n.messages";

    private static final ResourceBundle.Control BUNDLE_CONTROL = ResourceBundle.Control.getNoFallbackControl(
        ResourceBundle.Control.FORMAT_DEFAULT
    );

    private final LocaleManager localeManager;
    private final String baseName;
    private final Consumer<Locale> localeChangeListener;

    private ResourceBundle bundle;

    /**
     * Constructs an I18n instance with the specified locale manager and the default base name for the resource bundle.
     * @param localeManager the locale manager to use
     * @throws NullPointerException if localeManager is null
     */
    public I18n(LocaleManager localeManager) {
        this(localeManager, DEFAULT_BASE_NAME);
    }

    /**
     * Constructs an I18n instance with the specified locale manager and base name for the resource bundle.
     * @param localeManager the locale manager to use
     * @param baseName the base name of the resource bundle
     * @throws NullPointerException if localeManager or baseName is null
     * @throws IllegalArgumentException if baseName is blank
     */
    I18n(LocaleManager localeManager, String baseName) {
        this.localeManager = Objects.requireNonNull(localeManager, "localeManager must not be null");

        Objects.requireNonNull(baseName, "baseName must not be null");

        if (baseName.isBlank()) {
            throw new IllegalArgumentException("baseName must not be blank");
        }

        this.baseName = baseName;
        this.localeChangeListener = this::reload;

        reload(localeManager.getLocale());
        localeManager.addLocaleChangeListener(localeChangeListener);
    }

    /**
     * Retrieves the message for the specified key from the resource bundle.
     * @param key the key of the message to retrieve
     * @return the message for the specified key
     * @throws NullPointerException if {@code key} is null
     * @throws java.util.MissingResourceException if the bundle or key is missing
     */
    public String get(String key) {
        Objects.requireNonNull(key, "key must not be null");

        return bundle.getString(key);
    }

    /**
     * Formats a message for the specified key using the provided arguments.
     * @param key the key of the message pattern
     * @param arguments the arguments to format the message with
     * @return the formatted message
     * @throws NullPointerException if {@code key} or {@code arguments} is null
     * @throws java.util.MissingResourceException if the bundle or key is missing
     */
    public String format(String key, Object... arguments) {
        Objects.requireNonNull(arguments, "arguments must not be null");

        String pattern = get(key);

        return new MessageFormat(pattern, localeManager.getLocale()).format(arguments);
    }

    /**
     * Reloads the resource bundle for the specified locale.
     * @param locale the locale to reload the resource bundle for
     */
    private void reload(Locale locale) {
        bundle = ResourceBundle.getBundle(baseName, locale, BUNDLE_CONTROL);
    }

    /**
     * Closes this I18n instance, removing the locale change listener from the locale manager.
     */
    @Override
    public void close() {
        localeManager.removeLocaleChangeListener(localeChangeListener);
    }
}
