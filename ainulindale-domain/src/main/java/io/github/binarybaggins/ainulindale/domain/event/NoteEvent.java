package io.github.binarybaggins.ainulindale.domain.event;

import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.Objects;

/**
 * Represents a musical note event in the score.
 */
public final class NoteEvent implements VoiceEvent {

    private final EventId id;
    private final ScoreRange range;
    private final Pitch pitch;

    private NoteEvent(EventId id, ScoreRange range, Pitch pitch) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.range = Objects.requireNonNull(range, "range must not be null");
        this.pitch = Objects.requireNonNull(pitch, "pitch must not be null");

        if (range.duration().value().signum() <= 0) {
            throw new IllegalArgumentException("range must have a positive duration");
        }
    }

    /**
     * Creates a new NoteEvent with the specified score range and pitch.
     *
     * @param range the score range of the note
     * @param pitch the pitch of the note
     * @return a new note event with a newly generated identity
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if the range has a non-positive duration
     */
    public static NoteEvent create(ScoreRange range, Pitch pitch) {
        return new NoteEvent(EventId.create(), range, pitch);
    }

    @Override
    public EventId id() {
        return id;
    }

    public ScoreRange range() {
        return range;
    }

    public Pitch pitch() {
        return pitch;
    }

    @Override
    public ScorePosition position() {
        return range.start();
    }
}
