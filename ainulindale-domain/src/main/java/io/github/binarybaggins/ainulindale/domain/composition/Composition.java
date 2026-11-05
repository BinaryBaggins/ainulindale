package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.event.NoteEvent;
import io.github.binarybaggins.ainulindale.domain.identity.CompositionId;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.instrument.InstrumentAssignment;
import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.tempo.Tempo;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the musical aggregate root with a unique identifier and ordered parts.
 * Public mutations of parts, voices, notes, and musical state enter through this class.
 *
 * <p>State setters establish a value from the given position until the next transition,
 * replacing any transition at that exact position. End operations replace any transition
 * at their position and do not require a previously active value. Meter and key signature
 * inherit from composition to part to voice when no local value is active.
 * Tempo is composition-scoped; instrument assignments are part-local without inheritance.
 */
public final class Composition {

    private final CompositionId id;
    private final List<Part> parts;
    private final StateTimeline<Tempo> tempoTimeline = new StateTimeline<>();
    private final StateTimeline<Meter> meterTimeline = new StateTimeline<>();
    private final StateTimeline<KeySignature> keySignatureTimeline = new StateTimeline<>();

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
     * Returns an unmodifiable live view of the parts contained in this composition.
     *
     * @return an unmodifiable list of parts
     */
    public List<Part> parts() {
        return Collections.unmodifiableList(parts);
    }

