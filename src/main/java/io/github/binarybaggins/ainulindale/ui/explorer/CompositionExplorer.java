package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

/**
 * A panel that displays and manages the tracks in a composition.
 */
public class CompositionExplorer extends JPanel {

    private final EditorWorkspace workspace;
    private final CompositionTableModel tableModel;
    private final JTable trackTable;

    private boolean refreshing;
    private EditorTrack selectedTrack;

    /**
     * Creates a new CompositionExplorer for the given workspace.
     * @param workspace the editor workspace
     */
    public CompositionExplorer(EditorWorkspace workspace) {
        this.workspace = Objects.requireNonNull(workspace);
        tableModel = new CompositionTableModel(this.workspace);
        trackTable = new JTable(tableModel);

        TableColumn visibilityColumn = trackTable.getColumnModel().getColumn(0);
        visibilityColumn.setMinWidth(40);
        visibilityColumn.setMaxWidth(40);
        visibilityColumn.setPreferredWidth(40);

        trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());
        add(new JScrollPane(trackTable), BorderLayout.CENTER);

        workspace.addListener(this::refresh);

        trackTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || refreshing) {
                return;
            }

            int row = trackTable.getSelectedRow();

            if (row < 0) {
                selectedTrack = null;
                return;
            }

            selectedTrack = workspace.getTracks().get(row);
        });

        trackTable.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() != 1) {
                        return;
                    }

                    int row = trackTable.rowAtPoint(e.getPoint());
                    int column = trackTable.columnAtPoint(e.getPoint());

                    if (row < 0 || column != CompositionTableModel.NAME_COLUMN) {
                        return;
                    }

                    EditorTrack track = workspace.getTracks().get(row);
                    workspace.setActiveTrack(track);
                }
            }
        );

        refresh();
    }

    private void refresh() {
        refreshing = true;

        try {
            tableModel.refresh();

            if (selectedTrack != null) {
                int row = workspace.getTracks().indexOf(selectedTrack);

                if (row >= 0) {
                    trackTable.setRowSelectionInterval(row, row);
                    return;
                }
            }

            trackTable.clearSelection();
        } finally {
            refreshing = false;
        }
    }

    /**
     * Only for testing purposes.
     * @return the JTable displaying the tracks
     */
    JTable getTrackTable() {
        return trackTable;
    }

    public JComponent getActionBindingTarget() {
        return trackTable;
    }

    /**
     *  Returns the currently selected track, if any.
     * @return an Optional containing the selected track, or empty if no track is selected
     */
    private Optional<EditorTrack> getSelectedTrack() {
        return Optional.ofNullable(selectedTrack);
    }

    /**
     * Renames the currently selected track.
     */
    public void renameTrack() {
        var selectedTrackOpt = getSelectedTrack();

        int row = selectedTrackOpt.map(workspace.getTracks()::indexOf).orElse(-1);

        if (row < 0) {
            return;
        }

        if (trackTable.editCellAt(row, CompositionTableModel.NAME_COLUMN)) {
            var editorComponent = trackTable.getEditorComponent();

            if (editorComponent != null) {
                editorComponent.requestFocusInWindow();
            }
        }
    }

    public void removeTrack() {
        var selectedTrackOpt = getSelectedTrack();
        int row = selectedTrackOpt.map(workspace.getTracks()::indexOf).orElse(-1);

        if (row < 0) {
            return;
        }

        var tracks = workspace.getTracks();
        EditorTrack track = tracks.get(row);

        if (workspace.getActiveTrack().orElse(null) == track) {
            EditorTrack replacement = findReplacementTrack(tracks, row);

            if (replacement != null) {
                workspace.setActiveTrack(replacement);
            } else {
                workspace.clearActiveTrack();
            }
        }

        Result<Unit> result = workspace.removeTrack(track);

        if (result instanceof Failure<?>) {
            return;
        }

        selectNearestTrack(row);
    }

    private void selectNearestTrack(int formerRow) {
        var tracks = workspace.getTracks();

        if (tracks.isEmpty()) {
            selectedTrack = null;
            trackTable.clearSelection();
            return;
        }

        int row = Math.min(formerRow, tracks.size() - 1);

        selectedTrack = tracks.get(row);
        trackTable.setRowSelectionInterval(row, row);
    }

    /**
     * Finds a replacement track to activate when the current active track is removed.
     * This method first looks for a visible track after the removed track, and if none is found, it looks before the removed track.
     *
     * @param tracks the list of all tracks
     * @param removedRow the index of the track being removed
     * @return the replacement track, or null if none found
     */
    private EditorTrack findReplacementTrack(List<EditorTrack> tracks, int removedRow) {
        var visibleTracks = workspace.getVisibleTracks();

        for (int i = removedRow + 1; i < tracks.size(); i++) {
            EditorTrack candidate = tracks.get(i);

            if (visibleTracks.contains(candidate)) {
                return candidate;
            }
        }

        for (int i = removedRow - 1; i >= 0; i--) {
            EditorTrack candidate = tracks.get(i);

            if (visibleTracks.contains(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * Moves the selected track up by one position.
     */
    public void moveTrackUp() {
        moveSelectedTrackBy(-1);
    }

    /**
     * Moves the selected track down by one position.
     */
    public void moveTrackDown() {
        moveSelectedTrackBy(1);
    }

    private void moveSelectedTrackBy(int offset) {
        var selectedTrackOpt = getSelectedTrack();
        int row = selectedTrackOpt.map(workspace.getTracks()::indexOf).orElse(-1);

        if (row < 0) {
            return;
        }

        int targetRow = row + offset;
        var tracks = workspace.getTracks();

        if (targetRow < 0 || targetRow >= tracks.size()) {
            return;
        }

        EditorTrack track = tracks.get(row);
        workspace.moveTrack(track, targetRow);
    }

    /**
     * Adds a new track to the composition.
     */
    public void addTrack() {
        EditorTrack track = new EditorTrack(createNewTrackName());

        Result<Unit> result = workspace.addTrack(track);
        if (result instanceof Failure<?>) {
            return;
        }

        selectedTrack = track;
        workspace.setActiveTrack(track);
    }

    private String createNewTrackName() {
        int number = 1;

        while (true) {
            String name = "Track " + number; // TODO: i18n

            boolean exists = workspace
                .getTracks()
                .stream()
                .anyMatch(track -> track.getName().equals(name));

            if (!exists) {
                return name;
            }

            number++;
        }
    }
}
