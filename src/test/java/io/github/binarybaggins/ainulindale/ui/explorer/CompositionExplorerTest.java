package io.github.binarybaggins.ainulindale.ui.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

public class CompositionExplorerTest {

    @Test
    public void selectingTrackDoesNotChangeActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);

            explorer.getTrackTable().setRowSelectionInterval(1, 1);

            assertSame(trackA, workspace.getActiveTrack().orElseThrow());
            assertEquals(1, explorer.getTrackTable().getSelectedRow());
        });
    }

    @Test
    public void hiddenTrackCanBeSelectedWithoutChangingActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);
        workspace.setTrackVisible(trackB, false);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);

            explorer.getTrackTable().setRowSelectionInterval(1, 1);

            assertSame(trackA, workspace.getActiveTrack().orElseThrow());
            assertEquals(1, explorer.getTrackTable().getSelectedRow());
        });
    }

    @Test
    public void clickingTrackNameChangesActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);
            JTable table = explorer.getTrackTable();

            Rectangle cell = table.getCellRect(1, CompositionTableModel.NAME_COLUMN, true);

            table.dispatchEvent(
                new MouseEvent(
                    table,
                    MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(),
                    0,
                    cell.x + cell.width / 2,
                    cell.y + cell.height / 2,
                    1,
                    false,
                    MouseEvent.BUTTON1
                )
            );

            assertSame(trackB, workspace.getActiveTrack().orElseThrow());
        });
    }

    @Test
    public void activatingSelectedVisibleTrackChangesActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);

            explorer.getTrackTable().setRowSelectionInterval(1, 1);
            explorer.activateSelectedTrack();

            assertSame(trackB, workspace.getActiveTrack().orElseThrow());
            assertEquals(1, explorer.getTrackTable().getSelectedRow());
        });
    }

    @Test
    public void activatingSelectedHiddenTrackKeepsCurrentActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);
        workspace.setTrackVisible(trackB, false);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);

            explorer.getTrackTable().setRowSelectionInterval(1, 1);
            explorer.activateSelectedTrack();

            assertSame(trackA, workspace.getActiveTrack().orElseThrow());
            assertEquals(1, explorer.getTrackTable().getSelectedRow());
        });
    }
}
