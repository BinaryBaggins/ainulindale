package io.github.binarybaggins.ainulindale.ui.actions;

import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;

public final class CompositionExplorerActions {

    private final EditorWorkspace workspace;
    private final JTable trackTable;

    private final Action addTrackAction;
    private final Action removeTrackAction;
    private final Action renameTrackAction;

    public CompositionExplorerActions(EditorWorkspace workspace, JTable trackTable) {
        this.workspace = Objects.requireNonNull(workspace);
        this.trackTable = Objects.requireNonNull(trackTable);

        addTrackAction = new AbstractAction("Add Track") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTrack();
            }
        };

        removeTrackAction = new AbstractAction("Remove Track") {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedTrack();
            }
        };

        renameTrackAction = new AbstractAction("Rename Track") {
            @Override
            public void actionPerformed(ActionEvent e) {
                beginRenameSelectedTrack();
            }
        };
    }

    public Action addTrack() {
        return addTrackAction;
    }

    public Action removeTrack() {
        return removeTrackAction;
    }

    public Action renameTrack() {
        return renameTrackAction;
    }

    // private implementation methods...
}
