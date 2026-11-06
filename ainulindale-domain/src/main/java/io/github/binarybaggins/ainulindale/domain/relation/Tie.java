package io.github.binarybaggins.ainulindale.domain.relation;

import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.RelationId;
import java.util.Objects;

/**
 * Represents a tie between two notes, identified by their event IDs.
 * Ensures that the source and target notes are different.
 */
public final class Tie {

    private final RelationId id;
    private final EventId sourceNoteId;
    private final EventId targetNoteId;

    private Tie(RelationId id, EventId sourceNoteId, EventId targetNoteId) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sourceNoteId, "sourceNoteId must not be null");
        Objects.requireNonNull(targetNoteId, "targetNoteId must not be null");

        if (sourceNoteId.equals(targetNoteId)) {
            throw new IllegalArgumentException("sourceNoteId and targetNoteId must be different");
        }

        this.id = id;
        this.sourceNoteId = sourceNoteId;
        this.targetNoteId = targetNoteId;
    }

    /**
     * Creates a new Tie with the given source and target note IDs.
     *
     * @param sourceNoteId the ID of the source note
     * @param targetNoteId the ID of the target note
     * @return a new Tie instance
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if sourceNoteId and targetNoteId are the same
     */
    public static Tie create(EventId sourceNoteId, EventId targetNoteId) {
        return new Tie(RelationId.create(), sourceNoteId, targetNoteId);
    }

    /**
     * Returns the ID of this tie.
     *
     * @return the relation ID
     */
    public RelationId id() {
        return id;
    }

    /**
     * Returns the ID of the source note of this tie.
     *
     * @return the source note ID
     */
    public EventId sourceNoteId() {
        return sourceNoteId;
    }

    /**
     * Returns the ID of the target note of this tie.
     *
     * @return the target note ID
     */
    public EventId targetNoteId() {
        return targetNoteId;
    }
}
