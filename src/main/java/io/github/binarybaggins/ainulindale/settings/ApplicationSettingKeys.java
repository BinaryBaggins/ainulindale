package io.github.binarybaggins.ainulindale.settings;

import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import java.util.Locale;

/** Keys for settings defined by the application. */
public final class ApplicationSettingKeys {

    /**
     * The UI locale preference, persisted under {@code ui.locale}.
     * If absent, the application follows the system locale.
     */
    public static final SettingKey<Locale> LOCALE = SettingKey.of("ui.locale", DefaultConverters.LOCALE);

    private ApplicationSettingKeys() {}
}
