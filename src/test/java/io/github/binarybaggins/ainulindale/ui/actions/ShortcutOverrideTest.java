package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ShortcutOverrideTest {

    @Test
    void createsShortcutOverride() {
        Shortcut shortcut = Shortcut.of(Key.Z, Modifier.MENU);

        ShortcutOverride override = ShortcutOverride.of(shortcut);

        assertEquals(Optional.of(shortcut), override.shortcut());
        assertFalse(override.isDisabled());
    }

    @Test
    void createsDisabledOverride() {
        ShortcutOverride override = ShortcutOverride.disabled();

        assertTrue(override.shortcut().isEmpty());
        assertTrue(override.isDisabled());
    }

    @Test
    void rejectsNullShortcut() {
        assertThrows(NullPointerException.class, () -> ShortcutOverride.of(null));
    }

    @Test
    void rejectsNullOptional() {
        assertThrows(NullPointerException.class, () -> new ShortcutOverride(null));
    }
}
