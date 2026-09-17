package io.github.binarybaggins.ainulindale.ui.actions;

/**
 * Stable identifiers for application actions.
 *
 * <p>Each persistent ID is used as part of persisted settings keys. Once an
 * ID has been released, it must not be changed; renaming an enum constant does
 * not change its persistent ID.</p>
 */
public enum ActionId {
    NOTE_DELETE("note.delete"),
    NOTE_UNDO("note.undo"),
    NOTE_REDO("note.redo"),

    TRACK_ADD("track.add"),
    TRACK_REMOVE("track.remove"),
    TRACK_RENAME("track.rename"),
    TRACK_ACTIVATE("track.activate"),
    TRACK_MOVE_UP("track.move-up"),
    TRACK_MOVE_DOWN("track.move-down");

    private final String persistentId;

    ActionId(String persistentId) {
        this.persistentId = persistentId;
    }

    /**
     * Returns the stable identifier used when action-related settings are persisted.
     *
     * @return the persistent action identifier
     */
    public String persistentId() {
        return persistentId;
    }
}
