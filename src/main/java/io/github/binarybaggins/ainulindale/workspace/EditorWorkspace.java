package io.github.binarybaggins.ainulindale.workspace;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Owns the editor workspace session state.
 *
 * <p>The workspace manages track membership, visibility, the active track,
 * and the persistent {@link TrackEditorModel} associated with each track
 * while that track belongs to the workspace.
 *
 * <p>Only the active track's editor model is exposed for editing.
 * The workspace intentionally does not provide arbitrary access to editor
 * models for inactive tracks.
 *
 * <p>UI and view state such as zoom, scrolling, selection presentation,
 * layout, and theme settings do not belong to the workspace.
 */
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
     * Renames the specified track within the workspace.
     * @param track the track to rename
     * @param newName the new name for the track
     * @return a Result indicating success or failure
     */
    public Result<Unit> renameTrack(EditorTrack track, String newName) {
        Objects.requireNonNull(track);
        Objects.requireNonNull(newName);

        if (!containsTrack(track)) {
            return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
        }

        track.rename(newName);
        return Result.success(Unit.INSTANCE);
    }

    /**
     * Moves a track to a new position within the workspace.
     *
     * @param track the track to move
     * @param newIndex the new index for the track
     * @return a Result indicating success or failure
     */
    public Result<Unit> moveTrack(EditorTrack track, int newIndex) {
        Objects.requireNonNull(track);

        int currentIndex = -1;
        for (int i = 0; i < trackEntries.size(); i++) {
            if (trackEntries.get(i).track == track) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
        }

        if (newIndex < 0 || newIndex >= trackEntries.size()) {
            return Result.failure(WorkspaceErrors.INVALID_TRACK_INDEX);
        }

        TrackEntry entry = trackEntries.remove(currentIndex);
        trackEntries.add(newIndex, entry);
        return Result.success(Unit.INSTANCE);
    }

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
     * Removes a track from the workspace.
     *
     * @param track the track to remove
     * @return a Result indicating success or failure
     */
    public Result<Unit> removeTrack(EditorTrack track) {
        Objects.requireNonNull(track);
        for (int i = 0; i < trackEntries.size(); i++) {
            TrackEntry entry = trackEntries.get(i);
            if (entry.track == track) {
                if (entry == activeTrackEntry) {
                    return Result.failure(WorkspaceErrors.ACTIVE_TRACK_CANNOT_BE_REMOVED);
                }
                trackEntries.remove(i);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
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
     * Returns the persistent editor model of the active track, if any.
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

    /**
     * Checks if the specified track is visible in the workspace.
     *
     * @param track the track to check for visibility
     * @return a Result containing true if the track is visible, false if not, or a failure if the track is not found
     */
    public Result<Boolean> isTrackVisible(EditorTrack track) {
        Objects.requireNonNull(track);
        for (TrackEntry entry : trackEntries) {
            if (entry.track == track) {
                return Result.success(entry.visible);
            }
        }
        return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
    }

    /**
     * Returns a list of all visible tracks in the workspace.
     *
     * @return a list of visible tracks
     */
    public List<EditorTrack> getVisibleTracks() {
        return trackEntries
            .stream()
            .filter(entry -> entry.visible)
            .map(entry -> entry.track)
            .toList();
    }

    /**
     * Sets the visibility of the specified track in the workspace.
     *
     * @param track the track to set visibility for
     * @param visible true to make the track visible, false to hide it
     * @return a Result indicating success or failure
     */
    public Result<Unit> setTrackVisible(EditorTrack track, boolean visible) {
        Objects.requireNonNull(track);
        for (TrackEntry entry : trackEntries) {
            if (entry.track == track) {
                if (!visible && entry == activeTrackEntry) {
                    return Result.failure(WorkspaceErrors.ACTIVE_TRACK_CANNOT_BE_HIDDEN);
                }
                entry.visible = visible;
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(WorkspaceErrors.TRACK_NOT_FOUND);
    }
}
