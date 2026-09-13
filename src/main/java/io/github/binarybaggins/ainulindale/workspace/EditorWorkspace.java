package io.github.binarybaggins.ainulindale.workspace;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class EditorWorkspace {

    private static final class TrackEntry {

        private final EditorTrack track;
        private final TrackEditorModel editorModel;
        private boolean visible;

        private TrackEntry(EditorTrack track, TrackEditorModel editorModel, boolean visible) {
            this.track = track;
            this.editorModel = editorModel;
            this.visible = visible;
        }
    }

    private final List<TrackEntry> trackEntries = new ArrayList<>();
    private TrackEntry activeTrackEntry;

    public EditorWorkspace() {}

    /**
     * Adds a track to the workspace.
     *
     * @param track the track to add
     * @return a Result indicating success or failure
     */
    public Result<Unit> addTrack(EditorTrack track) {
        Objects.requireNonNull(track);

        if (containsTrack(track)) {
            return Result.failure(WorkspaceErrors.TRACK_ALREADY_PRESENT);
        }

        trackEntries.add(new TrackEntry(track, new TrackEditorModel(track), true));

        return Result.success(Unit.INSTANCE);
    }

    /**
     * Returns a list of all tracks in the workspace.
     *
     * @return a list of all tracks
     */
    public List<EditorTrack> getTracks() {
        return trackEntries
            .stream()
            .map(entry -> entry.track)
            .toList();
    }

    /**
     * Checks if the workspace contains the specified track.
     *
     * @param track the track to check for
     * @return true if the track is present, false otherwise
     */
    private boolean containsTrack(EditorTrack track) {
        return trackEntries.stream().anyMatch(entry -> entry.track == track);
    }

    /**
     * Returns the active track in the workspace, if any.
     *
     * @return an Optional containing the active track, or empty if no track is active
     */
    public Optional<EditorTrack> getActiveTrack() {
        return activeTrackEntry != null ? Optional.of(activeTrackEntry.track) : Optional.empty();
    }

    /**
     * Returns the editor model of the active track, if any.
     *
     * @return an Optional containing the active track's editor model, or empty if no track is active
     */
    public Optional<TrackEditorModel> getActiveTrackEditor() {
        return activeTrackEntry != null ? Optional.of(activeTrackEntry.editorModel) : Optional.empty();
    }

    /**
     * Clears the active track in the workspace.
     */
    public void clearActiveTrack() {
        activeTrackEntry = null;
    }

    /**
     * Sets the active track in the workspace.
     *
     * @param track the track to set as active
     * @return a Result indicating success or failure
     */
    public Result<Unit> setActiveTrack(EditorTrack track) {
        Objects.requireNonNull(track);
        for (TrackEntry entry : trackEntries) {
            if (entry.track == track) {
                if (!entry.visible) {
                    return Result.failure(WorkspaceErrors.TRACK_NOT_VISIBLE);
                }
                activeTrackEntry = entry;
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
    }
}
