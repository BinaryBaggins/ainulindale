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
     * Error indicating that the specified note was not found within the composition.
     */
    static final ResultError NOTE_NOT_FOUND = new ResultError("Composition.NoteNotFound");

    /**
     * Error indicating that the specified tie was not found within the composition.
     */
    static final ResultError TIE_NOT_FOUND = new ResultError("Composition.TieNotFound");

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
     * Error indicating that the specified tempo state transition was not found within the composition.
     */
    static final ResultError TEMPO_CHANGE_NOT_FOUND = new ResultError("Composition.TempoChangeNotFound");

    /**
     * Error indicating that the specified meter state transition was not found within the composition.
     */
    static final ResultError METER_CHANGE_NOT_FOUND = new ResultError("Composition.MeterChangeNotFound");

    /**
     * Error indicating that the specified key signature state transition was not found within the composition.
     */
    static final ResultError KEY_SIGNATURE_CHANGE_NOT_FOUND = new ResultError("Composition.KeySignatureChangeNotFound");

    /**
     * Error indicating that the specified instrument assignment state transition was not found within the composition.
     */
    static final ResultError INSTRUMENT_ASSIGNMENT_CHANGE_NOT_FOUND = new ResultError(
        "Composition.InstrumentAssignmentChangeNotFound"
    );

    /**
     * Error indicating that the specified tie notes are identical.
     */
    static final ResultError TIE_NOTES_IDENTICAL = new ResultError("Composition.TieNotesIdentical");

    /**
     * Error indicating that the specified tie notes have a pitch mismatch.
     */
    static final ResultError TIE_NOTES_PITCH_MISMATCH = new ResultError("Composition.TieNotesPitchMismatch");

    /**
     * Error indicating that the specified tie notes are in different voices.
     */
    static final ResultError TIE_NOTES_IN_DIFFERENT_VOICES = new ResultError("Composition.TieNotesInDifferentVoices");

    /**
     * Error indicating that the specified tie notes are not adjacent.
     */
    static final ResultError TIE_NOTES_NOT_ADJACENT = new ResultError("Composition.TieNotesNotAdjacent");

    /**
     * Error indicating that the source note is already a source in another tie.
     */
    static final ResultError TIE_SOURCE_ALREADY_A_SOURCE = new ResultError("Composition.TieSourceAlreadyASource");

    /**
     * Error indicating that the target note is already a target in another tie.
     */
    static final ResultError TIE_TARGET_ALREADY_A_TARGET = new ResultError("Composition.TieTargetAlreadyATarget");

    /**
     * Error indicating that a note participating in a tie cannot be removed from the composition.
     */
    static final ResultError CANNOT_REMOVE_NOTE_PARTICIPATING_IN_TIE = new ResultError(
        "Composition.CannotRemoveNoteParticipatingInTie"
    );

    /**
     * Error indicating that a voice containing tied notes cannot be removed from the composition.
     */
    static final ResultError CANNOT_REMOVE_VOICE_CONTAINING_TIED_NOTES = new ResultError(
        "Composition.CannotRemoveVoiceContainingTiedNotes"
    );

    /**
     * Error indicating that a part containing tied notes cannot be removed from the composition.
     */
    static final ResultError CANNOT_REMOVE_PART_CONTAINING_TIED_NOTES = new ResultError(
        "Composition.CannotRemovePartContainingTiedNotes"
    );
}
