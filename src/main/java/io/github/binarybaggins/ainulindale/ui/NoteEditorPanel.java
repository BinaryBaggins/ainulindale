package io.github.binarybaggins.ainulindale.ui;

import io.github.binarybaggins.ainulindale.ui.components.EditorAreaPanel;
import io.github.binarybaggins.ainulindale.ui.components.ToolbarPanel;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class NoteEditorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ToolbarPanel toolbarPanel;
    private final EditorAreaPanel editorAreaPanel;

    public NoteEditorPanel(EditorWorkspace workspace) {
        toolbarPanel = new ToolbarPanel();
        editorAreaPanel = new EditorAreaPanel(workspace);

        setLayout(new BorderLayout());

        add(toolbarPanel, BorderLayout.NORTH);
        add(editorAreaPanel, BorderLayout.CENTER);
    }
}
