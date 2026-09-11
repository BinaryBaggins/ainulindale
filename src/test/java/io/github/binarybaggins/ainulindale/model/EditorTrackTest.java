package io.github.binarybaggins.ainulindale.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class EditorTrackTest {

    @Test
    void constructorCopiesInitialNoteList() {
        EditorNote note = new EditorNote(60, 0.0, 1.0);
        List<EditorNote> initialNotes = new ArrayList<>();
        initialNotes.add(note);

        EditorTrack track = new EditorTrack("Test", initialNotes);

        initialNotes.clear();

        assertEquals(1, track.size());
        assertSame(note, track.getNote(0));
    }

    @Test
    void notesViewCannotBeStructurallyModified() {
        EditorTrack track = new EditorTrack("Test");

        assertThrows(
                UnsupportedOperationException.class,
                () -> track.getNotes().add(new EditorNote(60, 0.0, 1.0)));
    }

    @Test
    void noteLookupUsesIdentity() {
        EditorNote note = new EditorNote(60, 0.0, 1.0);
        EditorTrack track = new EditorTrack("Test", List.of(note));

        assertTrue(track.containsNote(note));
        assertEquals(0, track.indexOfNote(note));

        EditorNote differentInstance = new EditorNote(60, 0.0, 1.0);

        assertFalse(track.containsNote(differentInstance));
        assertEquals(-1, track.indexOfNote(differentInstance));
    }

}
