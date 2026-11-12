
package io.github.binarybaggins.ainulindale.domain.relation;

import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.RelationId;
import java.util.Objects;

/**
 * Represents a slur between two notes, identified by their event IDs.
 * Ensures that the start and end notes are different.
 */
public final class Slur {

    private final RelationId id;
    private final EventId startNoteId;
    private final EventId endNoteId;

    private Slur(RelationId id, EventId startNoteId, EventId endNoteId) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(startNoteId, "startNoteId must not be null");
        Objects.requireNonNull(endNoteId, "endNoteId must not be null");

        if (startNoteId.equals(endNoteId)) {
            throw new IllegalArgumentException("startNoteId and endNoteId must be different");
        }

        this.id = id;
        this.startNoteId = startNoteId;
        this.endNoteId = endNoteId;
    }

    /**
     * Creates a new Slur with the given start and end note IDs.
     *
     * @param startNoteId the ID of the start note
     * @param endNoteId the ID of the end note
     * @return a new Slur instance
     * @throws NullPointerException if either argument is null
     * @throws IllegalArgumentException if startNoteId and endNoteId are the same
     */
    public static Slur create(EventId startNoteId, EventId endNoteId) {
        // A slur needs spite and something it's directed at. "Get out of the way, Pottha" - Draconia Malfoid
        return new Slur(RelationId.create(), startNoteId, endNoteId);
    }

    /**
     * Returns the ID of this slur.
     *
     * @return the relation ID
     */
    public RelationId id() {
        return id;
    }

    /**
     * Returns the ID of the start note of this slur.
     *
     * @return the start note ID
     */
    public EventId startNoteId() {
        return startNoteId;
    }

    /**
     * Returns the ID of the end note of this slur.
     *
     * @return the end note ID
     */
    public EventId endNoteId() {
        return endNoteId;
    }
}
