package io.github.binarybaggins.ainulindale.ui.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionDefinition;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;
import io.github.binarybaggins.ainulindale.ui.actions.ActionRegistry;
import io.github.binarybaggins.ainulindale.ui.actions.ActionScope;
import io.github.binarybaggins.ainulindale.ui.shortcut.Key;
import io.github.binarybaggins.ainulindale.ui.shortcut.Modifier;
import io.github.binarybaggins.ainulindale.ui.shortcut.Shortcut;
import io.github.binarybaggins.ainulindale.ui.shortcut.ShortcutManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwingActionBinderTest {

    private static final ActionDefinition DELETE_DEFINITION = new ActionDefinition(
        ActionId.NOTE_DELETE,
        ActionScope.NOTE_GRID,
        "action.note.delete.name",
        "action.note.delete.description",
        Shortcut.of(Key.DELETE)
    );

    private JPanel component;
    private Action action;
    private ShortcutManager shortcutManager;
    private SwingActionBinder binder;

    @BeforeEach
    void setup() {
        ActionCatalog catalog = new ActionCatalog(List.of(DELETE_DEFINITION));

        ActionRegistry registry = new ActionRegistry();

        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {}
        };

        registry.register(ActionId.NOTE_DELETE, action);

        shortcutManager = new ShortcutManager(new Settings(new InMemorySettingsStore()), catalog);

        binder = new SwingActionBinder(
            catalog,
            registry,
            shortcutManager,
            new ShortcutKeyStrokeMapper(InputEvent.CTRL_DOWN_MASK)
        );

        component = new JPanel();
    }

    @Test
    void bindsActionUsingEffectiveShortcut() {
        binder.bind(component, ActionId.NOTE_DELETE);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

        assertEquals(ActionId.NOTE_DELETE, component.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke));

        assertSame(action, component.getActionMap().get(ActionId.NOTE_DELETE));
    }

    @Test
    void disabledShortcutRemovesBindingButKeepsAction() {
        binder.bind(component, ActionId.NOTE_DELETE);

        shortcutManager.disableShortcut(ActionId.NOTE_DELETE);

        binder.bind(component, ActionId.NOTE_DELETE);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

        assertNull(component.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke));

        assertSame(action, component.getActionMap().get(ActionId.NOTE_DELETE));
    }

    @Test
    void rebindingReplacesPreviousShortcut() {
        binder.bind(component, ActionId.NOTE_DELETE);

        shortcutManager.setShortcut(ActionId.NOTE_DELETE, Shortcut.of(Key.Z, Modifier.CTRL));

        binder.bind(component, ActionId.NOTE_DELETE);

        KeyStroke oldKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

        KeyStroke newKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);

        var inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);

        assertNull(inputMap.get(oldKeyStroke));

        assertEquals(ActionId.NOTE_DELETE, inputMap.get(newKeyStroke));
    }

    @Test
    void rejectsNullComponent() {
        assertThrows(NullPointerException.class, () -> binder.bind(null, ActionId.NOTE_DELETE));
    }

    @Test
    void rejectsNullActionId() {
        assertThrows(NullPointerException.class, () -> binder.bind(component, null));
    }

    @Test
    void rejectsActionWithoutDefinition() {
        assertThrows(IllegalArgumentException.class, () -> binder.bind(component, ActionId.NOTE_UNDO));
    }

    @Test
    void rejectsUnregisteredAction() {
        ActionCatalog catalog = new ActionCatalog(List.of(DELETE_DEFINITION));

        ShortcutManager manager = new ShortcutManager(new Settings(new InMemorySettingsStore()), catalog);

        SwingActionBinder binder = new SwingActionBinder(
            catalog,
            new ActionRegistry(),
            manager,
            new ShortcutKeyStrokeMapper(InputEvent.CTRL_DOWN_MASK)
        );

        assertThrows(IllegalStateException.class, () -> binder.bind(component, ActionId.NOTE_DELETE));
    }
}