    /**
     * Adds a new part with one initial voice to this composition and returns its unique identifier.
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
     * @throws NullPointerException if the partId is null
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
     * @throws NullPointerException if the voiceId, scoreRange, or pitch is null
     * @throws IllegalArgumentException if the voice is found and scoreRange has zero duration
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
     * @throws NullPointerException if the eventId is null
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

    /**
     * Removes the specified voice from this composition.
     *
     * @param voiceId the unique identifier of the voice to be removed
     * @return a result indicating success if the voice was removed, or an error if the voice was not found
     *         or if attempting to remove the last remaining voice in a part
     * @throws NullPointerException if the voiceId is null
     */
    public Result<Unit> removeVoice(VoiceId voiceId) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        for (Part part : parts) {
            if (part.containsVoice(voiceId)) {
                if (part.hasSingleVoice()) {
                    return Result.failure(CompositionErrors.CANNOT_REMOVE_LAST_VOICE);
                }

                part.removeVoice(voiceId);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Removes the specified part from this composition.
     *
     * @param partId the unique identifier of the part to be removed
     * @return a result indicating success if the part was removed, or an error if the part was not found
     * @throws NullPointerException if the partId is null
     */
    public Result<Unit> removePart(PartId partId) {
        Objects.requireNonNull(partId, "partId must not be null");
        if (parts.removeIf(part -> part.id().equals(partId))) {
            return Result.success(Unit.INSTANCE);
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Moves the specified part to a new index within this composition.
     *
     * @param partId the unique identifier of the part to be moved
     * @param newIndex the final zero-based index of the part after moving it
     * (must be between 0 and the number of parts minus one)
     * @return a result indicating success if the part was moved, or an error
     * if the part was not found or if the new index is invalid
     * @throws NullPointerException if the partId is null
     */
    public Result<Unit> movePart(PartId partId, int newIndex) {
        Objects.requireNonNull(partId, "partId must not be null");
        if (newIndex < 0 || newIndex >= parts.size()) {
            return Result.failure(CompositionErrors.INVALID_PART_INDEX);
        }
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).id().equals(partId)) {
                Part part = parts.remove(i);
                parts.add(newIndex, part);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Moves the specified voice to a new index within its part in this composition.
     *
     * @param voiceId the unique identifier of the voice to be moved
     * @param newIndex the final zero-based index of the voice after moving it
     * (must be between 0 and the number of voices in its part minus one)
     * @return a result indicating success if the voice was moved, or an error
     * if the voice was not found or if the new index is invalid
     * @throws NullPointerException if the voiceId is null
     */
    public Result<Unit> moveVoice(VoiceId voiceId, int newIndex) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        if (newIndex < 0) {
            return Result.failure(CompositionErrors.INVALID_VOICE_INDEX);
        }
        for (Part part : parts) {
            if (part.containsVoice(voiceId)) {
                if (newIndex >= part.voices().size()) {
                    return Result.failure(CompositionErrors.INVALID_VOICE_INDEX);
                }
                part.moveVoice(voiceId, newIndex);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Changes the pitch of an existing note in this composition.
     *
     * @param noteId the unique identifier of the note event to be changed
     * @param newPitch the new pitch for the note
     * @return a result indicating success if the pitch was successfully changed, or an error
     * if the note was not found
     * @throws NullPointerException if the noteId or newPitch is null
     */
    public Result<Unit> changeNotePitch(EventId noteId, Pitch newPitch) {
        Objects.requireNonNull(noteId, "noteId must not be null");
        Objects.requireNonNull(newPitch, "newPitch must not be null");
        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.changeNotePitch(noteId, newPitch)) {
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.EVENT_NOT_FOUND);
    }

    /**
     * Changes the score range of an existing note in this composition.
     *
     * @param noteId the unique identifier of the note event to be changed
     * @param newRange the new score range for the note
     * @return a result indicating success if the score range was successfully changed, or an error
     * if the note was not found
     * @throws NullPointerException if the noteId or newRange is null
     * @throws IllegalArgumentException if the note is found and newRange has zero duration
     */
    public Result<Unit> changeNoteRange(EventId noteId, ScoreRange newRange) {
        Objects.requireNonNull(noteId, "noteId must not be null");
        Objects.requireNonNull(newRange, "newRange must not be null");
        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.changeNoteRange(noteId, newRange)) {
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.EVENT_NOT_FOUND);
    }

    /**
     * Moves a note from its current voice to another voice within this composition.
     * Moving to the current voice succeeds without changing the note. Its identity, pitch, and range are preserved.
     *
     * @param noteId the unique identifier of the note event to be moved
     * @param targetVoiceId the unique identifier of the target voice
     * @return a result indicating success if the note was successfully moved, or an error
     * if the note or target voice was not found
     * @throws NullPointerException if the noteId or targetVoiceId is null
     * @throws IllegalStateException if a duplicate event ID is found in the target voice or
     * if the source note disappears during the transfer
     */
    public Result<Unit> moveNoteToVoice(EventId noteId, VoiceId targetVoiceId) {
        Objects.requireNonNull(noteId, "noteId must not be null");
        Objects.requireNonNull(targetVoiceId, "targetVoiceId must not be null");

        Voice sourceVoice = null;
        Voice targetVoice = null;
        NoteEvent note = null;

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(targetVoiceId)) {
                    targetVoice = voice;
                }

                if (note == null) {
                    NoteEvent candidate = voice.findNote(noteId);
                    if (candidate != null) {
                        sourceVoice = voice;
                        note = candidate;
                    }
                }
            }
        }

        if (note == null) {
            return Result.failure(CompositionErrors.EVENT_NOT_FOUND);
        }

        if (targetVoice == null) {
            return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
        }

        if (sourceVoice == targetVoice) {
            return Result.success(Unit.INSTANCE);
        }

        if (!targetVoice.addEvent(note)) {
            throw new IllegalStateException("Duplicate event ID in target voice: " + noteId);
        }

        if (!sourceVoice.removeEvent(noteId)) {
            targetVoice.removeEvent(noteId);
            throw new IllegalStateException("Source note disappeared during transfer: " + noteId);
        }

        return Result.success(Unit.INSTANCE);
    }

    /**
     * Establishes a tempo at the specified score position.
     * Replaces any existing tempo transition at that position.
     * @param position the score position at which to set the tempo
     * @param tempo the tempo to establish at the specified position
     * @throws NullPointerException if position or tempo is null
     */
    public void setTempo(ScorePosition position, Tempo tempo) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(tempo, "tempo must not be null");

        tempoTimeline.set(position, tempo);
    }

    /**
     * Explicitly ends the specified tempo state at a score position.
     * The tempo becomes unspecified until the next value on this timeline.
     * @param position the score position at which to end the tempo
     * @throws NullPointerException if position is null
     */
    public void endTempo(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        tempoTimeline.end(position);
    }

    /**
     * Removes the tempo state transition (Set or End) stored exactly at the given position.
     * Earlier transitions may become effective again.
     * @param position the score position of the tempo state transition to remove
     * @return a Result indicating successful removal, or failure if no transition exists at that position
     * @throws NullPointerException if position is null
     */
    public Result<Unit> removeTempoChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        if (!tempoTimeline.remove(position)) {
            return Result.failure(CompositionErrors.TEMPO_CHANGE_NOT_FOUND);
        }

