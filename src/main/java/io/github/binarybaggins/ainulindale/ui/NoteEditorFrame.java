package io.github.binarybaggins.ainulindale.ui;

import javax.swing.JFrame;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;

public class NoteEditorFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final NoteEditorPanel editorPanel;

    public NoteEditorFrame(EditorWorkspace workspace) {
        editorPanel = new NoteEditorPanel(workspace);

        setTitle("Maestro Note Editor");
        setContentPane(editorPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // later change to DISPOSE_ON_CLOSE

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
