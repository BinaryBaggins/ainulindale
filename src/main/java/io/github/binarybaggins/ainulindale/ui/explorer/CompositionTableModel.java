package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;

final class CompositionTableModel extends AbstractTableModel {

    private static final int VISIBILITY_COLUMN = 0;
    private static final int NAME_COLUMN = 1;

    private final EditorWorkspace workspace;

    public CompositionTableModel(EditorWorkspace workspace) {
        this.workspace = Objects.requireNonNull(workspace);
    }

    @Override
    public int getRowCount() {
        return workspace.getTracks().size();
    }

    @Override
    public int getColumnCount() {
        return 2; // VISIBILITY_COLUMN and NAME_COLUMN
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case VISIBILITY_COLUMN -> "";
            case NAME_COLUMN -> "Track"; // TODO: i18n
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case VISIBILITY_COLUMN -> Boolean.class;
            case NAME_COLUMN -> String.class;
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EditorTrack track = workspace.getTracks().get(rowIndex);

        switch (columnIndex) {
            case VISIBILITY_COLUMN: {
                Result<Boolean> result = workspace.isTrackVisible(track);
                if (result instanceof Success<Boolean> success) {
                    return success.value();
                }
                throw new IllegalStateException("Workspace track unexpectedly has no visibility state");
            }
            case NAME_COLUMN:
                return track.getName();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == VISIBILITY_COLUMN;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex != VISIBILITY_COLUMN) {
            return;
        }

        EditorTrack track = workspace.getTracks().get(rowIndex);
        boolean visible = (Boolean) value;

        Result<Unit> result = workspace.setTrackVisible(track, visible);
        if (result instanceof Failure<?>) {
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
