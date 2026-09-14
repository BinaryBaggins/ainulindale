package io.github.binarybaggins.ainulindale.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EditorWorkspaceTest {

    private EditorWorkspace workspace;
    private EditorTrack trackA;
    private EditorTrack trackB;
    private EditorTrack trackC;

    @BeforeEach
    public void setUp() {
        workspace = new EditorWorkspace();
        trackA = new EditorTrack("Track A");
        trackB = new EditorTrack("Track B");
        trackC = new EditorTrack("Track C");
    }

    private static void assertFailure(Result<?> result, ResultError expectedError) {
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertSame(expectedError, failure.error());
    }

    @SuppressWarnings("unchecked")
    private static <T> T successValue(Result<T> result) {
        Success<T> success = (Success<T>) assertInstanceOf(Success.class, result);
        return success.value();
    }

    // --- identity-based membership and duplicate add failure ---

    @Test
    public void addTrackSucceedsForNewTrack() {
        Result<?> result = workspace.addTrack(trackA);

        assertInstanceOf(Success.class, result);
        assertSame(trackA, workspace.getTracks().getFirst());
    }

    @Test
    public void addTrackRejectsSameInstanceAddedTwice() {
        workspace.addTrack(trackA);

        Result<?> result = workspace.addTrack(trackA);

        assertFailure(result, WorkspaceErrors.TRACK_ALREADY_PRESENT);
        assertEquals(1, workspace.getTracks().size());
    }

    @Test
    public void addTrackTreatsDistinctInstancesWithSameNameAsDifferentTracks() {
        EditorTrack duplicateName = new EditorTrack("Track A");

        workspace.addTrack(trackA);
        Result<?> result = workspace.addTrack(duplicateName);

        assertInstanceOf(Success.class, result);
        assertEquals(2, workspace.getTracks().size());
    }

    @Test
    public void removeTrackFailsForUnknownTrack() {
        Result<?> result = workspace.removeTrack(trackA);

        assertFailure(result, WorkspaceErrors.TRACK_NOT_FOUND);
    }

    // --- ordered exposure and structural snapshots ---

    @Test
    public void getTracksReturnsTracksInInsertionOrder() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);

        List<EditorTrack> tracks = workspace.getTracks();

        assertEquals(2, tracks.size());
        assertSame(trackA, tracks.get(0));
        assertSame(trackB, tracks.get(1));
    }

    @Test
    public void getTracksReturnsSnapshotUnaffectedByLaterMutation() {
        workspace.addTrack(trackA);

        List<EditorTrack> snapshot = workspace.getTracks();
        workspace.addTrack(trackB);

        assertEquals(List.of(trackA), snapshot);
    }

    @Test
    public void getVisibleTracksExcludesHiddenTracksButPreservesOrder() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.addTrack(trackC);

        workspace.setTrackVisible(trackB, false);

        List<EditorTrack> tracks = workspace.getVisibleTracks();
        assertEquals(2, tracks.size());
        assertSame(trackA, tracks.get(0));
        assertSame(trackC, tracks.get(1));
    }

    @Test
    public void getVisibleTracksReturnsSnapshotUnaffectedByLaterMutation() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);

        List<EditorTrack> snapshot = workspace.getVisibleTracks();
        workspace.setTrackVisible(trackB, false);

        assertEquals(List.of(trackA, trackB), snapshot);
    }

    // --- zero-or-one active track behavior ---

    @Test
    public void noTrackIsActiveByDefault() {
        assertTrue(workspace.getActiveTrack().isEmpty());
        assertTrue(workspace.getActiveTrackEditor().isEmpty());
    }

    @Test
    public void setActiveTrackReplacesPreviouslyActiveTrack() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);

        workspace.setActiveTrack(trackA);
        workspace.setActiveTrack(trackB);

        assertSame(trackB, workspace.getActiveTrack().orElseThrow());
    }

    @Test
    public void clearActiveTrackLeavesNoTrackActive() {
        workspace.addTrack(trackA);
        workspace.setActiveTrack(trackA);

        workspace.clearActiveTrack();

        assertTrue(workspace.getActiveTrack().isEmpty());
        assertTrue(workspace.getActiveTrackEditor().isEmpty());
    }

    @Test
    public void setActiveTrackFailsForUnknownTrack() {
        Result<?> result = workspace.setActiveTrack(trackA);

        assertFailure(result, WorkspaceErrors.TRACK_NOT_FOUND);
    }

    // --- active-implies-visible invariant ---

    @Test
    public void activeTrackIsAlwaysVisible() {
        workspace.addTrack(trackA);

        workspace.setActiveTrack(trackA);

        assertTrue(successValue(workspace.isTrackVisible(trackA)));
    }

    @Test
    public void hidingNonActiveTrackDoesNotAffectActiveTrackVisibility() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.setActiveTrack(trackA);

        workspace.setTrackVisible(trackB, false);

        assertSame(trackA, workspace.getActiveTrack().orElseThrow());
        assertTrue(successValue(workspace.isTrackVisible(trackA)));
    }

    // --- hidden-track activation rejection, active-track hide rejection, active-track remove rejection ---

    @Test
    public void setActiveTrackRejectsHiddenTrack() {
        workspace.addTrack(trackA);
        workspace.setTrackVisible(trackA, false);

        Result<?> result = workspace.setActiveTrack(trackA);

        assertFailure(result, WorkspaceErrors.TRACK_NOT_VISIBLE);
        assertTrue(workspace.getActiveTrack().isEmpty());
    }

    @Test
    public void setTrackVisibleRejectsHidingActiveTrack() {
        workspace.addTrack(trackA);
        workspace.setActiveTrack(trackA);

        Result<?> result = workspace.setTrackVisible(trackA, false);

        assertFailure(result, WorkspaceErrors.ACTIVE_TRACK_CANNOT_BE_HIDDEN);
        assertTrue(successValue(workspace.isTrackVisible(trackA)));
    }

    @Test
    public void removeTrackRejectsRemovingActiveTrack() {
        workspace.addTrack(trackA);
        workspace.setActiveTrack(trackA);

        Result<?> result = workspace.removeTrack(trackA);

        assertFailure(result, WorkspaceErrors.ACTIVE_TRACK_CANNOT_BE_REMOVED);
        assertSame(trackA, workspace.getTracks().getFirst());
        assertSame(trackA, workspace.getActiveTrack().orElseThrow());
    }

    // --- active-track switching preserves per-track editor model and undo/redo history ---

    @Test
    public void switchingActiveTrackAndBackPreservesSameEditorModelInstance() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);

        workspace.setActiveTrack(trackA);
        TrackEditorModel editorForA = workspace.getActiveTrackEditor().orElseThrow();

        workspace.setActiveTrack(trackB);
        workspace.setActiveTrack(trackA);

        assertSame(editorForA, workspace.getActiveTrackEditor().orElseThrow());
    }

    @Test
    public void switchingActiveTrackPreservesUndoRedoHistory() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);

        workspace.setActiveTrack(trackA);
        TrackEditorModel editorForA = workspace.getActiveTrackEditor().orElseThrow();

        editorForA.createNote(60, 0.0, 1.0);
        assertTrue(editorForA.canUndo());

        workspace.setActiveTrack(trackB);
        workspace.setActiveTrack(trackA);

        TrackEditorModel editorForAAgain = workspace.getActiveTrackEditor().orElseThrow();

        assertSame(editorForA, editorForAAgain);
        assertTrue(editorForAAgain.canUndo());
        assertTrue(editorForAAgain.undo());
        assertTrue(editorForAAgain.canRedo());

        workspace.setActiveTrack(trackB);
        workspace.setActiveTrack(trackA);

        TrackEditorModel editorForAAfterRedoSwitch = workspace.getActiveTrackEditor().orElseThrow();

        assertSame(editorForAAgain, editorForAAfterRedoSwitch);
        assertTrue(editorForAAfterRedoSwitch.canRedo());
        assertTrue(editorForAAfterRedoSwitch.redo());
    }

    // --- remove-then-re-add creates a fresh editor model/history ---

    @Test
    public void removingAndReAddingTrackCreatesFreshEditorModel() {
        workspace.addTrack(trackA);
        workspace.setActiveTrack(trackA);
        TrackEditorModel originalEditor = workspace.getActiveTrackEditor().orElseThrow();
        originalEditor.createNote(60, 0.0, 1.0);

        workspace.clearActiveTrack();
        workspace.removeTrack(trackA);
        workspace.addTrack(trackA);
        workspace.setActiveTrack(trackA);

        TrackEditorModel newEditor = workspace.getActiveTrackEditor().orElseThrow();
        assertNotSame(originalEditor, newEditor);
        assertFalse(newEditor.canUndo());
        assertFalse(newEditor.canRedo());
    }

    // --- expected Result failures vs. exceptional programming errors ---

    @Test
    public void expectedFailuresAreReturnedAsResultsNotThrown() {
        workspace.addTrack(trackA);

        Result<?> duplicateAdd = workspace.addTrack(trackA);
        Result<?> missingTrack = workspace.removeTrack(trackB);

        assertInstanceOf(Failure.class, duplicateAdd);
        assertInstanceOf(Failure.class, missingTrack);
    }

    @Test
    public void nullTrackArgumentsThrowProgrammingErrorNotResultFailure() {
        assertThrows(NullPointerException.class, () -> workspace.addTrack(null));
        assertThrows(NullPointerException.class, () -> workspace.removeTrack(null));
        assertThrows(NullPointerException.class, () -> workspace.setActiveTrack(null));
        assertThrows(NullPointerException.class, () -> workspace.isTrackVisible(null));
        assertThrows(NullPointerException.class, () -> workspace.setTrackVisible(null, true));
        assertThrows(NullPointerException.class, () -> workspace.renameTrack(null, "Name"));
        assertThrows(NullPointerException.class, () -> workspace.renameTrack(trackA, null));
        assertThrows(NullPointerException.class, () -> workspace.moveTrack(null, 0));
    }

    // --- rename track tests ---
    @Test
    public void renamingTrackUpdatesItsName() {
        workspace.addTrack(trackA);
        Result<Unit> renameResult = workspace.renameTrack(trackA, "NewName");
        assertInstanceOf(Success.class, renameResult);
        assertEquals("NewName", trackA.getName());
    }

    @Test
    public void renamingNonexistentTrackReturnsFailure() {
        workspace.addTrack(trackB);
        workspace.removeTrack(trackB);
        Result<Unit> renameResult = workspace.renameTrack(trackB, "NewName");
        assertInstanceOf(Failure.class, renameResult);
        assertFailure(renameResult, WorkspaceErrors.TRACK_NOT_FOUND);
    }

    // --- move track tests ---
    @Test
    public void movingTrackUpdatesItsPosition() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.addTrack(trackC);

        Result<Unit> moveResult = workspace.moveTrack(trackC, 0);
        assertInstanceOf(Success.class, moveResult);
        assertEquals(List.of(trackC, trackA, trackB), workspace.getTracks());
    }

    @Test
    public void movingNonexistentTrackReturnsFailure() {
        Result<Unit> moveResult = workspace.moveTrack(trackC, 0);
        assertInstanceOf(Failure.class, moveResult);
        assertFailure(moveResult, WorkspaceErrors.TRACK_NOT_FOUND);
    }

    @Test
    public void movingTrackToInvalidIndexReturnsFailure() {
        workspace.addTrack(trackA);
        Result<Unit> moveResult = workspace.moveTrack(trackA, -1);
        assertInstanceOf(Failure.class, moveResult);
        assertFailure(moveResult, WorkspaceErrors.INVALID_TRACK_INDEX);

        moveResult = workspace.moveTrack(trackA, 10);
        assertInstanceOf(Failure.class, moveResult);
        assertFailure(moveResult, WorkspaceErrors.INVALID_TRACK_INDEX);
    }

    @Test
    public void movingTrackRetainsContext() {
        workspace.addTrack(trackA);
        workspace.addTrack(trackB);
        workspace.addTrack(trackC);

        workspace.setActiveTrack(trackA);

        TrackEditorModel editorA = workspace.getActiveTrackEditor().orElseThrow();

        editorA.createNote(60, 0.0, 1.0);

        workspace.moveTrack(trackA, 2);

        assertEquals(List.of(trackB, trackC, trackA), workspace.getTracks());
        assertSame(trackA, workspace.getActiveTrack().orElseThrow());
        assertSame(editorA, workspace.getActiveTrackEditor().orElseThrow());
        assertTrue(editorA.canUndo());
    }
}
