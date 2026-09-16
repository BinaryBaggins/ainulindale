package io.github.binarybaggins.ainulindale.ui;

import io.github.binarybaggins.ainulindale.ui.swing.NoteGridActionInstaller;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import javax.swing.JFrame;

public class NoteEditorFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final NoteEditorPanel editorPanel;

    public NoteEditorFrame(EditorWorkspace workspace, NoteGridActionInstaller actionInstaller) {
        editorPanel = new NoteEditorPanel(workspace, actionInstaller);

        setTitle("Ainulindale");
        setContentPane(editorPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
