package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShortcutTest {

    @Test
    void createsShortcutWithoutModifiers() {
        Shortcut shortcut = Shortcut.of(Key.DELETE);

        assertEquals(Key.DELETE, shortcut.key());
        assertTrue(shortcut.modifiers().isEmpty());
    }

    @Test
    void createsShortcutWithModifiers() {
        Shortcut shortcut = Shortcut.of(Key.Z, Modifier.MENU, Modifier.SHIFT);

        assertEquals(Key.Z, shortcut.key());
        assertEquals(Set.of(Modifier.MENU, Modifier.SHIFT), shortcut.modifiers());
    }

    @Test
    void copiesModifierSetDefensively() {
        Set<Modifier> modifiers = new HashSet<>(Set.of(Modifier.MENU));

        Shortcut shortcut = new Shortcut(Key.Z, modifiers);

        modifiers.add(Modifier.SHIFT);

        assertEquals(Set.of(Modifier.MENU), shortcut.modifiers());
    }

    @Test
    void modifierSetIsImmutable() {
        Shortcut shortcut = Shortcut.of(Key.Z, Modifier.MENU);

        assertThrows(UnsupportedOperationException.class, () -> shortcut.modifiers().add(Modifier.SHIFT));
    }

    @Test
    void rejectsNullKey() {
        assertThrows(NullPointerException.class, () -> Shortcut.of(null));
    }

    @Test
    void rejectsNullModifierSet() {
        assertThrows(NullPointerException.class, () -> new Shortcut(Key.Z, null));
    }

    @Test
    void rejectsNullModifierArray() {
        assertThrows(NullPointerException.class, () -> Shortcut.of(Key.Z, (Modifier[]) null));
    }

    @Test
    void rejectsNullModifier() {
        assertThrows(NullPointerException.class, () -> Shortcut.of(Key.Z, Modifier.MENU, null));
    }

    @Test
    void rejectsDuplicateModifier() {
        assertThrows(IllegalArgumentException.class, () -> Shortcut.of(Key.Z, Modifier.MENU, Modifier.MENU));
    }
}
