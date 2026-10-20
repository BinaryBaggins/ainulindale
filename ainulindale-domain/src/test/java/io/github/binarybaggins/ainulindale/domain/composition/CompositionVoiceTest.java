package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import java.util.List;
import org.junit.jupiter.api.Test;

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
    void removeVoiceRemovesSpecifiedVoice() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId initialVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> addedVoice = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId addedVoiceId = assertInstanceOf(VoiceId.class, addedVoice.value());
        assertInstanceOf(Success.class, composition.removeVoice(addedVoiceId));
        List<Voice> voices = composition.parts().getFirst().voices();
        assertEquals(1, voices.size());
        assertEquals(initialVoiceId, voices.getFirst().id());
        assertFalse(voices.stream().anyMatch(voice -> voice.id().equals(addedVoiceId)));
    }

    @Test
    void removeVoicePreservesRemainingVoiceOrder() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        assertInstanceOf(Success.class, composition.removeVoice(secondVoiceId));
        assertEquals(
            List.of(firstVoiceId, thirdVoiceId),
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

    @Test
    void moveVoiceToEarlierIndexPreservesOrder() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        assertInstanceOf(Success.class, composition.moveVoice(thirdVoiceId, 0));
        assertEquals(
            List.of(thirdVoiceId, firstVoiceId, secondVoiceId),
            composition.parts().getFirst().voices().stream().map(Voice::id).toList()
        );
    }

    @Test
    void moveVoiceToLaterIndexPreservesOrder() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, second.value());
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId thirdVoiceId = assertInstanceOf(VoiceId.class, third.value());
        assertInstanceOf(Success.class, composition.moveVoice(firstVoiceId, 2));
        assertEquals(
            List.of(secondVoiceId, thirdVoiceId, firstVoiceId),
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

    @Test
    void moveVoiceReturnsFailureForInvalidIndex() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();
        Failure<?> failure = assertInstanceOf(Failure.class, composition.moveVoice(voiceId, -1));
        assertEquals(CompositionErrors.INVALID_VOICE_INDEX, failure.error());
        failure = assertInstanceOf(Failure.class, composition.moveVoice(voiceId, 1));
        assertEquals(CompositionErrors.INVALID_VOICE_INDEX, failure.error());
        assertEquals(voiceId, composition.parts().getFirst().voices().getFirst().id());
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
}
