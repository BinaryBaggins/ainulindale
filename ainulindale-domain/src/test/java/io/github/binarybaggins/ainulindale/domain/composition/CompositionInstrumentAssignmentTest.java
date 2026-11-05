package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.InstrumentDefinitionId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.instrument.InstrumentAssignment;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompositionInstrumentAssignmentTest {

    private static InstrumentAssignment assignment() {
        return new InstrumentAssignment(InstrumentDefinitionId.create());
    }

    @Test
    void newPartHasNoInstrumentAssignment() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        assertEquals(new Success<>(Optional.empty()), composition.instrumentAssignmentAt(partId, ScorePosition.ZERO));
    }

    @Test
    void assignmentAppliesFromGivenPositionOnwards() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment assignment = assignment();

        assertInstanceOf(
            Success.class,
            composition.setInstrumentAssignment(partId, ScorePosition.of(2, 1), assignment)
        );

        assertEquals(
            new Success<>(Optional.empty()),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(1, 1))
        );
        assertEquals(
            new Success<>(Optional.of(assignment)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(2, 1))
        );
        assertEquals(
            new Success<>(Optional.of(assignment)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(6, 1))
        );
    }

    @Test
    void laterAssignmentReplacesPreviousAssignment() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment first = assignment();
        InstrumentAssignment second = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, first);
        composition.setInstrumentAssignment(partId, ScorePosition.of(8, 1), second);

        assertEquals(
            new Success<>(Optional.of(first)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(7, 1))
        );
        assertEquals(
            new Success<>(Optional.of(second)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(8, 1))
        );
    }

    @Test
    void settingAssignmentAtSamePositionReplacesTransition() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment first = assignment();
        InstrumentAssignment second = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, first);
        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, second);

        assertEquals(
            new Success<>(Optional.of(second)),
            composition.instrumentAssignmentAt(partId, ScorePosition.ZERO)
        );
    }

    @Test
    void endingAssignmentMakesItUnassigned() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment assignment = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, assignment);

        assertInstanceOf(Success.class, composition.endInstrumentAssignment(partId, ScorePosition.of(8, 1)));

        assertEquals(
            new Success<>(Optional.of(assignment)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(7, 1))
        );
        assertEquals(
            new Success<>(Optional.empty()),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(8, 1))
        );
        assertEquals(
            new Success<>(Optional.empty()),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(12, 1))
        );
    }

    @Test
    void assignmentCanBeSetAgainAfterEnd() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment first = assignment();
        InstrumentAssignment second = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, first);
        composition.endInstrumentAssignment(partId, ScorePosition.of(8, 1));
        composition.setInstrumentAssignment(partId, ScorePosition.of(12, 1), second);

        assertEquals(
            new Success<>(Optional.empty()),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(10, 1))
        );
        assertEquals(
            new Success<>(Optional.of(second)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(12, 1))
        );
    }

    @Test
    void removingAssignmentChangeRestoresPreviousAssignment() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment first = assignment();
        InstrumentAssignment second = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, first);
        composition.setInstrumentAssignment(partId, ScorePosition.of(8, 1), second);

        assertInstanceOf(Success.class, composition.removeInstrumentAssignmentChange(partId, ScorePosition.of(8, 1)));

        assertEquals(
            new Success<>(Optional.of(first)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(10, 1))
        );
    }

    @Test
    void removingEndRestoresPreviousAssignment() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment assignment = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, assignment);
        composition.endInstrumentAssignment(partId, ScorePosition.of(8, 1));

        assertInstanceOf(Success.class, composition.removeInstrumentAssignmentChange(partId, ScorePosition.of(8, 1)));

        assertEquals(
            new Success<>(Optional.of(assignment)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(12, 1))
        );
    }

    @Test
    void removingFirstAssignmentRestoresUnassignedState() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, assignment());

        assertInstanceOf(Success.class, composition.removeInstrumentAssignmentChange(partId, ScorePosition.ZERO));

        assertEquals(new Success<>(Optional.empty()), composition.instrumentAssignmentAt(partId, ScorePosition.ZERO));
    }

    @Test
    void removingUnknownAssignmentChangeReturnsFailure() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment assignment = assignment();

        composition.setInstrumentAssignment(partId, ScorePosition.ZERO, assignment);

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            composition.removeInstrumentAssignmentChange(partId, ScorePosition.of(4, 1))
        );

        assertEquals(CompositionErrors.INSTRUMENT_ASSIGNMENT_CHANGE_NOT_FOUND, failure.error());
        assertEquals(
            new Success<>(Optional.of(assignment)),
            composition.instrumentAssignmentAt(partId, ScorePosition.of(4, 1))
        );
    }

    @Test
    void assignmentsAreIndependentBetweenParts() {
        Composition composition = Composition.create();
        PartId firstPart = composition.addPart();
        PartId secondPart = composition.addPart();
        InstrumentAssignment first = assignment();
        InstrumentAssignment second = assignment();

        composition.setInstrumentAssignment(firstPart, ScorePosition.ZERO, first);
        composition.setInstrumentAssignment(secondPart, ScorePosition.ZERO, second);
        composition.endInstrumentAssignment(firstPart, ScorePosition.of(4, 1));

        assertEquals(
            new Success<>(Optional.empty()),
            composition.instrumentAssignmentAt(firstPart, ScorePosition.of(5, 1))
        );
        assertEquals(
            new Success<>(Optional.of(second)),
            composition.instrumentAssignmentAt(secondPart, ScorePosition.of(5, 1))
        );
    }

    @Test
    void unknownPartReturnsFailureForAllOperations() {
        Composition composition = Composition.create();
        PartId unknown = PartId.create();

        Failure<?> setFailure = assertInstanceOf(
            Failure.class,
            composition.setInstrumentAssignment(unknown, ScorePosition.ZERO, assignment())
        );
        Failure<?> endFailure = assertInstanceOf(
            Failure.class,
            composition.endInstrumentAssignment(unknown, ScorePosition.ZERO)
        );
        Failure<?> removeFailure = assertInstanceOf(
            Failure.class,
            composition.removeInstrumentAssignmentChange(unknown, ScorePosition.ZERO)
        );
        Failure<?> queryFailure = assertInstanceOf(
            Failure.class,
            composition.instrumentAssignmentAt(unknown, ScorePosition.ZERO)
        );

        assertEquals(CompositionErrors.PART_NOT_FOUND, setFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, endFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, removeFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, queryFailure.error());
    }

    @Test
    void operationsRejectNullArguments() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        InstrumentAssignment assignment = assignment();

        assertThrows(NullPointerException.class, () ->
            composition.setInstrumentAssignment(null, ScorePosition.ZERO, assignment)
        );
        assertThrows(NullPointerException.class, () -> composition.setInstrumentAssignment(partId, null, assignment));
        assertThrows(NullPointerException.class, () ->
            composition.setInstrumentAssignment(partId, ScorePosition.ZERO, null)
        );

        assertThrows(NullPointerException.class, () -> composition.endInstrumentAssignment(null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.endInstrumentAssignment(partId, null));

        assertThrows(NullPointerException.class, () ->
            composition.removeInstrumentAssignmentChange(null, ScorePosition.ZERO)
        );
        assertThrows(NullPointerException.class, () -> composition.removeInstrumentAssignmentChange(partId, null));

        assertThrows(NullPointerException.class, () -> composition.instrumentAssignmentAt(null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.instrumentAssignmentAt(partId, null));
    }
}
