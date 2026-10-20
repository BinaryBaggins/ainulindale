package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest(name = "remove part at index {0}")
    @CsvSource({ "0, 1, 2", "1, 0, 2", "2, 0, 1" })
    void removePartPreservesRemainingPartOrder(int removed, int expectedFirst, int expectedSecond) {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        PartId thirdPartId = composition.addPart();
        List<PartId> ids = List.of(firstPartId, secondPartId, thirdPartId);
        assertInstanceOf(Success.class, composition.removePart(ids.get(removed)));
        assertEquals(
            List.of(ids.get(expectedFirst), ids.get(expectedSecond)),
            composition.parts().stream().map(Part::id).toList()
        );
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

    @ParameterizedTest
    @ValueSource(ints = { -1, 3 })
    void movePartReturnsFailureForInvalidIndex(int index) {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        composition.addPart();
        composition.addPart();
        List<Part> originalOrder = List.copyOf(composition.parts());
        Failure<?> failure = assertInstanceOf(Failure.class, composition.movePart(partId, index));
        assertEquals(CompositionErrors.INVALID_PART_INDEX, failure.error());
        assertEquals(originalOrder, composition.parts());
    }

    @ParameterizedTest(name = "move from {0} to {1}")
    @CsvSource({ "2, 0, 2, 0, 1", "0, 2, 1, 2, 0", "1, 1, 0, 1, 2" })
    void movePartPreservesOrder(int fromIndex, int toIndex, int expectedFirst, int expectedSecond, int expectedThird) {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        PartId thirdPartId = composition.addPart();
        List<PartId> ids = List.of(firstPartId, secondPartId, thirdPartId);
        assertInstanceOf(Success.class, composition.movePart(ids.get(fromIndex), toIndex));
        assertEquals(
            List.of(ids.get(expectedFirst), ids.get(expectedSecond), ids.get(expectedThird)),
            composition.parts().stream().map(Part::id).toList()
        );
    }

    @Test
    void movePartRejectsNullPartId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.movePart(null, 0));
    }
}
