package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.Test;

public class ShortcutKeyStrokeMapperTest {

    @Test
    void mapsShortcutWithoutModifiers() {
        ShortcutKeyStrokeMapper mapper = new ShortcutKeyStrokeMapper(InputEvent.CTRL_DOWN_MASK);

        assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), mapper.toKeyStroke(Shortcut.of(Key.DELETE)));
    }

    @Test
    void mapsMenuShortcutUsingConfiguredMask() {
        ShortcutKeyStrokeMapper mapper = new ShortcutKeyStrokeMapper(InputEvent.META_DOWN_MASK);

        assertEquals(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK),
            mapper.toKeyStroke(Shortcut.of(Key.Z, Modifier.MENU))
        );
    }

    @Test
    void mapsCombinedModifiers() {
        ShortcutKeyStrokeMapper mapper = new ShortcutKeyStrokeMapper(InputEvent.CTRL_DOWN_MASK);

        assertEquals(
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
            mapper.toKeyStroke(Shortcut.of(Key.Y, Modifier.CTRL, Modifier.SHIFT))
        );
    }

    @Test
    void rejectsNullShortcut() {
        ShortcutKeyStrokeMapper mapper = new ShortcutKeyStrokeMapper(InputEvent.CTRL_DOWN_MASK);

        assertThrows(NullPointerException.class, () -> mapper.toKeyStroke(null));
    }
}
