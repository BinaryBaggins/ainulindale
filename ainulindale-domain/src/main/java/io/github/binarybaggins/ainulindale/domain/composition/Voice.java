package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.event.NoteEvent;
import io.github.binarybaggins.ainulindale.domain.event.VoiceEvent;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a voice in the composition domain.
 * A voice has a unique identifier and a collection of associated events.
 */
public final class Voice {

    private final VoiceId id;
    private final Map<EventId, VoiceEvent> events;

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
}
