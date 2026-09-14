package io.github.binarybaggins.ainulindale.ui.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompositionTableModelTest {

    private EditorWorkspace workspace;
    private CompositionTableModel model;
    private EditorTrack trackA;

    @BeforeEach
    public void setUp() {
        workspace = new EditorWorkspace();
        trackA = new EditorTrack("Track A");
        workspace.addTrack(trackA);
        model = new CompositionTableModel(workspace);
    }

    @SuppressWarnings("unchecked")
    private static <T> T successValue(Result<T> result) {
        Success<T> success = (Success<T>) assertInstanceOf(Success.class, result);
        return success.value();
    }

    @Test
    public void visibilityColumnReturnsBoolean() {
        assertEquals(Boolean.class, model.getColumnClass(0));
        assertEquals(Boolean.TRUE, model.getValueAt(0, 0));
    }

    @Test
    public void visibilityChangeUpdatesWorkspace() {
        model.setValueAt(false, 0, 0);

        assertEquals(Boolean.FALSE, successValue(workspace.isTrackVisible(trackA)));
    }

    @Test
    public void activeTrackCannotBeHiddenThroughTableModel() {
        workspace.setActiveTrack(trackA);

        model.setValueAt(false, 0, 0);

        assertSame(trackA, workspace.getActiveTrack().orElseThrow());
        assertEquals(Boolean.TRUE, successValue(workspace.isTrackVisible(trackA)));
        assertEquals(Boolean.TRUE, model.getValueAt(0, 0));
    }

    @Test
    public void nameColumnReturnsTrackName() {
        assertEquals(String.class, model.getColumnClass(1));
        assertEquals("Track A", model.getValueAt(0, 1));
    }

    @Test
    public void failedVisibilityChangeRefreshesCell() {
        workspace.setActiveTrack(trackA);

        AtomicInteger eventCount = new AtomicInteger();

        model.addTableModelListener(event -> {
            if (event.getFirstRow() == 0 && event.getColumn() == 0) {
                eventCount.incrementAndGet();
            }
        });

        model.setValueAt(false, 0, 0);

        assertEquals(1, eventCount.get());
    }
}
