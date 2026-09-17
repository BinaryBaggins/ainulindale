package io.github.binarybaggins.ainulindale.ui.explorer;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.Component;
import java.awt.Font;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

final class TrackNameCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final EditorWorkspace workspace;

    TrackNameCellRenderer(EditorWorkspace workspace) {
        this.workspace = Objects.requireNonNull(workspace);
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        EditorTrack track = workspace.getTracks().get(row);
        boolean active = workspace.getActiveTrack().orElse(null) == track;

        setText(active ? "▶ " + value : String.valueOf(value));

        Font baseFont = table.getFont();
        setFont(active ? baseFont.deriveFont(Font.BOLD) : baseFont);

        return this;
    }
}
