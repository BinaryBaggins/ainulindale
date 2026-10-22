package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.meter.FreeMeter;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.meter.MeterComponent;
import io.github.binarybaggins.ainulindale.domain.meter.StructuredMeter;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Map;
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

        assertInstanceOf(Success.class, composition.setMeter(partId, ScorePosition.of(2, 1), meter(3, 4)));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(2, 1)));
    }

    @Test
    void localOverrideSurvivesLaterCompositionChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.of(10, 1), meter(3, 4));
        composition.setMeter(ScorePosition.of(20, 1), meter(6, 8));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(25, 1)));
    }

    @Test
    void endingOverrideRestoresDynamicInheritance() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.of(10, 1), meter(3, 4));
        composition.setMeter(ScorePosition.of(20, 1), meter(6, 8));

        assertInstanceOf(Success.class, composition.endMeter(partId, ScorePosition.of(30, 1)));

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
        composition.setMeter(partId, ScorePosition.of(2, 1), meter(3, 4));
        composition.endMeter(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(5, 1)));

        assertInstanceOf(Success.class, composition.removeMeterChange(partId, ScorePosition.of(4, 1)));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(partId, ScorePosition.of(5, 1)));
    }

    @Test
    void removingLocalSetRestoresInheritedMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.of(2, 1), meter(3, 4));

        assertInstanceOf(Success.class, composition.removeMeterChange(partId, ScorePosition.of(2, 1)));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(3, 1)));
    }

    @Test
    void freeMeterOverridesStructuredCompositionMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.of(2, 1), FreeMeter.INSTANCE);

        assertEquals(
            new Success<>(Optional.of(FreeMeter.INSTANCE)),
            composition.meterAt(partId, ScorePosition.of(3, 1))
        );

        composition.endMeter(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(partId, ScorePosition.of(4, 1)));
    }

    @Test
    void localMeterChangesDoNotAffectOtherParts() {
        Composition composition = Composition.create();
        PartId firstPart = composition.addPart();
        PartId secondPart = composition.addPart();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(firstPart, ScorePosition.ZERO, meter(3, 4));

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
            composition.setMeter(unknown, ScorePosition.ZERO, meter(4, 4))
        );
        Failure<?> endFailure = assertInstanceOf(Failure.class, composition.endMeter(unknown, ScorePosition.ZERO));
        Failure<?> removeFailure = assertInstanceOf(
            Failure.class,
            composition.removeMeterChange(unknown, ScorePosition.ZERO)
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

        Failure<?> failure = assertInstanceOf(Failure.class, composition.removeMeterChange(partId, ScorePosition.ZERO));

        assertEquals(CompositionErrors.METER_CHANGE_NOT_FOUND, failure.error());
    }

    @Test
    void localMeterOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        assertThrows(NullPointerException.class, () ->
            composition.setMeter((PartId) null, ScorePosition.ZERO, meter(4, 4))
        );
        assertThrows(NullPointerException.class, () -> composition.setMeter(partId, null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> composition.setMeter(partId, ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> composition.endMeter(partId, null));
        assertThrows(NullPointerException.class, () -> composition.removeMeterChange(partId, null));
        assertThrows(NullPointerException.class, () -> composition.meterAt(partId, null));
    }

    private static KeySignature signature(DiatonicStep step, PitchAlteration alteration) {
        return KeySignature.of(Map.of(step, alteration));
    }

    @Test
    void partInheritsCompositionKeySignature() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));

        composition.setKeySignature(ScorePosition.ZERO, global);

        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(partId, ScorePosition.ZERO));
    }

    @Test
    void partWithoutOverrideFollowsCompositionKeySignatureChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, first);
        composition.setKeySignature(ScorePosition.of(4, 1), second);

        assertEquals(new Success<>(Optional.of(first)), composition.keySignatureAt(partId, ScorePosition.of(3, 1)));
        assertEquals(new Success<>(Optional.of(second)), composition.keySignatureAt(partId, ScorePosition.of(4, 1)));
    }

    @Test
    void localKeySignatureOverridesCompositionKeySignature() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, global);

        assertInstanceOf(Success.class, composition.setKeySignature(partId, ScorePosition.of(2, 1), local));

        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(partId, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(local)), composition.keySignatureAt(partId, ScorePosition.of(2, 1)));
    }

    @Test
    void localKeySignatureOverrideSurvivesParentChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));
        KeySignature changedGlobal = signature(DiatonicStep.C, PitchAlteration.of(1, 2));

        composition.setKeySignature(ScorePosition.ZERO, global);
        composition.setKeySignature(partId, ScorePosition.of(10, 1), local);
        composition.setKeySignature(ScorePosition.of(20, 1), changedGlobal);

        assertEquals(new Success<>(Optional.of(local)), composition.keySignatureAt(partId, ScorePosition.of(25, 1)));
    }

    @Test
    void endingLocalKeySignatureRestoresDynamicInheritance() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));
        KeySignature second = signature(DiatonicStep.C, PitchAlteration.of(1, 2));

        composition.setKeySignature(ScorePosition.ZERO, first);
        composition.setKeySignature(partId, ScorePosition.of(10, 1), local);
        composition.setKeySignature(ScorePosition.of(20, 1), second);

        assertInstanceOf(Success.class, composition.endKeySignature(partId, ScorePosition.of(30, 1)));

        assertEquals(new Success<>(Optional.of(local)), composition.keySignatureAt(partId, ScorePosition.of(29, 1)));
        assertEquals(new Success<>(Optional.of(second)), composition.keySignatureAt(partId, ScorePosition.of(30, 1)));

        KeySignature third = signature(DiatonicStep.G, PitchAlteration.of(-1, 2));

        composition.setKeySignature(ScorePosition.of(40, 1), third);

        assertEquals(new Success<>(Optional.of(third)), composition.keySignatureAt(partId, ScorePosition.of(40, 1)));
    }

    @Test
    void removingLocalKeySignatureEndRestoresPreviousOverride() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, global);
        composition.setKeySignature(partId, ScorePosition.of(2, 1), local);
        composition.endKeySignature(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(partId, ScorePosition.of(5, 1)));

        assertInstanceOf(Success.class, composition.removeKeySignatureChange(partId, ScorePosition.of(4, 1)));

        assertEquals(new Success<>(Optional.of(local)), composition.keySignatureAt(partId, ScorePosition.of(5, 1)));
    }

    @Test
    void removingLocalKeySignatureSetRestoresInheritance() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, global);
        composition.setKeySignature(partId, ScorePosition.of(2, 1), local);

        assertInstanceOf(Success.class, composition.removeKeySignatureChange(partId, ScorePosition.of(2, 1)));

        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(partId, ScorePosition.of(3, 1)));
    }

    @Test
    void explicitNaturalKeySignatureOverridesInheritedSignature() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature natural = KeySignature.of(Map.of());

        composition.setKeySignature(ScorePosition.ZERO, global);
        composition.setKeySignature(partId, ScorePosition.of(2, 1), natural);

        assertEquals(new Success<>(Optional.of(natural)), composition.keySignatureAt(partId, ScorePosition.of(3, 1)));

        composition.endKeySignature(partId, ScorePosition.of(4, 1));

        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(partId, ScorePosition.of(4, 1)));
    }

    @Test
    void localKeySignatureChangesDoNotAffectOtherParts() {
        Composition composition = Composition.create();
        PartId firstPart = composition.addPart();
        PartId secondPart = composition.addPart();

        KeySignature global = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature local = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, global);
        composition.setKeySignature(firstPart, ScorePosition.ZERO, local);

        assertEquals(new Success<>(Optional.of(local)), composition.keySignatureAt(firstPart, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(global)), composition.keySignatureAt(secondPart, ScorePosition.ZERO));
    }

    @Test
    void partMayHaveNoEffectiveKeySignature() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        assertEquals(new Success<>(Optional.empty()), composition.keySignatureAt(partId, ScorePosition.ZERO));
    }

    @Test
    void unknownPartReturnsFailureForKeySignatureOperations() {
        Composition composition = Composition.create();
        PartId unknown = PartId.create();

        KeySignature signature = KeySignature.of(Map.of());

        Failure<?> setFailure = assertInstanceOf(
            Failure.class,
            composition.setKeySignature(unknown, ScorePosition.ZERO, signature)
        );
        Failure<?> endFailure = assertInstanceOf(
            Failure.class,
            composition.endKeySignature(unknown, ScorePosition.ZERO)
        );
        Failure<?> removeFailure = assertInstanceOf(
            Failure.class,
            composition.removeKeySignatureChange(unknown, ScorePosition.ZERO)
        );
        Failure<?> queryFailure = assertInstanceOf(
            Failure.class,
            composition.keySignatureAt(unknown, ScorePosition.ZERO)
        );

        assertEquals(CompositionErrors.PART_NOT_FOUND, setFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, endFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, removeFailure.error());
        assertEquals(CompositionErrors.PART_NOT_FOUND, queryFailure.error());
    }

    @Test
    void removingUnknownLocalKeySignatureChangeReturnsFailure() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            composition.removeKeySignatureChange(partId, ScorePosition.ZERO)
        );

        assertEquals(CompositionErrors.KEY_SIGNATURE_CHANGE_NOT_FOUND, failure.error());
    }

    @Test
    void localKeySignatureOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        KeySignature signature = KeySignature.of(Map.of());

        assertThrows(NullPointerException.class, () ->
            composition.setKeySignature((PartId) null, ScorePosition.ZERO, signature)
        );
        assertThrows(NullPointerException.class, () -> composition.setKeySignature(partId, null, signature));
        assertThrows(NullPointerException.class, () -> composition.setKeySignature(partId, ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> composition.endKeySignature((PartId) null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.endKeySignature(partId, null));
        assertThrows(NullPointerException.class, () ->
            composition.removeKeySignatureChange((PartId) null, ScorePosition.ZERO)
        );
        assertThrows(NullPointerException.class, () -> composition.removeKeySignatureChange(partId, null));
        assertThrows(NullPointerException.class, () -> composition.keySignatureAt((PartId) null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.keySignatureAt(partId, null));
    }
}
