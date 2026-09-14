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

        syncActiveTrackEditor(workspace);

        workspace.addListener(() -> syncActiveTrackEditor(workspace));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Composition", compositionExplorer);
        tabbedPane.addTab("Project", projectExplorer);

        setLayout(new BorderLayout());

        add(editorToolbar, BorderLayout.NORTH);
        add(pianoRollPanel, BorderLayout.CENTER);
        add(tabbedPane, BorderLayout.WEST);
    }

    private void syncActiveTrackEditor(EditorWorkspace workspace) {
        workspace.getActiveTrackEditor().ifPresentOrElse(
            model -> {
                pianoRollPanel.setTrackEditorModel(model);
                pianoRollPanel.setVisible(true);
            },
            () -> pianoRollPanel.setVisible(false)
        );
    }
}
