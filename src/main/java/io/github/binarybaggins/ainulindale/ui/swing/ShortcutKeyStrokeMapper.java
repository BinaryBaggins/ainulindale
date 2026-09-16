package io.github.binarybaggins.ainulindale.ui.swing;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.Objects;
import javax.swing.KeyStroke;

import io.github.binarybaggins.ainulindale.ui.shortcut.Shortcut;

final class ShortcutKeyStrokeMapper {

    private final SwingModifierAdapter modifierAdapter;

    /**
     * Constructs a ShortcutKeyStrokeMapper with the default menu shortcut mask.
     */
    ShortcutKeyStrokeMapper() {
        this(resolveMenuShortcutMask());
    }

    /**
     * Constructs a ShortcutKeyStrokeMapper with the specified menu shortcut mask.
     *
     * @param menuShortcutMask the menu shortcut mask to use
     */
    ShortcutKeyStrokeMapper(int menuShortcutMask) {
        modifierAdapter = new SwingModifierAdapter(menuShortcutMask);
    }

    /**
     * Converts a Shortcut to a KeyStroke.
     *
     * @param shortcut the shortcut to convert
     * @return the corresponding KeyStroke
     */
    KeyStroke toKeyStroke(Shortcut shortcut) {
        Objects.requireNonNull(shortcut, "shortcut must not be null");

        int keyCode = SwingKeyAdapter.toKeyCode(shortcut.key());

        int modifierMask = modifierAdapter.toModifierMask(shortcut.modifiers());

        return KeyStroke.getKeyStroke(keyCode, modifierMask);
    }

    /**
     * Resolves the menu shortcut mask based on the current environment.
     * If the environment is headless, the CTRL_DOWN_MASK key is used as a fallback.
     *
     * @return the menu shortcut mask
     */
    private static int resolveMenuShortcutMask() {
        if (GraphicsEnvironment.isHeadless()) {
            return InputEvent.CTRL_DOWN_MASK;
        }

        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    }
}
