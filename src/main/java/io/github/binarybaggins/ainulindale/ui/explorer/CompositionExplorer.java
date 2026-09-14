package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CompositionExplorer extends JPanel {

    private final EditorWorkspace workspace;
    private final JList<EditorTrack> trackList;

    public CompositionExplorer(EditorWorkspace workspace) {
        this.workspace = Objects.requireNonNull(workspace);
        this.trackList = new JList<>();
        setLayout(new BorderLayout());
        add(new JScrollPane(trackList), BorderLayout.CENTER);

        refreshTrackList();

        trackList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            EditorTrack selectedTrack = trackList.getSelectedValue();
            if (selectedTrack == null) {
                return;
            }

            workspace.setActiveTrack(selectedTrack);
        });
    }

    private void refreshTrackList() {
        // Refresh the track list with the current tracks from the workspace
        trackList.setListData(workspace.getTracks().toArray(EditorTrack[]::new));
        // Select the active track in the list, if any
        workspace.getActiveTrack().ifPresent(track -> trackList.setSelectedValue(track, true));
    }
}
