package io.github.binarybaggins.ainulindale.workspace;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Result<Unit> addTrack(EditorTrack track) {
        Objects.requireNonNull(track);

        if (containsTrack(track)) {
            return Result.failure(WorkspaceErrors.TRACK_ALREADY_PRESENT);
        }

        trackEntries.add(new TrackEntry(track, new TrackEditorModel(track), true));

        return Result.success(Unit.INSTANCE);
    }

    public List<EditorTrack> getTracks() {
        return trackEntries
            .stream()
            .map(entry -> entry.track)
            .toList();
    }

    private boolean containsTrack(EditorTrack track) {
        return trackEntries.stream().anyMatch(entry -> entry.track == track);
    }
}
