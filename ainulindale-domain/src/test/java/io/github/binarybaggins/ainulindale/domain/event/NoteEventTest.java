package io.github.binarybaggins.ainulindale.domain.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
