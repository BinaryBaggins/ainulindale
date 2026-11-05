package io.github.binarybaggins.ainulindale.domain.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.identity.InstrumentDefinitionId;
import org.junit.jupiter.api.Test;

class InstrumentAssignmentTest {

    @Test
    void constructorPreservesInstrumentDefinitionId() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        InstrumentAssignment assignment = new InstrumentAssignment(id);

        assertEquals(id, assignment.instrumentDefinitionId());
    }

    @Test
    void factoryPreservesInstrumentDefinitionId() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        InstrumentAssignment assignment = InstrumentAssignment.of(id);

        assertEquals(id, assignment.instrumentDefinitionId());
    }

    @Test
    void assignmentsWithSameIdAreEqual() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        InstrumentAssignment first = new InstrumentAssignment(id);
        InstrumentAssignment second = new InstrumentAssignment(id);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void assignmentsWithDifferentIdsAreNotEqual() {
        InstrumentAssignment first = new InstrumentAssignment(InstrumentDefinitionId.create());
        InstrumentAssignment second = new InstrumentAssignment(InstrumentDefinitionId.create());

        assertNotEquals(first, second);
    }

    @Test
    void constructorRejectsNullId() {
        assertThrows(NullPointerException.class, () -> new InstrumentAssignment(null));
    }

    @Test
    void factoryRejectsNullId() {
        assertThrows(NullPointerException.class, () -> InstrumentAssignment.of(null));
    }
}
