package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.identity.CompositionId;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a composition which contains a unique identifier and a collection of parts.
 */
public final class Composition {

    private final CompositionId id;
    private final List<Part> parts;

    private Composition(CompositionId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.parts = new ArrayList<>();
    }

    /**
     * Creates a new composition with a unique identifier.
     *
     * @return a new composition instance
     */
    public static Composition create() {
        return new Composition(CompositionId.create());
    }

    /**
     * Returns the unique identifier of this composition.
     *
     * @return the composition ID
     */
    public CompositionId id() {
        return id;
    }

    /**
     * Returns an unmodifiable view of the parts contained in this composition.
     *
     * @return an unmodifiable list of parts
     */
    public List<Part> parts() {
        return Collections.unmodifiableList(parts);
    }

    /**
     * Adds a new part to this composition and returns its unique identifier.
     *
     * @return the unique identifier of the newly added part
     */
    public PartId addPart() {
        Part part = Part.create();
        parts.add(part);
        return part.id();
    }

    /**
     * Adds a new voice to the specified part within this composition.
     *
     * @param partId the unique identifier of the part to which the voice will be added
     * @return a result containing the unique identifier of the newly added voice if successful,
     *         or an error if the part was not found
     */
    public Result<VoiceId> addVoice(PartId partId) {
        Objects.requireNonNull(partId, "partId must not be null");
        for (Part part : parts) {
            if (part.id().equals(partId)) {
                return Result.success(part.addVoice());
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Adds a new note to the specified voice within this composition.
     *
     * @param voiceId the unique identifier of the voice to which the note will be added
     * @param scoreRange the score range of the note
     * @param pitch the pitch of the note
     * @return a result containing the unique identifier of the newly added note if successful,
     *         or an error if the voice was not found
     */
    public Result<EventId> addNote(VoiceId voiceId, ScoreRange scoreRange, Pitch pitch) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(scoreRange, "scoreRange must not be null");
        Objects.requireNonNull(pitch, "pitch must not be null");
        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    return Result.success(voice.addNote(scoreRange, pitch));
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Removes the specified event from this composition.
     *
     * @param eventId the unique identifier of the event to be removed
     * @return a result indicating success if the event was removed, or an error if the event was not found
     */
    public Result<Unit> removeEvent(EventId eventId) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.removeEvent(eventId)) {
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.EVENT_NOT_FOUND);
    }
}
