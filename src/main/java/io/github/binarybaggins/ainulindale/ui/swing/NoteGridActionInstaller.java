package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;
import io.github.binarybaggins.ainulindale.ui.actions.ActionRegistry;
import io.github.binarybaggins.ainulindale.ui.components.NoteGridPanel;
import io.github.binarybaggins.ainulindale.ui.shortcut.ShortcutManager;
import io.github.binarybaggins.ainulindale.ui.swing.action.DeleteSelectedNotesAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.RedoAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.UndoAction;
import java.util.Objects;

/**
 * Installs actions for a NoteGridPanel, including delete, undo, and redo actions.
 */
public final class NoteGridActionInstaller {

    private final ActionRegistry registry;
    private final SwingActionBinder binder;

    /**
     * Constructs a new NoteGridActionInstaller with the given action catalog, action registry, and shortcut manager.
     *
     * @param catalog the action catalog
     * @param registry the action registry
     * @param shortcutManager the shortcut manager
     */
    public NoteGridActionInstaller(ActionCatalog catalog, ActionRegistry registry, ShortcutManager shortcutManager) {
        this.registry = Objects.requireNonNull(registry);
        this.binder = new SwingActionBinder(catalog, registry, shortcutManager, new ShortcutKeyStrokeMapper());
    }

    /**
     * Installs the necessary actions for the given note grid panel.
     *
     * @param noteGridPanel the note grid panel to install actions for
     */
    public void install(NoteGridPanel noteGridPanel) {
        // Register actions for the note grid panel to the action registry
        registry.register(ActionId.NOTE_DELETE, new DeleteSelectedNotesAction(noteGridPanel));
        registry.register(ActionId.NOTE_UNDO, new UndoAction(noteGridPanel));
        registry.register(ActionId.NOTE_REDO, new RedoAction(noteGridPanel));

        // Bind actions to the note grid panel
        binder.bind(noteGridPanel, ActionId.NOTE_DELETE);
        binder.bind(noteGridPanel, ActionId.NOTE_UNDO);
        binder.bind(noteGridPanel, ActionId.NOTE_REDO);
    }
}
