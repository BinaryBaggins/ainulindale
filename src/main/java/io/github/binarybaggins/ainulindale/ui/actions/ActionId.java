package io.github.binarybaggins.ainulindale.ui.actions;

public enum ActionId {
    NOTE_DELETE("note.delete"),
    NOTE_UNDO("note.undo"),
    NOTE_REDO("note.redo"),

    TRACK_ADD("track.add"),
    TRACK_REMOVE("track.remove"),
    TRACK_RENAME("track.rename"),
    TRACK_MOVE_UP("track.move-up"),
    TRACK_MOVE_DOWN("track.move-down");

    private final String persistentId;

    ActionId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String persistentId() {
        return persistentId;
    }
}
