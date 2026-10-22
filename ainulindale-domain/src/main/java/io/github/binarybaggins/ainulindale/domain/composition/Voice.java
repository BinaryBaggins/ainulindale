package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.event.NoteEvent;
import io.github.binarybaggins.ainulindale.domain.event.VoiceEvent;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a voice in the composition domain.
 * A voice has a unique identifier and a collection of associated events.
 */
public final class Voice {

    private final VoiceId id;
    private final Map<EventId, VoiceEvent> events;
    private final StateTimeline<Meter> meterTimeline = new StateTimeline<>();
    private final StateTimeline<KeySignature> keySignatureTimeline = new StateTimeline<>();

    private Voice(VoiceId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.events = new HashMap<>();
    }

    /**
     * Creates a new voice with a unique identifier.
     *
     * @return a new voice instance
     */
    static Voice create() {
        return new Voice(VoiceId.create());
    }

    /**
     * Returns the unique identifier of this voice.
     *
     * @return the voice ID
     */
    public VoiceId id() {
        return id;
    }

    /**
     * Returns an unmodifiable collection of all events associated with this voice.
     *
     * @return an unmodifiable collection of voice events
     */
    public Collection<VoiceEvent> events() {
        return Collections.unmodifiableCollection(events.values());
    }

    /**
     * Adds a new note to this voice.
     *
     * @param range the score range of the note
     * @param pitch the pitch of the note
     * @return the unique identifier of the newly added note
     * @throws NullPointerException if the range or pitch is null
     */
    EventId addNote(ScoreRange range, Pitch pitch) {
        Objects.requireNonNull(range, "range must not be null");
        Objects.requireNonNull(pitch, "pitch must not be null");
        NoteEvent noteEvent = NoteEvent.create(range, pitch);
        events.put(noteEvent.id(), noteEvent);
        return noteEvent.id();
    }

    /**
     * Adds a new event to this voice.
     *
     * @param event the voice event to be added
     * @return true if the event was successfully added, false if an event with the same ID already exists
     * @throws NullPointerException if the event is null
     */
    boolean addEvent(VoiceEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        return events.putIfAbsent(event.id(), event) == null;
    }

    /**
     * Removes an event from this voice.
     *
     * @param eventId the unique identifier of the event to be removed
     * @return true if the event was successfully removed, false otherwise
     */
    boolean removeEvent(EventId eventId) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        return events.remove(eventId) != null;
    }

    /**
     * Finds a note event by its unique identifier in this voice.
     *
     * @param eventId the unique identifier of the note event
     * @return the note event if found, or null if not found
     * @throws NullPointerException if the eventId is null
     */
    NoteEvent findNote(EventId eventId) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        return events.get(eventId) instanceof NoteEvent note ? note : null;
    }

    /**
     * Changes the pitch of an existing note in this voice.
     *
     * @param eventId the unique identifier of the note event to be changed
     * @param newPitch the new pitch for the note
     * @return true if the pitch was successfully changed, false otherwise
     * @throws NullPointerException if the eventId or newPitch is null
     */
    boolean changeNotePitch(EventId eventId, Pitch newPitch) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(newPitch, "newPitch must not be null");
        VoiceEvent event = events.get(eventId);
        if (event instanceof NoteEvent noteEvent) {
            NoteEvent updatedNoteEvent = noteEvent.withPitch(newPitch);
            events.put(eventId, updatedNoteEvent);
            return true;
        }
        return false;
    }

    /**
     * Changes the score range of an existing note in this voice.
     *
     * @param eventId the unique identifier of the note event to be changed
     * @param newRange the new score range for the note
     * @return true if the score range was successfully changed, false otherwise
     * @throws NullPointerException if the eventId or newRange is null
     */
    boolean changeNoteRange(EventId eventId, ScoreRange newRange) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(newRange, "newRange must not be null");
        VoiceEvent event = events.get(eventId);
        if (event instanceof NoteEvent noteEvent) {
            NoteEvent updatedNoteEvent = noteEvent.withRange(newRange);
            events.put(eventId, updatedNoteEvent);
            return true;
        }
        return false;
    }

    /**
     * Sets the meter at the specified score position within this voice.
     *
     * @param position the score position at which to set the meter
     * @param meter the meter to establish at the specified position
     * @throws NullPointerException if position or meter is null
     */
    void setMeter(ScorePosition position, Meter meter) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(meter, "meter must not be null");

        meterTimeline.set(position, meter);
    }

    /**
     * Ends the meter at the specified score position within this voice.
     *
     * @param position the score position at which to end the meter
     * @throws NullPointerException if position is null
     */
    void endMeter(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        meterTimeline.end(position);
    }

    /**
     * Removes the meter change at the specified score position within this voice.
     *
     * @param position the score position at which to remove the meter change
     * @return true if a meter change was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean removeMeterChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return meterTimeline.remove(position);
    }

    /**
     * Retrieves the local meter at the specified score position within this voice.
     *
     * @param position the score position at which to retrieve the local meter
     * @return an Optional containing the local meter if present, or an empty Optional if not
     * @throws NullPointerException if position is null
     */
    Optional<Meter> localMeterAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return meterTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<Meter> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets the key signature at the specified score position within this voice.
     *
     * @param position the score position at which to set the key signature
     * @param keySignature the key signature to set
     * @throws NullPointerException if position or keySignature is null
     */
    void setKeySignature(ScorePosition position, KeySignature keySignature) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keySignature, "keySignature must not be null");

        keySignatureTimeline.set(position, keySignature);
    }

    /**
     * Ends the key signature at the specified score position within this voice.
     *
     * @param position the score position at which to end the key signature
     * @throws NullPointerException if position is null
     */
    void endKeySignature(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        keySignatureTimeline.end(position);
    }

    /**
     * Removes the key signature change at the specified score position within this voice.
     *
     * @param position the score position at which to remove the key signature change
     * @return true if a key signature change was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean removeKeySignatureChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return keySignatureTimeline.remove(position);
    }

    /**
     * Retrieves the local key signature at the specified score position within this voice.
     *
     * @param position the score position at which to retrieve the local key signature
     * @return an Optional containing the local key signature if present, or an empty Optional if not
     * @throws NullPointerException if position is null
     */
    Optional<KeySignature> localKeySignatureAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return keySignatureTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<KeySignature> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }
}
