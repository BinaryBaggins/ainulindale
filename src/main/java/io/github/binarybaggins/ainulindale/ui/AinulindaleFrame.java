package io.github.binarybaggins.ainulindale.ui;

import javax.swing.JFrame;

import io.github.binarybaggins.ainulindale.ui.swing.CompositionExplorerActionInstaller;
import io.github.binarybaggins.ainulindale.ui.swing.NoteGridActionInstaller;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;

public class AinulindaleFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final WorkspacePanel workspacePanel;

    public AinulindaleFrame(EditorWorkspace workspace, NoteGridActionInstaller actionInstaller, CompositionExplorerActionInstaller compositionExplorerActionInstaller) {
        workspacePanel = new WorkspacePanel(workspace, actionInstaller, compositionExplorerActionInstaller);

        setTitle("Ainulindalë");
        setContentPane(workspacePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
