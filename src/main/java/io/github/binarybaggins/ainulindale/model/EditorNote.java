package io.github.binarybaggins.ainulindale.model;

public class EditorNote {

    private int midiNote;
    private double startBeat;
    private double durationBeats;

    public EditorNote(int midiNote, double startBeat, double durationBeats) {
        this.midiNote = midiNote;
        this.startBeat = startBeat;
        this.durationBeats = durationBeats;
    }

    public int getMidiNote() {
        return midiNote;
    }

    public double getStartBeat() {
        return startBeat;
    }

    public double getDurationBeats() {
        return durationBeats;
    }

    void setState(int midiNote, double startBeat, double durationBeats) {
        this.midiNote = midiNote;
        this.startBeat = startBeat;
        this.durationBeats = durationBeats;
    }
}
