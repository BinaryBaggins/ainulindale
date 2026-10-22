package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.core.result.ResultError;

/**
 * Contains error definitions related to the Composition class.
 */
final class CompositionErrors {

    private CompositionErrors() {}

    /**
     * Error indicating that the specified part was not found within the composition.
     */
    static final ResultError PART_NOT_FOUND = new ResultError("Composition.PartNotFound");

    /**
     * Error indicating that the specified voice was not found within the composition.
     */
    static final ResultError VOICE_NOT_FOUND = new ResultError("Composition.VoiceNotFound");

    /**
     * Error indicating that the specified event was not found within the composition.
     */
    static final ResultError EVENT_NOT_FOUND = new ResultError("Composition.EventNotFound");

    /**
     * Error indicating that an attempt was made to remove the last remaining voice in a part.
     */
    static final ResultError CANNOT_REMOVE_LAST_VOICE = new ResultError("Composition.CannotRemoveLastVoice");

    /**
     * Error indicating that the specified part index is invalid within the composition.
     */
    static final ResultError INVALID_PART_INDEX = new ResultError("Composition.InvalidPartIndex");

    /**
     * Error indicating that the specified voice index is invalid within the composition.
     */
    static final ResultError INVALID_VOICE_INDEX = new ResultError("Composition.InvalidVoiceIndex");

    /**
     * Error indicating that the specified tempo change was not found within the composition.
     */
    static final ResultError TEMPO_CHANGE_NOT_FOUND = new ResultError("Composition.TempoChangeNotFound");
}
