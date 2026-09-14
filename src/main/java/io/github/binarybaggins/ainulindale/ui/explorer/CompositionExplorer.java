package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

public class CompositionExplorer extends JPanel {

    private final CompositionTableModel tableModel;
    private final JTable trackTable;

    public CompositionExplorer(EditorWorkspace workspace) {
        Objects.requireNonNull(workspace);

        tableModel = new CompositionTableModel(workspace);
        trackTable = new JTable(tableModel);

        TableColumn visibilityColumn = trackTable.getColumnModel().getColumn(0);

        visibilityColumn.setMinWidth(40);
        visibilityColumn.setMaxWidth(40);
        visibilityColumn.setPreferredWidth(40);

        trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());
        add(new JScrollPane(trackTable), BorderLayout.CENTER);

        workspace.addListener(() -> refresh(workspace));

        trackTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = trackTable.getSelectedRow();
            if (row < 0) {
                return;
            }

            EditorTrack track = workspace.getTracks().get(row);
            workspace.setActiveTrack(track);
        });

        refresh(workspace);
    }

    private void refresh(EditorWorkspace workspace) {
        tableModel.refresh();

        workspace.getActiveTrack().ifPresent(activeTrack -> {
            int row = workspace.getTracks().indexOf(activeTrack);

            if (row >= 0) {
                trackTable.setRowSelectionInterval(row, row);
            }
        });
    }
}
