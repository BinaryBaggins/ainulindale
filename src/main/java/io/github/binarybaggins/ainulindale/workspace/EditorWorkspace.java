package io.github.binarybaggins.ainulindale.workspace;

import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;
import java.util.ArrayList;
import java.util.List;

public final class EditorWorkspace {

    private static class TrackEntry {

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

}
