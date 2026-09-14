package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;
import javax.swing.DefaultListCellRenderer;
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

        // Listen for changes in the workspace and refresh the track list accordingly
        workspace.addListener(this::refreshTrackList);

        // Initial refresh of the track list
        refreshTrackList();

        trackList.setCellRenderer(
            new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
                ) {
                    EditorTrack track = (EditorTrack) value;
                    return super.getListCellRendererComponent(list, track.getName(), index, isSelected, cellHasFocus);
                }
            }
        );

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
