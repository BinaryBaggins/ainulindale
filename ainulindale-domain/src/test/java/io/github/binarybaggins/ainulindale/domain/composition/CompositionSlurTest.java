package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.*;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.RelationId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.relation.Slur;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class CompositionSlurTest {

    private static final Pitch C4 = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);

    private static final Pitch D4 = new Pitch(DiatonicStep.D, PitchAlteration.NATURAL, 4);

    @Test
    void addSlurBetweenValidNotesReturnsRelationId() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);
        EventId end = addNote(composition, voiceId, 1, 1, D4);

        Success<?> success = assertInstanceOf(Success.class, composition.addSlur(start, end));

        RelationId relationId = assertInstanceOf(RelationId.class, success.value());

        assertEquals(1, composition.slurs().size());

        Slur slur = composition.slurs().iterator().next();

        assertEquals(relationId, slur.id());
        assertEquals(start, slur.startNoteId());
        assertEquals(end, slur.endNoteId());
    }

    @Test
    void addSlurRejectsNullStartId() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.addSlur(null, EventId.create()));
    }

    @Test
    void addSlurRejectsNullEndId() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.addSlur(EventId.create(), null));
    }

    @Test
    void addSlurWithUnknownStartReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId end = addNote(composition, voiceId, 1, 1, C4);

        assertFailure(composition, EventId.create(), end, CompositionErrors.NOTE_NOT_FOUND);
    }

    @Test
    void addSlurWithUnknownEndReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);

        assertFailure(composition, start, EventId.create(), CompositionErrors.NOTE_NOT_FOUND);
    }

    @Test
    void addSlurWithIdenticalNoteIdsReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);

        assertFailure(composition, start, EventId.of(start.value()), CompositionErrors.SLUR_NOTES_IDENTICAL);
    }

    @Test
    void addSlurWithReversedNotesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId earlier = addNote(composition, voiceId, 0, 1, C4);
        EventId later = addNote(composition, voiceId, 1, 1, D4);

        assertFailure(composition, later, earlier, CompositionErrors.SLUR_NOTES_NOT_CHRONOLOGICAL);
    }

    @Test
    void addSlurWithSimultaneousNotesReturnsFailure() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);
        EventId end = addNote(composition, voiceId, 0, 1, D4);

        assertFailure(composition, start, end, CompositionErrors.SLUR_NOTES_NOT_CHRONOLOGICAL);
    }

    @Test
    void addSlurAllowsGapBetweenNotes() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);
        EventId end = addNote(composition, voiceId, 3, 1, D4);

        assertInstanceOf(Success.class, composition.addSlur(start, end));

        assertEquals(1, composition.slurs().size());
    }

    @Test
    void addSlurAllowsOverlappingNotes() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 3, C4);
        EventId end = addNote(composition, voiceId, 1, 1, D4);

        assertInstanceOf(Success.class, composition.addSlur(start, end));

        assertEquals(1, composition.slurs().size());
    }

    @Test
    void slursReturnsUnmodifiableLiveView() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        Collection<Slur> view = composition.slurs();

        assertTrue(view.isEmpty());

        EventId start = addNote(composition, voiceId, 0, 1, C4);
        EventId end = addNote(composition, voiceId, 1, 1, D4);

        assertInstanceOf(Success.class, composition.addSlur(start, end));

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

    private static void assertFailure(Composition composition, EventId start, EventId end, ResultError expectedError) {
        int originalCount = composition.slurs().size();

        Failure<?> failure = assertInstanceOf(Failure.class, composition.addSlur(start, end));

        assertEquals(expectedError, failure.error());
        assertEquals(originalCount, composition.slurs().size());
    }

    @Test
    void removeSlurPreservesReferencedNotes() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId start = addNote(composition, voiceId, 0, 1, C4);
        EventId end = addNote(composition, voiceId, 1, 1, D4);

        Success<?> added = assertInstanceOf(Success.class, composition.addSlur(start, end));
        RelationId slurId = assertInstanceOf(RelationId.class, added.value());
        Collection<Slur> view = composition.slurs();

        assertInstanceOf(Success.class, composition.removeSlur(slurId));

        assertTrue(view.isEmpty());

        Voice voice = composition.parts().getFirst().voices().getFirst();
        assertNotNull(voice.findNote(start));
        assertNotNull(voice.findNote(end));
    }

    @Test
    void removeUnknownSlurReturnsFailure() {
        Composition composition = Composition.create();

        Failure<?> failure = assertInstanceOf(Failure.class, composition.removeSlur(RelationId.create()));

        assertEquals(CompositionErrors.SLUR_NOT_FOUND, failure.error());
    }

    @Test
    void removeSlurRejectsNullId() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.removeSlur(null));
    }

    @Test
    void removeSlurPreservesOtherSlurs() {
        Composition composition = compositionWithPart();
        VoiceId voiceId = firstVoiceId(composition);

        EventId first = addNote(composition, voiceId, 0, 1, C4);
        EventId middle = addNote(composition, voiceId, 1, 1, D4);
        EventId last = addNote(composition, voiceId, 2, 1, C4);

        Success<?> firstResult = assertInstanceOf(Success.class, composition.addSlur(first, middle));
        Success<?> secondResult = assertInstanceOf(Success.class, composition.addSlur(middle, last));

        RelationId firstId = assertInstanceOf(RelationId.class, firstResult.value());
        RelationId secondId = assertInstanceOf(RelationId.class, secondResult.value());

        assertInstanceOf(Success.class, composition.removeSlur(firstId));

        assertEquals(1, composition.slurs().size());

        Slur remaining = composition.slurs().iterator().next();
        assertEquals(secondId, remaining.id());
        assertEquals(middle, remaining.startNoteId());
        assertEquals(last, remaining.endNoteId());
    }
}
