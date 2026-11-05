package io.github.binarybaggins.ainulindale.domain.instrument;

import io.github.binarybaggins.ainulindale.domain.identity.InstrumentDefinitionId;
import java.util.Objects;

/**
 * Represents an assignment of an instrument definition.
 */
public record InstrumentAssignment(InstrumentDefinitionId instrumentDefinitionId) {
    /**
     * Constructs a new InstrumentAssignment with the given instrumentDefinitionId.
     *
     * @param instrumentDefinitionId the ID of the instrument definition
     * @throws NullPointerException if instrumentDefinitionId is null
     */
    public InstrumentAssignment {
        Objects.requireNonNull(instrumentDefinitionId, "instrumentDefinitionId must not be null");
    }

    /**
     * Creates a new InstrumentAssignment with the given instrumentDefinitionId.
     *
     * @param instrumentDefinitionId the ID of the instrument definition
     * @return a new InstrumentAssignment instance
     * @throws NullPointerException if instrumentDefinitionId is null
     */
    public static InstrumentAssignment of(InstrumentDefinitionId instrumentDefinitionId) {
        return new InstrumentAssignment(instrumentDefinitionId);
    }
}
