package io.github.binarybaggins.ainulindale.ui.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

public class CompositionExplorerTest {

    @Test
    public void selectingVisibleTrackChangesActiveTrack() throws Exception {
        EditorWorkspace workspace = new EditorWorkspace();

        EditorTrack trackA = new EditorTrack("Track A");
        EditorTrack trackB = new EditorTrack("Track B");

        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);

        SwingUtilities.invokeAndWait(() -> {
            CompositionExplorer explorer = new CompositionExplorer(workspace);

            explorer.getTrackTable().setRowSelectionInterval(1, 1);

            assertSame(trackB, workspace.getActiveTrack().orElseThrow());
        });
    }

    @Test
    public void selectingHiddenTrackRestoresActiveTrackSelection() throws Exception {
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
            assertEquals(0, explorer.getTrackTable().getSelectedRow());
        });
    }
}
