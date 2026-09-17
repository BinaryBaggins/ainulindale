package io.github.binarybaggins.ainulindale.ui.swing.action;

import io.github.binarybaggins.ainulindale.ui.editor.NoteGridPanel;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;

public final class RedoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final NoteGridPanel noteGridPanel;

    public RedoAction(NoteGridPanel noteGridPanel) {
        this.noteGridPanel = Objects.requireNonNull(noteGridPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        noteGridPanel.redo();
    }
}
