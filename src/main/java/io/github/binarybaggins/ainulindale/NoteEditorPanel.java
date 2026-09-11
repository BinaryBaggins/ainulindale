package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.components.EditorAreaPanel;
import io.github.binarybaggins.ainulindale.components.ToolbarPanel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class NoteEditorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ToolbarPanel toolbarPanel;
    private final EditorAreaPanel editorAreaPanel;

    public NoteEditorPanel() {
        toolbarPanel = new ToolbarPanel();
        editorAreaPanel = new EditorAreaPanel();

        setLayout(new BorderLayout());

        add(toolbarPanel, BorderLayout.NORTH);
        add(editorAreaPanel, BorderLayout.CENTER);
    }
}
