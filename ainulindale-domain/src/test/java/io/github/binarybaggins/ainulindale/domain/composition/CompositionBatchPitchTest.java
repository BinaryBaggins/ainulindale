package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import io.github.binarybaggins.ainulindale.domain.time.ScoreRange;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CompositionBatchPitchTest {

    private static final Pitch C4 = new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 4);

    private static final Pitch D4 = new Pitch(DiatonicStep.D, PitchAlteration.NATURAL, 4);

    @Test
    void changesMultipleUntiedNotes() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);

        assertInstanceOf(Success.class, fixture.composition().changeNotePitches(Map.of(first, D4, second, D4)));

        assertEquals(D4, fixture.pitchOf(first));
        assertEquals(D4, fixture.pitchOf(second));
    }

    @Test
    void changesBothEndpointsOfTieAtomically() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);
        fixture.tie(first, second);

        assertInstanceOf(Success.class, fixture.composition().changeNotePitches(Map.of(first, D4, second, D4)));

        assertEquals(D4, fixture.pitchOf(first));
        assertEquals(D4, fixture.pitchOf(second));
        assertEquals(1, fixture.composition().ties().size());
    }

    @Test
    void rejectsChangingOnlyOneTieEndpoint() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);
        fixture.tie(first, second);

        assertRejected(
            fixture.composition().changeNotePitches(Map.of(first, D4)),
            CompositionErrors.TIE_PITCH_EQUALITY_WOULD_BE_BROKEN
        );

        assertEquals(C4, fixture.pitchOf(first));
        assertEquals(C4, fixture.pitchOf(second));
    }

    @Test
    void rejectsDifferentFinalPitchesOfTiedNotes() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);
        fixture.tie(first, second);

        assertRejected(
            fixture.composition().changeNotePitches(Map.of(first, D4, second, C4)),
            CompositionErrors.TIE_PITCH_EQUALITY_WOULD_BE_BROKEN
        );

        assertEquals(C4, fixture.pitchOf(first));
        assertEquals(C4, fixture.pitchOf(second));
    }

    @Test
    void unknownNoteRejectsEntireBatch() {
        Fixture fixture = fixture();
        EventId existing = fixture.addNote(0);

        assertRejected(
            fixture.composition().changeNotePitches(Map.of(existing, D4, EventId.create(), D4)),
            CompositionErrors.EVENT_NOT_FOUND
        );

        assertEquals(C4, fixture.pitchOf(existing));
    }

    @Test
    void invalidTieLeavesUnrelatedChangesUnapplied() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);
        EventId unrelated = fixture.addNote(2);
        fixture.tie(first, second);

        assertRejected(
            fixture.composition().changeNotePitches(Map.of(first, D4, unrelated, D4)),
            CompositionErrors.TIE_PITCH_EQUALITY_WOULD_BE_BROKEN
        );

        assertEquals(C4, fixture.pitchOf(first));
        assertEquals(C4, fixture.pitchOf(second));
        assertEquals(C4, fixture.pitchOf(unrelated));
    }

    @Test
    void unchangedTieEndpointIsValid() {
        Fixture fixture = fixture();
        EventId first = fixture.addNote(0);
        EventId second = fixture.addNote(1);
        fixture.tie(first, second);

        assertInstanceOf(Success.class, fixture.composition().changeNotePitches(Map.of(first, C4)));

        assertEquals(C4, fixture.pitchOf(first));
        assertEquals(C4, fixture.pitchOf(second));
    }

    @Test
    void emptyBatchSucceeds() {
        Composition composition = Composition.create();

        assertInstanceOf(Success.class, composition.changeNotePitches(Map.of()));
    }

    @Test
    void nullMapIsRejected() {
        Composition composition = Composition.create();

        assertThrows(NullPointerException.class, () -> composition.changeNotePitches(null));
    }

    @Test
    void nullKeyIsRejected() {
        Composition composition = Composition.create();
        Map<EventId, Pitch> changes = new HashMap<>();
        changes.put(null, D4);

        assertThrows(NullPointerException.class, () -> composition.changeNotePitches(changes));
    }

    @Test
    void nullValueIsRejectedWithoutMutation() {
        Fixture fixture = fixture();
        EventId existing = fixture.addNote(0);
        Map<EventId, Pitch> changes = new HashMap<>();
        changes.put(existing, null);

        assertThrows(NullPointerException.class, () -> fixture.composition().changeNotePitches(changes));

        assertEquals(C4, fixture.pitchOf(existing));
    }

    private static Fixture fixture() {
        Composition composition = Composition.create();
        composition.addPart();
        Voice voice = composition.parts().getFirst().voices().getFirst();
        return new Fixture(composition, voice);
    }

    private static void assertRejected(
        Result<?> result,
        io.github.binarybaggins.ainulindale.core.result.ResultError error
    ) {
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(error, failure.error());
    }

    private record Fixture(Composition composition, Voice voice) {
        EventId addNote(long startQuarters) {
            ScoreRange range = new ScoreRange(ScorePosition.of(startQuarters, 4), MusicalDuration.of(1, 4));

            Success<?> result = assertInstanceOf(Success.class, composition.addNote(voice.id(), range, C4));

            return assertInstanceOf(EventId.class, result.value());
        }

        void tie(EventId source, EventId target) {
            assertInstanceOf(Success.class, composition.addTie(source, target));
        }

        Pitch pitchOf(EventId id) {
            return voice.findNote(id).pitch();
        }
    }
}
