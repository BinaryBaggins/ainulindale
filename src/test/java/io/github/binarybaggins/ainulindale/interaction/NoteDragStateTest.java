package io.github.binarybaggins.ainulindale.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.interaction.DragMode;
import io.github.binarybaggins.ainulindale.model.EditorNote;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.NoteSnapshot;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NoteDragStateTest {

    @Test
    void capturesAllSelectedNotesAsSnapshots() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);
        EditorNote b = new EditorNote(64, 3.0, 1.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a, b), DragMode.MOVE, 1.0, 5);

        assertEquals(Set.of(a, b), state.notes());
    }

    @Test
    void laterChangesToNoteDoNotAffectSnapshot() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);
        EditorTrack track = new EditorTrack("Test Track", List.of(a));
        TrackEditorModel model = new TrackEditorModel(track);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.MOVE, 1.0, 5);

        model.moveNotes(List.of(a), -59, 98.0);
        model.resizeNotesRight(List.of(a), 48.0);

        NoteSnapshot snapshot = state.grabbedStartState();

        assertEquals(60, snapshot.midiNote());
        assertEquals(1.0, snapshot.startBeat(), 0.000001);
        assertEquals(2.0, snapshot.durationBeats(), 0.000001);
    }

    @Test
    void preservesOrderOfNotesAndSnapshots() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);
        EditorNote b = new EditorNote(64, 3.0, 1.0);
        EditorNote c = new EditorNote(62, 5.0, 1.0);

        NoteDragState state = NoteDragState.capture(b, List.of(a, b, c), DragMode.MOVE, 3.0, 5);

        assertIterableEquals(List.of(a, b, c), state.notes());
        assertIterableEquals(List.of(new NoteSnapshot(a), new NoteSnapshot(b), new NoteSnapshot(c)), state.snapshots());
    }

    @Test
    void grabbedStartBeatAndEndBeatAreCorrect() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.MOVE, 1.0, 5);

        assertEquals(1.0, state.grabbedStartBeat(), 0.000001);
        assertEquals(3.0, state.grabbedStartEndBeat(), 0.000001);
    }

    @Test
    void pointerOffsetForMoveIsRelativeToStartBeat() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.MOVE, 1.5, 5);

        assertEquals(0.5, state.pointerOffsetBeats(), 0.000001);
    }

    @Test
    void pointerOffsetForResizeLeftIsRelativeToStartBeat() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.RESIZE_LEFT, 1.25, 5);

        assertEquals(0.25, state.pointerOffsetBeats(), 0.000001);
    }

    @Test
    void pointerOffsetForResizeRightIsRelativeToStartEndBeat() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.RESIZE_RIGHT, 3.5, 5);

        assertEquals(0.5, state.pointerOffsetBeats(), 0.000001);
    }

    @Test
    void rejectsGrabbedNoteOutsideGroup() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);
        EditorNote outsider = new EditorNote(61, 2.0, 1.0);

        assertThrows(IllegalArgumentException.class, () ->
            NoteDragState.capture(outsider, List.of(a), DragMode.MOVE, 1.0, 5)
        );
    }

    @Test
    void rejectsNoneDragMode() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        assertThrows(IllegalArgumentException.class, () -> NoteDragState.capture(a, List.of(a), DragMode.NONE, 1.0, 5));
    }

    @Test
    void exposedCollectionsAreImmutable() {
        EditorNote a = new EditorNote(60, 1.0, 2.0);

        NoteDragState state = NoteDragState.capture(a, List.of(a), DragMode.MOVE, 1.0, 5);

        assertThrows(UnsupportedOperationException.class, () -> state.notes().add(new EditorNote(1, 0, 1)));
        assertThrows(UnsupportedOperationException.class, () -> state.snapshots().add(new NoteSnapshot(1, 0, 1)));
        assertThrows(UnsupportedOperationException.class, () ->
            state.startStates().put(new EditorNote(1, 0, 1), new NoteSnapshot(1, 0, 1))
        );
    }
}
