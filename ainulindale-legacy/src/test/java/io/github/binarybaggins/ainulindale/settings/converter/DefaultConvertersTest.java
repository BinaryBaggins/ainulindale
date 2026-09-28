package io.github.binarybaggins.ainulindale.settings.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class DefaultConvertersTest {

    @Test
    void stringConverterRoundTripsValue() {
        String serialized = DefaultConverters.STRING.serialize("test value");

        assertEquals("test value", serialized);
        assertEquals("test value", DefaultConverters.STRING.deserialize(serialized));
    }

    @Test
    void integerConverterRoundTripsValue() {
        String serialized = DefaultConverters.INTEGER.serialize(42);

        assertEquals("42", serialized);
        assertEquals(42, DefaultConverters.INTEGER.deserialize(serialized));
    }

    @Test
    void integerConverterRejectsInvalidValue() {
        assertThrows(NumberFormatException.class, () -> DefaultConverters.INTEGER.deserialize("not-an-integer"));
    }

    @Test
    void booleanConverterRoundTripsTrue() {
        String serialized = DefaultConverters.BOOLEAN.serialize(true);

        assertEquals("true", serialized);
        assertEquals(true, DefaultConverters.BOOLEAN.deserialize(serialized));
    }

    @Test
    void booleanConverterRoundTripsFalse() {
        String serialized = DefaultConverters.BOOLEAN.serialize(false);

        assertEquals("false", serialized);
        assertEquals(false, DefaultConverters.BOOLEAN.deserialize(serialized));
    }

    @Test
    void booleanConverterAcceptsDifferentCase() {
        assertEquals(true, DefaultConverters.BOOLEAN.deserialize("TRUE"));

        assertEquals(false, DefaultConverters.BOOLEAN.deserialize("False"));
    }

    @Test
    void booleanConverterRejectsInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> DefaultConverters.BOOLEAN.deserialize("yes"));
    }

    @Test
    void doubleConverterRoundTripsValue() {
        String serialized = DefaultConverters.DOUBLE.serialize(12.5);

        assertEquals("12.5", serialized);
        assertEquals(12.5, DefaultConverters.DOUBLE.deserialize(serialized));
    }

    @Test
    void doubleConverterRejectsInvalidValue() {
        assertThrows(NumberFormatException.class, () -> DefaultConverters.DOUBLE.deserialize("not-a-double"));
    }

    @Test
    void localeConverterRoundTripsLocale() {
        Locale locale = Locale.GERMANY;

        String serialized = DefaultConverters.LOCALE.serialize(locale);

        assertEquals("de-DE", serialized);
        assertEquals(locale, DefaultConverters.LOCALE.deserialize(serialized));
    }

    @Test
    void localeConverterSupportsLanguageOnlyLocale() {
        Locale locale = Locale.ENGLISH;

        assertEquals(locale, DefaultConverters.LOCALE.deserialize(DefaultConverters.LOCALE.serialize(locale)));
    }
}
