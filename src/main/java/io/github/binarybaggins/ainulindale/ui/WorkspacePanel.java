package io.github.binarybaggins.ainulindale.ui;

import io.github.binarybaggins.ainulindale.ui.editor.PianoRollPanel;
import io.github.binarybaggins.ainulindale.ui.explorer.CompositionExplorer;
import io.github.binarybaggins.ainulindale.ui.explorer.ProjectExplorer;
import io.github.binarybaggins.ainulindale.ui.toolbar.EditorToolbar;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class WorkspacePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final EditorToolbar editorToolbar;
    private final PianoRollPanel pianoRollPanel;
    private final CompositionExplorer compositionExplorer;
    private final ProjectExplorer projectExplorer;

    public WorkspacePanel(EditorWorkspace workspace) {
        editorToolbar = new EditorToolbar();
        pianoRollPanel = new PianoRollPanel();
        compositionExplorer = new CompositionExplorer(workspace);
        projectExplorer = new ProjectExplorer();

        workspace.getActiveTrackEditor().ifPresent(pianoRollPanel::setTrackEditorModel);

        workspace.addListener(() -> workspace.getActiveTrackEditor().ifPresent(pianoRollPanel::setTrackEditorModel));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Composition Explorer", compositionExplorer);
        tabbedPane.addTab("Project Explorer", projectExplorer);

        setLayout(new BorderLayout());

        add(editorToolbar, BorderLayout.NORTH);
        add(pianoRollPanel, BorderLayout.CENTER);
        add(tabbedPane, BorderLayout.WEST);
    }
}
