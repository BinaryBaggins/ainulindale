package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.RelationId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.relation.Tie;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class CompositionTieTest {

    private static final Pitch C4 = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);

    private static final Pitch D4 = new Pitch(DiatonicStep.D, PitchAlteration.NATURAL, 4);

    @Test
    void addTieBetweenValidNotesReturnsRelationId() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4));

        Success<?> success = assertInstanceOf(Success.class, composition.addTie(source, target));
        RelationId relationId = assertInstanceOf(RelationId.class, success.value());

        assertEquals(1, composition.ties().size());

        Tie tie = composition.ties().iterator().next();
        assertEquals(relationId, tie.id());
        assertEquals(source, tie.sourceNoteId());
        assertEquals(target, tie.targetNoteId());
    }

    @Test
    void addTieRejectsNullSourceId() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.addTie(null, EventId.create()));
    }

    @Test
    void addTieRejectsNullTargetId() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.addTie(EventId.create(), null));
    }

    @Test
    void addTieWithUnknownSourceReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertFailure(composition, EventId.create(), target, CompositionErrors.NOTE_NOT_FOUND);
    }

    @Test
    void addTieWithUnknownTargetReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);
        EventId source = addNote(composition, voiceId, 0, 1, C4);

        assertFailure(composition, source, EventId.create(), CompositionErrors.NOTE_NOT_FOUND);
    }

    @Test
    void addTieWithIdenticalNoteIdsReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = EventId.of(source.value());

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_IDENTICAL);
    }

    @Test
    void addTieBetweenDifferentVoicesReturnsFailure() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId sourceVoiceId = firstVoiceId(composition);

        Success<?> addedVoice = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId targetVoiceId = assertInstanceOf(VoiceId.class, addedVoice.value());

        EventId source = addNote(composition, sourceVoiceId, 0, 1, C4);
        EventId target = addNote(composition, targetVoiceId, 1, 1, C4);

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_IN_DIFFERENT_VOICES);
    }

    @Test
    void addTieWithDifferentPitchesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, D4);

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_PITCH_MISMATCH);
    }

    @Test
    void addTieWithEnharmonicPitchesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        Pitch cSharp = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);
        Pitch dFlat = new Pitch(DiatonicStep.D, PitchAlteration.of(-1), 4);

        EventId source = addNote(composition, voiceId, 0, 1, cSharp);
        EventId target = addNote(composition, voiceId, 1, 1, dFlat);

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_PITCH_MISMATCH);
    }

    @Test
    void addTieWithGapBetweenNotesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 2, 1, C4);

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_NOT_ADJACENT);
    }

    @Test
    void addTieWithOverlappingNotesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 2, C4);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertFailure(composition, source, target, CompositionErrors.TIE_NOTES_NOT_ADJACENT);
    }

    @Test
    void addTieWithExistingSourceReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId firstTarget = addNote(composition, voiceId, 1, 1, C4);
        EventId secondTarget = addNote(composition, voiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(source, firstTarget));

        assertFailure(composition, source, secondTarget, CompositionErrors.TIE_SOURCE_ALREADY_HAS_OUTGOING_TIE);
    }

    @Test
    void addTieWithExistingTargetReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId firstSource = addNote(composition, voiceId, 0, 1, C4);
        EventId secondSource = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(firstSource, target));

        assertFailure(composition, secondSource, target, CompositionErrors.TIE_TARGET_ALREADY_HAS_INCOMING_TIE);
    }

    @Test
    void addTieAllowsLinearChains() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId first = addNote(composition, voiceId, 0, 1, C4);
        EventId middle = addNote(composition, voiceId, 1, 1, C4);
        EventId last = addNote(composition, voiceId, 2, 1, C4);

        Success<?> firstTie = assertInstanceOf(Success.class, composition.addTie(first, middle));
        Success<?> secondTie = assertInstanceOf(Success.class, composition.addTie(middle, last));

        RelationId firstId = assertInstanceOf(RelationId.class, firstTie.value());
        RelationId secondId = assertInstanceOf(RelationId.class, secondTie.value());

        assertNotEquals(firstId, secondId);
        assertEquals(2, composition.ties().size());

        assertTrue(
            composition
                .ties()
                .stream()
                .anyMatch(
                    tie ->
                        tie.id().equals(firstId) &&
                        tie.sourceNoteId().equals(first) &&
                        tie.targetNoteId().equals(middle)
                )
        );

        assertTrue(
            composition
                .ties()
                .stream()
                .anyMatch(
                    tie ->
                        tie.id().equals(secondId) &&
                        tie.sourceNoteId().equals(middle) &&
                        tie.targetNoteId().equals(last)
                )
        );
    }

    @Test
    void tiesReturnsUnmodifiableLiveView() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        Collection<Tie> view = composition.ties();
        assertTrue(view.isEmpty());

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(source, target));

        assertEquals(1, view.size());

        assertThrows(UnsupportedOperationException.class, view::clear);
    }

    private static Composition compositionWithPart() {
        Composition composition = Composition.create();
        composition.addPart();
        return composition;
    }

    private static VoiceId firstVoiceId(Composition composition) {
        return composition.parts().getFirst().voices().getFirst().id();
    }

    private static EventId addNote(
        Composition composition,
        VoiceId voiceId,
        long startQuarters,
        long durationQuarters,
        Pitch pitch
    ) {
        ScoreRange range = new ScoreRange(ScorePosition.of(startQuarters, 4), MusicalDuration.of(durationQuarters, 4));

        Success<?> success = assertInstanceOf(Success.class, composition.addNote(voiceId, range, pitch));

        return assertInstanceOf(EventId.class, success.value());
    }

    private static void assertFailure(
        Composition composition,
        EventId source,
        EventId target,
        ResultError expectedError
    ) {
        int originalTieCount = composition.ties().size();

        Failure<?> failure = assertInstanceOf(Failure.class, composition.addTie(source, target));

        assertEquals(expectedError, failure.error());
        assertEquals(originalTieCount, composition.ties().size());
    }

    @Test
    void moveTiedNoteToSameVoiceSucceeds() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(source, target));

        assertInstanceOf(Success.class, composition.moveNoteToVoice(source, voiceId));

        Voice voice = composition.parts().getFirst().voices().getFirst();

        assertEquals(2, voice.events().size());
        assertTrue(
            voice
                .events()
                .stream()
                .anyMatch(e -> e.id().equals(source))
        );
        assertEquals(1, composition.ties().size());
    }

    @Test
    void moveTiedNoteToUnknownVoiceReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId source = addNote(composition, voiceId, 0, 1, C4);
        EventId target = addNote(composition, voiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(source, target));

        Failure<?> failure = assertInstanceOf(Failure.class, composition.moveNoteToVoice(source, VoiceId.create()));

        assertEquals(CompositionErrors.VOICE_NOT_FOUND, failure.error());

        Voice voice = composition.parts().getFirst().voices().getFirst();
        assertEquals(2, voice.events().size());
        assertEquals(1, composition.ties().size());
    }

    @Test
    void moveTieSourceToDifferentVoiceReturnsFailure() {
        assertMovingTiedNoteToDifferentVoiceFails(true);
    }

    @Test
    void moveTieTargetToDifferentVoiceReturnsFailure() {
        assertMovingTiedNoteToDifferentVoiceFails(false);
    }

    private static void assertMovingTiedNoteToDifferentVoiceFails(boolean moveSource) {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();

        VoiceId sourceVoiceId = firstVoiceId(composition);
        Success<?> addedVoice = assertInstanceOf(Success.class, composition.addVoice(partId));
        VoiceId destinationVoiceId = assertInstanceOf(VoiceId.class, addedVoice.value());

        EventId source = addNote(composition, sourceVoiceId, 0, 1, C4);
        EventId target = addNote(composition, sourceVoiceId, 1, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(source, target));

        EventId noteToMove = moveSource ? source : target;

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            composition.moveNoteToVoice(noteToMove, destinationVoiceId)
        );

        assertEquals(CompositionErrors.TIE_VOICE_CONSTRAINT_WOULD_BE_BROKEN, failure.error());

        Voice originalVoice = composition.parts().getFirst().voices().getFirst();
        Voice destinationVoice = composition.parts().getFirst().voices().get(1);

        assertEquals(2, originalVoice.events().size());
        assertTrue(
            originalVoice
                .events()
                .stream()
                .anyMatch(e -> e.id().equals(source))
        );
        assertTrue(
            originalVoice
                .events()
                .stream()
                .anyMatch(e -> e.id().equals(target))
        );
        assertTrue(destinationVoice.events().isEmpty());
        assertEquals(1, composition.ties().size());
    }
}
