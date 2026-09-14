package io.github.binarybaggins.ainulindale.settings;

import io.github.binarybaggins.ainulindale.settings.converter.DefaultConverters;
import java.util.Locale;

public final class ApplicationSettingKeys {

    public static final SettingKey<Locale> LOCALE = SettingKey.of("ui.locale", DefaultConverters.LOCALE);

    private ApplicationSettingKeys() {}
}
