package io.github.binarybaggins.ainulindale.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public final class I18n implements AutoCloseable {

    private static final String DEFAULT_BASE_NAME = "io.github.binarybaggins.ainulindale.i18n.messages";

    private static final ResourceBundle.Control BUNDLE_CONTROL = ResourceBundle.Control.getNoFallbackControl(
        ResourceBundle.Control.FORMAT_DEFAULT
    );

    private final LocaleManager localeManager;
    private final String baseName;
    private final Consumer<Locale> localeChangeListener;

    private ResourceBundle bundle;

    public I18n(LocaleManager localeManager) {
        this(localeManager, DEFAULT_BASE_NAME);
    }

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

    public String get(String key) {
        Objects.requireNonNull(key, "key must not be null");

        return bundle.getString(key);
    }

    public String format(String key, Object... arguments) {
        Objects.requireNonNull(arguments, "arguments must not be null");

        String pattern = get(key);

        return new MessageFormat(pattern, localeManager.getLocale()).format(arguments);
    }

    private void reload(Locale locale) {
        bundle = ResourceBundle.getBundle(baseName, locale, BUNDLE_CONTROL);
    }

    @Override
    public void close() {
        localeManager.removeLocaleChangeListener(localeChangeListener);
    }
}
