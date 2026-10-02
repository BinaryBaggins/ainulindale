package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.List;
import org.junit.jupiter.api.Test;

class CompositionTest {

    @Test
    void createGeneratesCompositionId() {
        Composition composition = Composition.create();
        assertNotNull(composition.id());
    }

    @Test
    void newCompositionHasNoParts() {
        Composition composition = Composition.create();
        assertTrue(composition.parts().isEmpty());
    }

    @Test
    void partsCollectionIsUnmodifiable() {
        Composition composition = Composition.create();
        assertThrows(UnsupportedOperationException.class, () -> composition.parts().clear());
    }

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
    void addNoteToVoiceAddsNoteAndReturnsCreatedEventId() {
        Composition composition = Composition.create();
        composition.addPart();
        Voice voice = composition.parts().getFirst().voices().getFirst();
        VoiceId voiceId = voice.id();
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);
        Success<?> success = assertInstanceOf(Success.class, composition.addNote(voiceId, range, pitch));
        EventId eventId = assertInstanceOf(EventId.class, success.value());
        assertEquals(1, voice.events().size());
        assertEquals(eventId, voice.events().iterator().next().id());
    }

    @Test
    void addNoteAddsNoteOnlyToSpecifiedVoice() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        Voice firstVoice = composition.parts().getFirst().voices().getFirst();
        Success<?> addedVoice = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId secondVoiceId = assertInstanceOf(VoiceId.class, addedVoice.value());
        Voice secondVoice = composition.parts().getFirst().voices().get(1);
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);
        Success<?> success = assertInstanceOf(Success.class, composition.addNote(secondVoiceId, range, pitch));

        assertInstanceOf(EventId.class, success.value());
        assertTrue(firstVoice.events().isEmpty());
        assertEquals(1, secondVoice.events().size());
    }

    @Test
    void addNoteToUnknownVoiceReturnsFailure() {
        Composition composition = Composition.create();
        VoiceId unknownVoiceId = VoiceId.create();
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);
        Result<EventId> result = composition.addNote(unknownVoiceId, range, pitch);
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(CompositionErrors.VOICE_NOT_FOUND, failure.error());
    }

    @Test
    void addNoteRejectsNullVoiceId() {
        Composition composition = Composition.create();
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);
        assertThrows(NullPointerException.class, () -> composition.addNote(null, range, pitch));
    }

    @Test
    void addNoteRejectsNullScoreRange() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);
        assertThrows(NullPointerException.class, () -> composition.addNote(voiceId, null, pitch));
    }

    @Test
    void addNoteRejectsNullPitch() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().getFirst().voices().getFirst().id();
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        assertThrows(NullPointerException.class, () -> composition.addNote(voiceId, range, null));
    }
}
