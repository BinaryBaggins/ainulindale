package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import org.junit.jupiter.api.Test;

class ShortcutOverrideConverterTest {

    private final ShortcutOverrideConverter converter = new ShortcutOverrideConverter();

    @Test
    void serializesShortcutWithoutModifiers() {
        ShortcutOverride override = ShortcutOverride.of(Shortcut.of(Key.DELETE));

        assertEquals("DELETE", converter.serialize(override));
    }

    @Test
    void serializesShortcutWithModifiersInCanonicalOrder() {
        ShortcutOverride override = ShortcutOverride.of(
            new Shortcut(Key.Z, Set.of(Modifier.SHIFT, Modifier.MENU, Modifier.ALT))
        );

        assertEquals("MENU+ALT+SHIFT+Z", converter.serialize(override));
    }

    @Test
    void serializesDisabledOverride() {
        assertEquals("NONE", converter.serialize(ShortcutOverride.disabled()));
    }

    @Test
    void deserializesShortcutWithoutModifiers() {
        assertEquals(ShortcutOverride.of(Shortcut.of(Key.DELETE)), converter.deserialize("DELETE"));
    }

    @Test
    void deserializesShortcutWithModifiers() {
        assertEquals(
            ShortcutOverride.of(Shortcut.of(Key.Z, Modifier.MENU, Modifier.SHIFT)),
            converter.deserialize("MENU+SHIFT+Z")
        );
    }

    @Test
    void deserializesDisabledOverride() {
        assertEquals(ShortcutOverride.disabled(), converter.deserialize("NONE"));
    }

    @Test
    void roundTripsShortcutOverride() {
        ShortcutOverride original = ShortcutOverride.of(Shortcut.of(Key.DOWN, Modifier.ALT, Modifier.SHIFT));

        assertEquals(original, converter.deserialize(converter.serialize(original)));
    }

    @Test
    void roundTripsDisabledOverride() {
        ShortcutOverride original = ShortcutOverride.disabled();

        assertEquals(original, converter.deserialize(converter.serialize(original)));
    }

    @Test
    void rejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> converter.deserialize("   "));
    }

    @Test
    void rejectsUnknownKey() {
        assertThrows(IllegalArgumentException.class, () -> converter.deserialize("MENU+UNKNOWN"));
    }

    @Test
    void rejectsUnknownModifier() {
        assertThrows(IllegalArgumentException.class, () -> converter.deserialize("UNKNOWN+Z"));
    }

    @Test
    void rejectsDuplicateModifier() {
        assertThrows(IllegalArgumentException.class, () -> converter.deserialize("MENU+MENU+Z"));
    }

    @Test
    void rejectsMissingKey() {
        assertThrows(IllegalArgumentException.class, () -> converter.deserialize("MENU+"));
    }

    @Test
    void serializeRejectsNull() {
        assertThrows(NullPointerException.class, () -> converter.serialize(null));
    }

    @Test
    void deserializeRejectsNull() {
        assertThrows(NullPointerException.class, () -> converter.deserialize(null));
    }
}
