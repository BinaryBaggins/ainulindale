package io.github.binarybaggins.ainulindale.ui.actions;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public final class KeyBindings {

    private KeyBindings() {}

    private static final int SHORTCUT_MASK = getShortcutMask();

    public static int menuShortcutMask() {
        return SHORTCUT_MASK;
    }

    public static void bind(
        JComponent component,
        int condition,
        KeyStroke keyStroke,
        String actionName,
        Action action
    ) {
        component.getInputMap(condition).put(keyStroke, actionName);

        component.getActionMap().put(actionName, action);
    }

    public static void bindFocused(JComponent component, KeyStroke keyStroke, String actionName, Action action) {
        bind(component, JComponent.WHEN_FOCUSED, keyStroke, actionName, action);
    }

    private static int getShortcutMask() {
        if (GraphicsEnvironment.isHeadless()) {
            return InputEvent.CTRL_DOWN_MASK;
        }

        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    }
}
