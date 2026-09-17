package io.github.binarybaggins.ainulindale.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EditorTrack {

    private String name;
    private final List<EditorNote> notes;

    public EditorTrack(String name) {
        this(name, List.of());
    }

    public EditorTrack(String name, List<EditorNote> initialNotes) {
        this.name = Objects.requireNonNull(name);
        this.notes = new ArrayList<>(Objects.requireNonNull(initialNotes));
    }

    public String getName() {
        return name;
    }

    // TODO: Track metadata mutations should go through EditorWorkspace.
    // Visibility can be restricted further once package boundaries are refactored.
    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName);
    }

    public List<EditorNote> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    public boolean containsNote(EditorNote note) {
        return notes.stream().anyMatch(existing -> existing == note);
    }

    public int indexOfNote(EditorNote note) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i) == note) {
                return i;
            }
        }

        return -1;
    }

    public EditorNote getNote(int index) {
        return notes.get(index);
    }

    public int size() {
        return notes.size();
    }

    void addNote(EditorNote note) {
        notes.add(note);
    }

    void addNote(int index, EditorNote note) {
        notes.add(index, note);
    }

    boolean removeNote(EditorNote note) {
        return notes.remove(note);
    }
}
