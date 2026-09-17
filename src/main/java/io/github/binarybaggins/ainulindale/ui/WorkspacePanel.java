package io.github.binarybaggins.ainulindale.ui;

import io.github.binarybaggins.ainulindale.ui.editor.PianoRollPanel;
import io.github.binarybaggins.ainulindale.ui.explorer.CompositionExplorer;
import io.github.binarybaggins.ainulindale.ui.explorer.ProjectExplorer;
import io.github.binarybaggins.ainulindale.ui.toolbar.EditorToolbar;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import io.github.binarybaggins.ainulindale.ui.swing.CompositionExplorerActionInstaller;
import io.github.binarybaggins.ainulindale.ui.swing.NoteGridActionInstaller;
import javax.swing.JTabbedPane;

public class WorkspacePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final EditorToolbar editorToolbar;
    private final PianoRollPanel pianoRollPanel;
    private final CompositionExplorer compositionExplorer;
    private final ProjectExplorer projectExplorer;

    public WorkspacePanel(EditorWorkspace workspace, NoteGridActionInstaller actionInstaller, CompositionExplorerActionInstaller compositionExplorerActionInstaller) {
        editorToolbar = new EditorToolbar();
        pianoRollPanel = new PianoRollPanel(actionInstaller);
        compositionExplorer = new CompositionExplorer(workspace);
        compositionExplorerActionInstaller.install(compositionExplorer);
        projectExplorer = new ProjectExplorer();

        syncActiveTrackEditor(workspace);

        workspace.addListener(() -> syncActiveTrackEditor(workspace));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Composition", compositionExplorer); // TODO: i18n
        tabbedPane.addTab("Project", projectExplorer); // TODO: i18n

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
