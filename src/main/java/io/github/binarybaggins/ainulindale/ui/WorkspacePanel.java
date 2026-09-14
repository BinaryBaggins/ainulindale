package io.github.binarybaggins.ainulindale.ui;

import io.github.binarybaggins.ainulindale.ui.editor.PianoRollPanel;
import io.github.binarybaggins.ainulindale.ui.toolbar.EditorToolbar;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * 
 * WorkspacePanel
 */
public class WorkspacePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final EditorToolbar toolbarPanel;
    private final PianoRollPanel pianoRollPanel;

    public WorkspacePanel(EditorWorkspace workspace) {
        toolbarPanel = new EditorToolbar();
        pianoRollPanel = new PianoRollPanel();

        workspace.getActiveTrackEditor().ifPresent(pianoRollPanel::setTrackEditorModel);

        setLayout(new BorderLayout());

        add(toolbarPanel, BorderLayout.NORTH);
        add(pianoRollPanel, BorderLayout.CENTER);
    }
}
