package io.github.binarybaggins.ainulindale.editing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.composition.Composition;
import io.github.binarybaggins.ainulindale.domain.composition.Voice;
import io.github.binarybaggins.ainulindale.domain.event.NoteEvent;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchInterval;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NoteTranspositionTest {

    private static final Pitch C4 = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);

    private static final Pitch D4 = new Pitch(DiatonicStep.D, PitchAlteration.NATURAL, 4);

    private static final PitchInterval MAJOR_SECOND = PitchInterval.of(1, Rational.of(2));

    @Test
    void transposesIndependentNotes() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId first = addNote(composition, voice, 0, C4);
        EventId second = addNote(composition, voice, 1, new Pitch(DiatonicStep.E, PitchAlteration.NATURAL, 4));

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(first, second), MAJOR_SECOND));

        assertEquals(D4, pitchOf(voice, first));
        assertEquals(new Pitch(DiatonicStep.F, PitchAlteration.of(1), 4), pitchOf(voice, second));
    }

    @Test
    void transposesBothTiedNotesAtomically() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId first = addNote(composition, voice, 0, C4);
        EventId second = addNote(composition, voice, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(first, second));

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(first, second), MAJOR_SECOND));

        assertEquals(D4, pitchOf(voice, first));
        assertEquals(D4, pitchOf(voice, second));
        assertEquals(1, composition.ties().size());
    }

    @Test
    void transposesTieChainOfThreeNotes() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId first = addNote(composition, voice, 0, C4);
        EventId second = addNote(composition, voice, 1, C4);
        EventId third = addNote(composition, voice, 2, C4);

        assertInstanceOf(Success.class, composition.addTie(first, second));

        assertInstanceOf(Success.class, composition.addTie(second, third));

        assertInstanceOf(
            Success.class,
            NoteTransposition.transpose(composition, Set.of(first, second, third), MAJOR_SECOND)
        );

        assertEquals(D4, pitchOf(voice, first));
        assertEquals(D4, pitchOf(voice, second));
        assertEquals(D4, pitchOf(voice, third));
        assertEquals(2, composition.ties().size());
    }

    @Test
    void failsWhenOnlyOneTieEndpointSelected() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId first = addNote(composition, voice, 0, C4);
        EventId second = addNote(composition, voice, 1, C4);

        assertInstanceOf(Success.class, composition.addTie(first, second));

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            NoteTransposition.transpose(composition, Set.of(first), MAJOR_SECOND)
        );

        assertEquals("Composition.TiePitchEqualityWouldBeBroken", failure.error().code());

        assertEquals(C4, pitchOf(voice, first));
        assertEquals(C4, pitchOf(voice, second));
        assertEquals(1, composition.ties().size());
    }

    @Test
    void failsWithUnknownEventIdWithoutMutation() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId existing = addNote(composition, voice, 0, C4);
        EventId unknown = EventId.create();

        Failure<?> failure = assertInstanceOf(
            Failure.class,
            NoteTransposition.transpose(composition, Set.of(existing, unknown), MAJOR_SECOND)
        );

        assertEquals("Editing.SelectedNoteNotFound", failure.error().code());

        assertEquals(C4, pitchOf(voice, existing));
    }

    @Test
    void succeedsWithEmptySelection() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId noteId = addNote(composition, voice, 0, C4);

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(), MAJOR_SECOND));

        assertEquals(C4, pitchOf(voice, noteId));
    }

    @Test
    void failsWithNullComposition() {
        assertThrows(NullPointerException.class, () -> NoteTransposition.transpose(null, Set.of(), MAJOR_SECOND));
    }

    @Test
    void failsWithNullSelectedNoteIds() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> NoteTransposition.transpose(composition, null, MAJOR_SECOND));
    }

    @Test
    void failsWithNullInterval() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> NoteTransposition.transpose(composition, Set.of(), null));
    }

    @Test
    void failsWithNullIdInSelection() {
        Composition composition = compositionWithPart();

        Set<EventId> selected = new HashSet<>();
        selected.add(EventId.create());
        selected.add(null);

        assertThrows(NullPointerException.class, () ->
            NoteTransposition.transpose(composition, selected, MAJOR_SECOND)
        );
    }

    @Test
    void transposesWithFractionalChromaticInterval() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId noteId = addNote(composition, voice, 0, C4);

        PitchInterval interval = PitchInterval.of(2, Rational.of(7, 2));

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(noteId), interval));

        assertEquals(new Pitch(DiatonicStep.E, PitchAlteration.of(-1, 2), 4), pitchOf(voice, noteId));
    }

    @Test
    void transposesAcrossOctaveBoundaryUp() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        Pitch b4 = new Pitch(DiatonicStep.B, PitchAlteration.NATURAL, 4);

        EventId noteId = addNote(composition, voice, 0, b4);

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(noteId), MAJOR_SECOND));

        assertEquals(new Pitch(DiatonicStep.C, PitchAlteration.of(1), 5), pitchOf(voice, noteId));
    }

    @Test
    void transposesAcrossOctaveBoundaryDown() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId noteId = addNote(composition, voice, 0, C4);

        PitchInterval interval = PitchInterval.of(-1, Rational.of(-2));

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(noteId), interval));

        assertEquals(new Pitch(DiatonicStep.B, PitchAlteration.of(-1), 3), pitchOf(voice, noteId));
    }

    @Test
    void zeroIntervalPreservesPitch() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        EventId noteId = addNote(composition, voice, 0, C4);

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(noteId), PitchInterval.ZERO));

        assertEquals(C4, pitchOf(voice, noteId));
    }

    @Test
    void transposesNotesAcrossMultipleVoices() {
        Composition composition = compositionWithPart();

        var part = composition.parts().getFirst();
        Voice firstVoice = part.voices().getFirst();

        assertInstanceOf(Success.class, composition.addVoice(part.id()));

        Voice secondVoice = part.voices().get(1);

        EventId first = addNote(composition, firstVoice, 0, C4);

        EventId second = addNote(composition, secondVoice, 0, C4);

        assertInstanceOf(Success.class, NoteTransposition.transpose(composition, Set.of(first, second), MAJOR_SECOND));

        assertEquals(D4, pitchOf(firstVoice, first));
        assertEquals(D4, pitchOf(secondVoice, second));
    }

    @Test
    void octaveOverflowThrowsWithoutMutation() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        Pitch highestOctave = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, Integer.MAX_VALUE);

        EventId noteId = addNote(composition, voice, 0, highestOctave);

        PitchInterval octaveUp = PitchInterval.of(7, Rational.of(12));

        assertThrows(ArithmeticException.class, () ->
            NoteTransposition.transpose(composition, Set.of(noteId), octaveUp)
        );

        assertEquals(highestOctave, pitchOf(voice, noteId));
    }

    @Test
    void overflowInBatchLeavesAllNotesUnchanged() {
        Composition composition = compositionWithPart();
        Voice voice = firstVoice(composition);

        Pitch highestOctave = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, Integer.MAX_VALUE);

        EventId normal = addNote(composition, voice, 0, C4);
        EventId overflowing = addNote(composition, voice, 1, highestOctave);

        PitchInterval octaveUp = PitchInterval.of(7, Rational.of(12));

        assertThrows(ArithmeticException.class, () ->
            NoteTransposition.transpose(composition, Set.of(normal, overflowing), octaveUp)
        );

        assertEquals(C4, pitchOf(voice, normal));
        assertEquals(highestOctave, pitchOf(voice, overflowing));
    }

    private static Composition compositionWithPart() {
        Composition composition = Composition.create();
        composition.addPart();
        return composition;
    }

    private static Voice firstVoice(Composition composition) {
        return composition.parts().getFirst().voices().getFirst();
    }

    private static EventId addNote(Composition composition, Voice voice, long startQuarters, Pitch pitch) {
        ScoreRange range = new ScoreRange(ScorePosition.of(startQuarters, 4), MusicalDuration.of(1, 4));

        Result<EventId> result = composition.addNote(voice.id(), range, pitch);

        return assertInstanceOf(EventId.class, assertInstanceOf(Success.class, result).value());
    }

    private static Pitch pitchOf(Voice voice, EventId eventId) {
        return voice
            .events()
            .stream()
            .filter(event -> event.id().equals(eventId))
            .map(event -> ((NoteEvent) event).pitch())
            .findFirst()
            .orElseThrow();
    }
}
