package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.meter.FreeMeter;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.meter.MeterComponent;
import io.github.binarybaggins.ainulindale.domain.meter.StructuredMeter;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Optional;
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

    private static Meter meter(int count, long denominator) {
        return new StructuredMeter(List.of(new MeterComponent(count, MusicalDuration.of(1, denominator))));
    }

    @Test
    void partInheritsCompositionMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.ZERO, fourFour);

        assertEquals(new Success<>(Optional.of(fourFour)), composition.meterAt(partId, ScorePosition.ZERO));
    }

    @Test
    void partWithoutOverrideFollowsCompositionChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(ScorePosition.of(4, 1), meter(3, 4));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(3, 1)));
        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(4, 1)));
    }

    @Test
    void localOverrideTakesPrecedenceOverCompositionMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));

        assertInstanceOf(Success.class, composition.setLocalMeter(partId, ScorePosition.of(2, 1), meter(3, 4)));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(2, 1)));
    }

    @Test
    void localOverrideSurvivesLaterCompositionChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(partId, ScorePosition.of(10, 1), meter(3, 4));
        composition.setMeter(ScorePosition.of(20, 1), meter(6, 8));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(25, 1)));
    }

    @Test
    void endingOverrideRestoresDynamicInheritance() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(partId, ScorePosition.of(10, 1), meter(3, 4));
        composition.setMeter(ScorePosition.of(20, 1), meter(6, 8));

        assertInstanceOf(Success.class, composition.endLocalMeter(partId, ScorePosition.of(30, 1)));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(29, 1)));
        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(partId, ScorePosition.of(30, 1)));

        composition.setMeter(ScorePosition.of(40, 1), meter(2, 4));

        assertEquals(new Success<>(Optional.of(meter(2, 4))), composition.meterAt(partId, ScorePosition.of(40, 1)));
    }

    @Test
    void removingEndRestoresLocalOverride() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(partId, ScorePosition.of(2, 1), meter(3, 4));
        composition.endLocalMeter(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(5, 1)));

        assertInstanceOf(Success.class, composition.removeLocalMeterChange(partId, ScorePosition.of(4, 1)));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(5, 1)));
    }

    @Test
    void removingLocalSetRestoresInheritedMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(partId, ScorePosition.of(2, 1), meter(3, 4));

        assertInstanceOf(Success.class, composition.removeLocalMeterChange(partId, ScorePosition.of(2, 1)));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(3, 1)));
    }

    @Test
    void freeMeterOverridesStructuredCompositionMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(partId, ScorePosition.of(2, 1), FreeMeter.INSTANCE);

        assertEquals(
            new Success<>(Optional.of(FreeMeter.INSTANCE)),
            composition.meterAt(partId, ScorePosition.of(3, 1))
        );

        composition.endLocalMeter(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(4, 1)));
    }

    @Test
    void localMeterChangesDoNotAffectOtherParts() {
        Composition composition = Composition.create();
        PartId firstPart = composition.addPart();
        PartId secondPart = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setLocalMeter(firstPart, ScorePosition.ZERO, meter(3, 4));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(firstPart, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(secondPart, ScorePosition.ZERO));
    }

    @Test
    void partMayHaveNoEffectiveMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        assertEquals(new Success<>(Optional.empty()), composition.meterAt(partId, ScorePosition.ZERO));
    }

    @Test
    void unknownPartReturnsFailure() {
        Composition composition = Composition.create();
        PartId unknown = PartId.create();

        Failure<?> setFailure = assertInstanceOf(
            Failure.class,
            composition.setLocalMeter(unknown, ScorePosition.ZERO, meter(4, 4))
        );
        Failure<?> endFailure = assertInstanceOf(Failure.class, composition.endLocalMeter(unknown, ScorePosition.ZERO));
        Failure<?> removeFailure = assertInstanceOf(
            Failure.class,
            composition.removeLocalMeterChange(unknown, ScorePosition.ZERO)
        );
        Failure<?> queryFailure = assertInstanceOf(Failure.class, composition.meterAt(unknown, ScorePosition.ZERO));

        assertEquals(CompositionErrors.PART_NOT_FOUND, setFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, endFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, removeFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, queryFailure.error());
    }

    @Test
    void removingUnknownLocalChangeReturnsFailure() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            composition.removeLocalMeterChange(partId, ScorePosition.ZERO)
        );

        assertEquals(CompositionErrors.METER_CHANGE_NOT_FOUND, failure.error());
    }

    @Test
    void localMeterOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        assertThrows(NullPointerException.class, () ->
            composition.setLocalMeter(null, ScorePosition.ZERO, meter(4, 4))
        );
        assertThrows(NullPointerException.class, () -> composition.setLocalMeter(partId, null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> composition.setLocalMeter(partId, ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> composition.endLocalMeter(partId, null));
        assertThrows(NullPointerException.class, () -> composition.removeLocalMeterChange(partId, null));
        assertThrows(NullPointerException.class, () -> composition.meterAt(partId, null));
    }
}
