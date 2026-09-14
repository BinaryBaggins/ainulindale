package io.github.binarybaggins.ainulindale.ui.actions;

import io.github.binarybaggins.ainulindale.ui.editor.NoteGridPanel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public final class NoteGridPanelKeyBindings {

    private NoteGridPanelKeyBindings() {}

    public static void install(NoteGridPanel noteGridPanel) {
        KeyBindings.bindFocused(
            noteGridPanel,
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            "deleteSelectedNotes",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    noteGridPanel.deleteSelectedNotes();
                }
            }
        );

        KeyBindings.bindFocused(
            noteGridPanel,
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyBindings.menuShortcutMask()),
            "undo",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    noteGridPanel.undo();
                }
            }
        );

        KeyBindings.bindFocused(
            noteGridPanel,
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyBindings.menuShortcutMask()),
            "redo",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    noteGridPanel.redo();
                }
            }
        );
    }
}