        return Result.success(Unit.INSTANCE);
    }

    /**
     * Returns the effective tempo at the specified score position.
     * An empty result means that no quantitative tempo is specified.
     * @param position the score position at which to query the tempo
     * @return an Optional containing the effective tempo at the specified score position, or empty if no tempo is specified
     * @throws NullPointerException if position is null
     */
    public Optional<Tempo> tempoAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        return tempoTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<Tempo> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets the meter at the specified score position.
     * @param position the score position at which to set the meter
     * @param meter the meter to establish at the specified position
     * @throws NullPointerException if position or meter is null
     */
    public void setMeter(ScorePosition position, Meter meter) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(meter, "meter must not be null");

        meterTimeline.set(position, meter);
    }

    /**
     * Ends the meter state at the specified score position.
     * The meter becomes unspecified until the next value on this timeline.
     * @param position the score position at which to end the meter
     * @throws NullPointerException if position is null
     */
    public void endMeter(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        meterTimeline.end(position);
    }

    /**
     * Removes the meter state transition (Set or End) stored exactly at the specified score position.
     * Earlier transitions may become effective again.
     * @param position the score position at which to remove the meter state transition
     * @return a Result indicating successful removal, or failure if no transition exists at that position
     * @throws NullPointerException if position is null
     */
    public Result<Unit> removeMeterChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        if (!meterTimeline.remove(position)) {
            return Result.failure(CompositionErrors.METER_CHANGE_NOT_FOUND);
        }

        return Result.success(Unit.INSTANCE);
    }

    /**
     * Retrieves the meter effective at the specified score position.
     * @param position the score position at which to retrieve the meter
     * @return an Optional containing the composition-level meter, empty if no Set is active at that position
     * @throws NullPointerException if position is null
     */
    public Optional<Meter> meterAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        return meterTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<Meter> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets the local meter at the specified score position for the given part.
     * @param partId the unique identifier of the part
     * @param position the score position at which to set the meter
     * @param meter the meter to establish at the specified position
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId, position, or meter is null
     */
    public Result<Unit> setMeter(PartId partId, ScorePosition position, Meter meter) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(meter, "meter must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.setMeter(position, meter);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Ends the local meter state at the specified score position for the given part.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     * @param partId the unique identifier of the part
     * @param position the score position at which to end the meter
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> endMeter(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.endMeter(position);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Removes the local meter state transition (Set or End) stored exactly at the specified score position for the given part.
     * Earlier transitions may become effective again.
     * @param partId the unique identifier of the part
     * @param position the score position at which to remove the meter state transition
     * @return a Result indicating successful removal, or failure if the part was not found or no transition exists at that position
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> removeMeterChange(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                if (!part.removeMeterChange(position)) {
                    return Result.failure(CompositionErrors.METER_CHANGE_NOT_FOUND);
                }

                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Retrieves the effective meter at the specified score position for the given part.
     * If the part has a local meter at the position, it is returned; otherwise, the global meter is returned.
     * @param partId the unique identifier of the part
     * @param position the score position at which to retrieve the effective meter
     * @return a successful Result containing an Optional with the effective meter, empty if no scope specifies one;
     *         failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Optional<Meter>> meterAt(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                Optional<Meter> effectiveMeter = part.localMeterAt(position).or(() -> meterAt(position));

                return Result.success(effectiveMeter);
            }
        }

        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Sets the local meter at the specified score position for the given voice.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to set the meter
     * @param meter the meter to set
     * @return a Result indicating success, or failure if the voice was not found
     * @throws NullPointerException if voiceId, position, or meter is null
     */
    public Result<Unit> setMeter(VoiceId voiceId, ScorePosition position, Meter meter) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(meter, "meter must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    voice.setMeter(position, meter);
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Ends the local meter state at the specified score position for the given voice.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to end the meter
     * @return a Result indicating success, or failure if the voice was not found
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Unit> endMeter(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    voice.endMeter(position);
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Removes a local meter state transition (Set or End) stored exactly at the specified score position for the given voice.
     * Earlier transitions may become effective again.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to remove the local meter change
     * @return a Result indicating successful removal, or failure if the voice was not found or no transition exists at that position
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Unit> removeMeterChange(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    if (!voice.removeMeterChange(position)) {
                        return Result.failure(CompositionErrors.METER_CHANGE_NOT_FOUND);
                    }

                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Retrieves the effective meter at the specified score position for the given voice.
     * Resolution order: voice, part, composition.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to retrieve the effective meter
     * @return a successful Result containing an Optional with the effective meter, empty if no scope specifies one;
     *         failure if the voice was not found
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Optional<Meter>> meterAt(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    Optional<Meter> effectiveMeter = voice
                        .localMeterAt(position)
                        .or(() -> part.localMeterAt(position).or(() -> meterAt(position)));
                    return Result.success(effectiveMeter);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Sets a key signature at the specified score position.
     *
     * @param position the score position at which to set the key signature
     * @param keySignature the key signature to set
     * @throws NullPointerException if position or keySignature is null
     */
    public void setKeySignature(ScorePosition position, KeySignature keySignature) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keySignature, "keySignature must not be null");

        keySignatureTimeline.set(position, keySignature);
    }

    /**
     * Ends the key signature state at the specified score position.
     * The key signature becomes unspecified until the next value on this timeline.
     *
     * @param position the score position at which to end the key signature
     * @throws NullPointerException if position is null
     */
    public void endKeySignature(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        keySignatureTimeline.end(position);
    }

    /**
     * Removes the key signature state transition (Set or End) stored exactly at the specified score position.
     * Earlier transitions may become effective again.
     *
     * @param position the score position at which to remove the key signature state transition
     * @return a Result indicating successful removal, or failure if no transition exists at that position
     * @throws NullPointerException if position is null
     */
    public Result<Unit> removeKeySignatureChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        if (!keySignatureTimeline.remove(position)) {
            return Result.failure(CompositionErrors.KEY_SIGNATURE_CHANGE_NOT_FOUND);
        }

        return Result.success(Unit.INSTANCE);
    }

    /**
     * Retrieves the key signature effective at the specified score position.
     *
     * @param position the score position at which to retrieve the key signature
     * @return an Optional containing the composition-level key signature, empty if no Set is active at that position
     * @throws NullPointerException if position is null
     */
    public Optional<KeySignature> keySignatureAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");

        return keySignatureTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<KeySignature> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets a key signature at the specified score position for the given part.
     *
     * @param partId the ID of the part for which to set the key signature
     * @param position the score position at which to set the key signature
     * @param keySignature the key signature to set
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId, position or keySignature is null
     */
    public Result<Unit> setKeySignature(PartId partId, ScorePosition position, KeySignature keySignature) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keySignature, "keySignature must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.setKeySignature(position, keySignature);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Ends the key signature state at the specified score position for the given part.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     *
     * @param partId the ID of the part for which to end the key signature
     * @param position the score position at which to end the key signature
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> endKeySignature(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.endKeySignature(position);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Removes the key signature state transition (Set or End) stored exactly at the specified score position for the given part.
     * Earlier transitions may become effective again.
     *
     * @param partId the ID of the part for which to remove the key signature
     * @param position the score position at which to remove the key signature state transition
     * @return a Result indicating successful removal, or failure if the part was not found or no transition exists at that position
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> removeKeySignatureChange(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                if (!part.removeKeySignatureChange(position)) {
                    return Result.failure(CompositionErrors.KEY_SIGNATURE_CHANGE_NOT_FOUND);
                }
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Retrieves the key signature effective at the specified score position for the given part.
     * Resolution order: part, composition.
     *
     * @param partId the ID of the part for which to retrieve the key signature
     * @param position the score position at which to retrieve the key signature
     * @return a successful Result containing an Optional with the effective key signature, empty if no scope specifies one;
     *         failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Optional<KeySignature>> keySignatureAt(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                Optional<KeySignature> effective = part
                    .localKeySignatureAt(position)
                    .or(() -> keySignatureAt(position));

                return Result.success(effective);
            }
        }

        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Sets the local key signature at the specified score position for the given voice.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to set the key signature
     * @param keySignature the key signature to set
     * @return a Result indicating success, or failure if the voice was not found
     * @throws NullPointerException if voiceId, position, or keySignature is null
     */
    public Result<Unit> setKeySignature(VoiceId voiceId, ScorePosition position, KeySignature keySignature) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keySignature, "keySignature must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    voice.setKeySignature(position, keySignature);
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Ends the local key signature state at the specified score position for the given voice.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to end the key signature
     * @return a Result indicating success, or failure if the voice was not found
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Unit> endKeySignature(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    voice.endKeySignature(position);
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Removes a local key signature state transition (Set or End) at the specified score position for the given voice.
     * Removes only the transition at this exact position, potentially revealing an earlier state.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to remove the key signature state transition
     * @return a Result indicating successful removal, or failure if the voice was not found or no transition exists at that position
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Unit> removeKeySignatureChange(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    if (!voice.removeKeySignatureChange(position)) {
                        return Result.failure(CompositionErrors.KEY_SIGNATURE_CHANGE_NOT_FOUND);
                    }
                    return Result.success(Unit.INSTANCE);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Retrieves the effective key signature at the specified score position for the given voice.
     * Resolution order: voice, part, composition.
     *
     * @param voiceId the unique identifier of the voice
     * @param position the score position at which to retrieve the key signature
     * @return a successful Result containing an Optional with the effective key signature, empty if no scope specifies one;
     *         failure if the voice was not found
     * @throws NullPointerException if voiceId or position is null
     */
    public Result<Optional<KeySignature>> keySignatureAt(VoiceId voiceId, ScorePosition position) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            for (Voice voice : part.voices()) {
                if (voice.id().equals(voiceId)) {
                    Optional<KeySignature> effective = voice
                        .localKeySignatureAt(position)
                        .or(() -> part.localKeySignatureAt(position).or(() -> keySignatureAt(position)));
                    return Result.success(effective);
                }
            }
        }
        return Result.failure(CompositionErrors.VOICE_NOT_FOUND);
    }

    /**
     * Sets the instrument assignment for the specified part at the given score position.
     *
     * @param partId the unique identifier of the part
     * @param position the score position at which to set the instrument assignment
     * @param assignment the instrument assignment to set
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId, position, or assignment is null
     */
    public Result<Unit> setInstrumentAssignment(
        PartId partId,
        ScorePosition position,
        InstrumentAssignment assignment
    ) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(assignment, "assignment must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.setInstrumentAssignment(position, assignment);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Ends the instrument assignment for the specified part at the given score position.
     * The part remains unassigned until the next assignment; there is no inherited fallback.
     *
     * @param partId the unique identifier of the part
     * @param position the score position at which to end the instrument assignment
     * @return a Result indicating success, or failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> endInstrumentAssignment(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                part.endInstrumentAssignment(position);
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Removes the instrument assignment state transition (Set or End) for the specified part at the given score position.
     * Removes only the transition at this exact position, potentially revealing an earlier state.
     *
     * @param partId the unique identifier of the part
     * @param position the score position at which to remove the instrument assignment state transition
     * @return a Result indicating successful removal, or failure if the part was not found or no transition exists at that position
     * @throws NullPointerException if partId or position is null
     */
    public Result<Unit> removeInstrumentAssignmentChange(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                if (!part.removeInstrumentAssignmentChange(position)) {
                    return Result.failure(CompositionErrors.INSTRUMENT_ASSIGNMENT_CHANGE_NOT_FOUND);
                }
                return Result.success(Unit.INSTANCE);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }

    /**
     * Retrieves the instrument assignment for the specified part at the given score position.
     *
     * @param partId the unique identifier of the part
     * @param position the score position at which to retrieve the instrument assignment
     * @return a successful Result containing an Optional with the assignment, empty if the part is unassigned;
     *         failure if the part was not found
     * @throws NullPointerException if partId or position is null
     */
    public Result<Optional<InstrumentAssignment>> instrumentAssignmentAt(PartId partId, ScorePosition position) {
        Objects.requireNonNull(partId, "partId must not be null");
        Objects.requireNonNull(position, "position must not be null");

        for (Part part : parts) {
            if (part.id().equals(partId)) {
                Optional<InstrumentAssignment> assignment = part.instrumentAssignmentAt(position);
                return Result.success(assignment);
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }
}
