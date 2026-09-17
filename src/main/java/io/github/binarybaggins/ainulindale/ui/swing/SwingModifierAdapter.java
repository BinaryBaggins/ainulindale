package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.ui.shortcut.Modifier;
import java.awt.event.InputEvent;
import java.util.Set;

/**
 * Utility class for converting custom Modifier enums to Swing modifier masks.
 * Provides methods to convert individual modifiers and sets of modifiers to Swing modifier masks.
 */
final class SwingModifierAdapter {

    private final int menuShortcutMask;

    /**
     * Constructs a SwingModifierAdapter with the specified menu shortcut mask.
     *
     * @param menuShortcutMask the menu shortcut mask to use
     */
    SwingModifierAdapter(int menuShortcutMask) {
        this.menuShortcutMask = menuShortcutMask;
    }

    /**
     * Converts a single modifier to its corresponding modifier mask.
     *
     * @param modifier the modifier to convert
     * @return the corresponding modifier mask
     */
    int toModifierMask(Modifier modifier) {
        return switch (modifier) {
            case MENU -> menuShortcutMask;
            case CTRL -> InputEvent.CTRL_DOWN_MASK;
            case ALT -> InputEvent.ALT_DOWN_MASK;
            case SHIFT -> InputEvent.SHIFT_DOWN_MASK;
            case META -> InputEvent.META_DOWN_MASK;
        };
    }

    /**
     * Converts a set of modifiers to a combined modifier mask.
     *
     * @param modifiers the set of modifiers to convert
     * @return the combined modifier mask
     */
    int toModifierMask(Set<Modifier> modifiers) {
        int mask = 0;

        for (Modifier modifier : modifiers) {
            mask |= toModifierMask(modifier);
        }

        return mask;
    }
}
