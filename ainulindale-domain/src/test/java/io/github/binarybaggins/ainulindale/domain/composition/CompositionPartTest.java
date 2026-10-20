package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import java.util.List;
import org.junit.jupiter.api.Test;

class CompositionPartTest {

    @Test
    void addPartAddsPartAndReturnsCreatedPartId() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        assertEquals(1, composition.parts().size());
        assertEquals(partId, composition.parts().getFirst().id());
    }

    @Test
    void addPartPreservesPartOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        assertEquals(firstPartId, composition.parts().get(0).id());
        assertEquals(secondPartId, composition.parts().get(1).id());
    }

    @Test
    void removePartRemovesSpecifiedPart() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        assertInstanceOf(Success.class, composition.removePart(partId));
        assertFalse(
            composition
                .parts()
                .stream()
                .anyMatch(part -> part.id().equals(partId))
        );
    }

    @Test
    void removePartPreservesRemainingPartOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        PartId thirdPartId = composition.addPart();
        assertInstanceOf(Success.class, composition.removePart(secondPartId));
        assertEquals(List.of(firstPartId, thirdPartId), composition.parts().stream().map(Part::id).toList());
    }

    @Test
    void removePartAllowsRemovingLastPart() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        assertInstanceOf(Success.class, composition.removePart(partId));
        assertTrue(composition.parts().isEmpty());
    }

    @Test
    void removePartReturnsFailureForUnknownPart() {
        Composition composition = Composition.create();
        PartId unknownPartId = PartId.create();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.removePart(unknownPartId));
        assertEquals(CompositionErrors.PART_NOT_FOUND, failure.error());
    }

    @Test
    void removePartRejectsNullPartId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.removePart(null));
    }

    @Test
    void movePartReturnsFailureForUnknownPart() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId unknownPartId = PartId.create();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.movePart(unknownPartId, 0));
        assertEquals(CompositionErrors.PART_NOT_FOUND, failure.error());
    }

    @Test
    void movePartReturnsFailureForInvalidIndex() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.movePart(partId, -1));
        assertEquals(CompositionErrors.INVALID_PART_INDEX, failure.error());
        failure = assertInstanceOf(Failure.class, composition.movePart(partId, 1));
        assertEquals(CompositionErrors.INVALID_PART_INDEX, failure.error());
    }

    @Test
    void movePartToEarlierIndexPreservesOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        PartId thirdPartId = composition.addPart();
        assertInstanceOf(Success.class, composition.movePart(thirdPartId, 0));
        assertEquals(
            List.of(thirdPartId, firstPartId, secondPartId),
            composition.parts().stream().map(Part::id).toList()
        );
    }

    @Test
    void movePartToLaterIndexPreservesOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        PartId thirdPartId = composition.addPart();
        assertInstanceOf(Success.class, composition.movePart(firstPartId, 2));
        assertEquals(
            List.of(secondPartId, thirdPartId, firstPartId),
            composition.parts().stream().map(Part::id).toList()
        );
    }

    @Test
    void movePartAllowsMovingPartToSameIndex() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        assertInstanceOf(Success.class, composition.movePart(firstPartId, 0));
        assertEquals(List.of(firstPartId, secondPartId), composition.parts().stream().map(Part::id).toList());
    }

    @Test
    void movePartRejectsNullPartId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.movePart(null, 0));
    }
}
