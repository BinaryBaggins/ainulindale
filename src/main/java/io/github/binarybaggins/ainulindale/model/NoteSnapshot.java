package io.github.binarybaggins.ainulindale.model;

public record NoteSnapshot(int midiNote, double startBeat, double durationBeats) {
    public NoteSnapshot(EditorNote note) {
        this(note.getMidiNote(), note.getStartBeat(), note.getDurationBeats());
    }
}