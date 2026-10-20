package io.github.binarybaggins.ainulindale.domain.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import org.junit.jupiter.api.Test;

class NoteEventTest {

    @Test
    void createGeneratesEventId() {
        NoteEvent note = NoteEvent.create(quarterNoteRange(), c4());
        assertNotNull(note.id());
    }

    @Test
    void rangeIsPreserved() {
        ScoreRange range = quarterNoteRange();
        NoteEvent note = NoteEvent.create(range, c4());
        assertEquals(range, note.range());
    }

    @Test
    void pitchIsPreserved() {
        Pitch pitch = c4();
        NoteEvent note = NoteEvent.create(quarterNoteRange(), pitch);
        assertEquals(pitch, note.pitch());
    }

    @Test
    void positionEqualsRangeStart() {
        ScoreRange range = quarterNoteRange();
        NoteEvent note = NoteEvent.create(range, c4());
        assertEquals(range.start(), note.position());
    }

    @Test
    void zeroDurationIsRejected() {
        ScoreRange range = new ScoreRange(ScorePosition.of(Rational.ZERO), MusicalDuration.ZERO);
        assertThrows(IllegalArgumentException.class, () -> NoteEvent.create(range, c4()));
    }

    @Test
    void positiveDurationIsAccepted() {
        assertDoesNotThrow(() -> NoteEvent.create(quarterNoteRange(), c4()));
    }

    @Test
    void nullRangeIsRejected() {
        assertThrows(NullPointerException.class, () -> NoteEvent.create(null, c4()));
    }

    @Test
    void nullPitchIsRejected() {
        assertThrows(NullPointerException.class, () -> NoteEvent.create(quarterNoteRange(), null));
    }

    private static ScoreRange quarterNoteRange() {
        return new ScoreRange(ScorePosition.of(Rational.ZERO), MusicalDuration.of(Rational.of(1, 4)));
    }

    private static Pitch c4() {
        return new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);
    }

    @Test
    void withPitchCreatesNewNotePreservingIdentityAndRange() {
        NoteEvent original = NoteEvent.create(quarterNoteRange(), c4());
        Pitch newPitch = new Pitch(DiatonicStep.G, PitchAlteration.NATURAL, 4);
        NoteEvent updated = original.withPitch(newPitch);
        assertNotSame(original, updated);
        assertEquals(original.id(), updated.id());
        assertEquals(original.range(), updated.range());
        assertEquals(newPitch, updated.pitch());
        assertEquals(c4(), original.pitch());
    }

    @Test
    void withPitchRejectsNullPitch() {
        NoteEvent note = NoteEvent.create(quarterNoteRange(), c4());
        assertThrows(NullPointerException.class, () -> note.withPitch(null));
    }

    @Test
    void withRangeCreatesNewNotePreservingIdentityAndPitch() {
        NoteEvent original = NoteEvent.create(quarterNoteRange(), c4());
        ScoreRange newRange = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        NoteEvent updated = original.withRange(newRange);
        assertNotSame(original, updated);
        assertEquals(original.id(), updated.id());
        assertEquals(original.pitch(), updated.pitch());
        assertEquals(newRange, updated.range());
        assertEquals(newRange.start(), updated.position());
        assertEquals(quarterNoteRange(), original.range());
    }

    @Test
    void withRangeRejectsNullRange() {
        NoteEvent note = NoteEvent.create(quarterNoteRange(), c4());
        assertThrows(NullPointerException.class, () -> note.withRange(null));
    }

    @Test
    void withRangeRejectsZeroDuration() {
        NoteEvent note = NoteEvent.create(quarterNoteRange(), c4());
        ScoreRange invalidRange = new ScoreRange(ScorePosition.of(Rational.ZERO), MusicalDuration.ZERO);
        assertThrows(IllegalArgumentException.class, () -> note.withRange(invalidRange));
    }
}
