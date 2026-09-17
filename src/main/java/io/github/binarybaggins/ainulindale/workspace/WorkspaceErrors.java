package io.github.binarybaggins.ainulindale.workspace;

import io.github.binarybaggins.ainulindale.core.result.ResultError;

public class WorkspaceErrors {

    private WorkspaceErrors() {}

    public static final ResultError TRACK_ALREADY_PRESENT = new ResultError("Workspace.TrackAlreadyPresent");

    public static final ResultError TRACK_NOT_FOUND = new ResultError("Workspace.TrackNotFound");

    public static final ResultError TRACK_NOT_VISIBLE = new ResultError("Workspace.TrackNotVisible");

    public static final ResultError ACTIVE_TRACK_CANNOT_BE_HIDDEN = new ResultError(
        "Workspace.ActiveTrackCannotBeHidden"
    );

    public static final ResultError ACTIVE_TRACK_CANNOT_BE_REMOVED = new ResultError(
        "Workspace.ActiveTrackCannotBeRemoved"
    );

    public static final ResultError INVALID_TRACK_INDEX = new ResultError("Workspace.InvalidTrackIndex");
}
