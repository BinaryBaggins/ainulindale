package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
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

class CompositionVoiceTest {

    @Test
    void addVoiceAddsVoiceAndReturnsCreatedVoiceId() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        Result<VoiceId> result = composition.addVoice(partId);
        Success<?> success = assertInstanceOf(Success.class, result);
        VoiceId voiceId = assertInstanceOf(VoiceId.class, success.value());
        Part part = composition.parts().getFirst();
        assertEquals(2, part.voices().size());
        assertEquals(voiceId, part.voices().get(1).id());
    }

    @Test
    void addVoicePreservesVoiceOrder() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        assertEquals(
            List.of(firstVoiceId, secondVoiceId, thirdVoiceId),
            composition.parts().getFirst().voices().stream().map(Voice::id).toList()
        );
    }

    @Test
    void addVoiceForUnknownPartReturnsFailure() {
        Composition composition = Composition.create();
        PartId unknownPartId = PartId.create();
        Result<VoiceId> result = composition.addVoice(unknownPartId);
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(CompositionErrors.PART_NOT_FOUND, failure.error());
    }

    @Test
    void addVoiceRejectsNullPartId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.addVoice(null));
    }

    @Test
    void removeVoiceOnlyRemovesVoiceFromOwningPart() {
        Composition composition = Composition.create();
        composition.addPart();
        Part firstPart = composition.parts().getFirst();
        List<Voice> firstPartVoices = List.copyOf(firstPart.voices());
        PartId partId = composition.addPart();
        Part owningPart = composition.parts().get(1);
        VoiceId initialVoiceId = owningPart.voices().getFirst().id();
        Success<?> addedVoice = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId addedVoiceId = assertInstanceOf(VoiceId.class, addedVoice.value());
        assertInstanceOf(Success.class, composition.removeVoice(addedVoiceId));
        assertEquals(List.of(initialVoiceId), owningPart.voices().stream().map(Voice::id).toList());
        assertEquals(firstPartVoices, firstPart.voices());
        assertEquals(List.of(firstPart, owningPart), composition.parts());
    }

    @ParameterizedTest(name = "remove voice at index {0}")
    @CsvSource({ "0, 1, 2", "1, 0, 2", "2, 0, 1" })
    void removeVoicePreservesRemainingVoiceOrder(int removed, int expectedFirst, int expectedSecond) {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        List<VoiceId> ids = List.of(firstVoiceId, secondVoiceId, thirdVoiceId);
        assertInstanceOf(Success.class, composition.removeVoice(ids.get(removed)));
        assertEquals(
            List.of(ids.get(expectedFirst), ids.get(expectedSecond)),
            composition.parts().getFirst().voices().stream().map(Voice::id).toList()
        );
    }

    @Test
    void removeVoiceReturnsFailureWhenRemovingLastVoice() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.removeVoice(voiceId));
        assertEquals(CompositionErrors.CANNOT_REMOVE_LAST_VOICE, failure.error());
        assertEquals(1, composition.parts().getFirst().voices().size());
        assertEquals(voiceId, composition.parts().getFirst().voices().getFirst().id());
    }

    @Test
    void removeVoiceReturnsFailureForUnknownVoice() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId unknownVoiceId = VoiceId.create();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.removeVoice(unknownVoiceId));
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, failure.error());
    }

    @Test
    void removeVoiceRejectsNullVoiceId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.removeVoice(null));
    }

    @ParameterizedTest(name = "move from {0} to {1}")
    @CsvSource({ "2, 0, 2, 0, 1", "0, 2, 1, 2, 0", "1, 1, 0, 1, 2" })
    void moveVoicePreservesOrder(int fromIndex, int toIndex, int expectedFirst, int expectedSecond, int expectedThird) {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        List<VoiceId> ids = List.of(firstVoiceId, secondVoiceId, thirdVoiceId);
        assertInstanceOf(Success.class, composition.moveVoice(ids.get(fromIndex), toIndex));
        assertEquals(
            List.of(ids.get(expectedFirst), ids.get(expectedSecond), ids.get(expectedThird)),
            composition.parts().getFirst().voices().stream().map(Voice::id).toList()
        );
    }

    @Test
    void moveVoiceAllowsMovingOnlyVoiceToSameIndex() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();
        assertInstanceOf(Success.class, composition.moveVoice(voiceId, 0));
        assertEquals(List.of(voiceId), composition.parts().getFirst().voices().stream().map(Voice::id).toList());
    }

    @Test
    void moveVoiceReturnsFailureForUnknownVoice() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId unknownVoiceId = VoiceId.create();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.moveVoice(unknownVoiceId, 0));
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, failure.error());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 3 })
    void moveVoiceReturnsFailureForInvalidIndex(int index) {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        assertInstanceOf(Success.class, composition.addVoice(partId));
        assertInstanceOf(Success.class, composition.addVoice(partId));
        Part part = composition.parts().getFirst();
        List<Voice> originalOrder = List.copyOf(part.voices());
        VoiceId voiceId = originalOrder.get(1).id();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.moveVoice(voiceId, index));
        assertEquals(CompositionErrors.INVALID_VOICE_INDEX, failure.error());
        assertEquals(originalOrder, part.voices());
    }

    @Test
    void moveVoiceRejectsNullVoiceId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.moveVoice(null, 0));
    }

    @Test
    void moveVoiceOnlyReordersVoicesWithinOwningPart() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        VoiceId firstPartVoiceId = composition.parts().get(0).voices().getFirst().id();
        VoiceId secondPartFirstVoiceId = composition.parts().get(1).voices().getFirst().id();
        Success<?> added = assertInstanceOf(Success.class, composition.addVoice(secondPartId));
        VoiceId secondPartSecondVoiceId = assertInstanceOf(VoiceId.class, added.value());
        assertInstanceOf(Success.class, composition.moveVoice(secondPartSecondVoiceId, 0));
        assertEquals(firstPartId, composition.parts().get(0).id());
        assertEquals(List.of(firstPartVoiceId), composition.parts().get(0).voices().stream().map(Voice::id).toList());
        assertEquals(secondPartId, composition.parts().get(1).id());
        assertEquals(
            List.of(secondPartSecondVoiceId, secondPartFirstVoiceId),
            composition.parts().get(1).voices().stream().map(Voice::id).toList()
        );
    }

    private static Meter meter(int count, long denominator) {
        return new StructuredMeter(List.of(new MeterComponent(count, MusicalDuration.of(1, denominator))));
    }

    @Test
    void voiceMayHaveNoEffectiveMeter() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        assertEquals(new Success<>(Optional.empty()), composition.meterAt(voiceId, ScorePosition.ZERO));
    }

    @Test
    void voiceInheritsCompositionMeter() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(voiceId, ScorePosition.ZERO));
    }

    @Test
    void voiceInheritsPartMeterBeforeCompositionMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(voiceId, ScorePosition.ZERO));
    }

    @Test
    void voiceOverrideTakesPrecedenceOverPartAndComposition() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));

        assertInstanceOf(Success.class, composition.setMeter(voiceId, ScorePosition.of(2, 1), meter(6, 8)));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(voiceId, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(voiceId, ScorePosition.of(2, 1)));
    }

    @Test
    void voiceOverrideSurvivesParentChanges() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));
        composition.setMeter(voiceId, ScorePosition.of(10, 1), meter(6, 8));

        composition.setMeter(partId, ScorePosition.of(20, 1), meter(2, 4));
        composition.setMeter(ScorePosition.of(25, 1), meter(5, 8));

        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(voiceId, ScorePosition.of(30, 1)));
    }

    @Test
    void endingVoiceOverrideRestoresEffectivePartMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));
        composition.setMeter(voiceId, ScorePosition.of(10, 1), meter(6, 8));

        composition.endMeter(voiceId, ScorePosition.of(20, 1));

        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(voiceId, ScorePosition.of(19, 1)));
        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(voiceId, ScorePosition.of(20, 1)));
    }

    @Test
    void endingPartOverrideRestoresCompositionForVoice() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));
        composition.setMeter(voiceId, ScorePosition.ZERO, meter(6, 8));

        composition.endMeter(voiceId, ScorePosition.of(10, 1));
        composition.endMeter(partId, ScorePosition.of(20, 1));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(voiceId, ScorePosition.of(15, 1)));
        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(voiceId, ScorePosition.of(20, 1)));
    }

    @Test
    void removingVoiceEndRestoresEarlierOverride() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(voiceId, ScorePosition.ZERO, meter(6, 8));
        composition.endMeter(voiceId, ScorePosition.of(4, 1));

        assertInstanceOf(Success.class, composition.removeMeterChange(voiceId, ScorePosition.of(4, 1)));

        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(voiceId, ScorePosition.of(5, 1)));
    }

    @Test
    void removingVoiceSetRestoresInheritedMeter() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(voiceId, ScorePosition.ZERO, meter(6, 8));

        assertInstanceOf(Success.class, composition.removeMeterChange(voiceId, ScorePosition.ZERO));

        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(voiceId, ScorePosition.ZERO));
    }

    @Test
    void freeMeterOverridesInheritedMeter() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(partId, ScorePosition.ZERO, meter(3, 4));
        composition.setMeter(voiceId, ScorePosition.ZERO, FreeMeter.INSTANCE);

        assertEquals(new Success<>(Optional.of(FreeMeter.INSTANCE)), composition.meterAt(voiceId, ScorePosition.ZERO));

        composition.endMeter(voiceId, ScorePosition.of(2, 1));

        assertEquals(new Success<>(Optional.of(meter(3, 4))), composition.meterAt(voiceId, ScorePosition.of(2, 1)));
    }

    @Test
    void voiceMeterChangesDoNotAffectSiblingVoices() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId first = composition.parts().getFirst().voices().getFirst().id();

        Success<?> added = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId second = assertInstanceOf(VoiceId.class, added.value());

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(first, ScorePosition.ZERO, meter(6, 8));

        assertEquals(new Success<>(Optional.of(meter(6, 8))), composition.meterAt(first, ScorePosition.ZERO));
        assertEquals(new Success<>(Optional.of(meter(4, 4))), composition.meterAt(second, ScorePosition.ZERO));
    }

    @Test
    void unknownVoiceReturnsFailureForMeterOperations() {
        Composition composition = Composition.create();
        VoiceId unknown = VoiceId.create();

        Failure<?> set = assertInstanceOf(
            Failure.class,
            composition.setMeter(unknown, ScorePosition.ZERO, meter(4, 4))
        );
        Failure<?> end = assertInstanceOf(Failure.class, composition.endMeter(unknown, ScorePosition.ZERO));
        Failure<?> remove = assertInstanceOf(Failure.class, composition.removeMeterChange(unknown, ScorePosition.ZERO));
        Failure<?> query = assertInstanceOf(Failure.class, composition.meterAt(unknown, ScorePosition.ZERO));

        assertEquals(CompositionErrors.VOICE_NOT_FOUND, set.error());
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, end.error());
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, remove.error());
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, query.error());
    }

    @Test
    void removingUnknownVoiceMeterChangeReturnsFailure() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            composition.removeMeterChange(voiceId, ScorePosition.ZERO)
        );

        assertEquals(CompositionErrors.METER_CHANGE_NOT_FOUND, failure.error());
    }

    @Test
    void voiceMeterOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();

        assertThrows(NullPointerException.class, () ->
            composition.setMeter((VoiceId) null, ScorePosition.ZERO, meter(4, 4))
        );
        assertThrows(NullPointerException.class, () -> composition.setMeter(voiceId, null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> composition.setMeter(voiceId, ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> composition.endMeter((VoiceId) null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.endMeter(voiceId, null));
        assertThrows(NullPointerException.class, () ->
            composition.removeMeterChange((VoiceId) null, ScorePosition.ZERO)
        );
        assertThrows(NullPointerException.class, () -> composition.removeMeterChange(voiceId, null));
        assertThrows(NullPointerException.class, () -> composition.meterAt((VoiceId) null, ScorePosition.ZERO));
        assertThrows(NullPointerException.class, () -> composition.meterAt(voiceId, null));
    }
}
