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
}
