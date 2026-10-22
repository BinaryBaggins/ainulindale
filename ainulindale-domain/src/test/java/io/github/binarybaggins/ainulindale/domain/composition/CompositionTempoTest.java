package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.tempo.Tempo;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompositionTempoTest {

    private static Tempo tempo(long wholeNotesPerMinute) {
        return Tempo.of(Rational.of(wholeNotesPerMinute));
    }

    @Test
    void newCompositionHasNoTempo() {
        Composition composition = Composition.create();
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(0, 1)));
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(10, 1)));
    }

    @Test
    void setTempoAppliesFromGivenPositionOnwards() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(1, 1), tempo(30));
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(1, 2)));
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterTempoChangeDoesNotAffectEarlierPositions() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.setTempo(ScorePosition.of(4, 1), tempo(60));
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(tempo(60)), composition.tempoAt(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(tempo(60)), composition.tempoAt(ScorePosition.of(8, 1)));
    }

    @Test
    void setTempoAtSamePositionReplacesPreviousTempo() {
        Composition composition = Composition.create();
        ScorePosition position = ScorePosition.of(2, 1);
        composition.setTempo(position, tempo(30));
        composition.setTempo(position, tempo(45));
        assertEquals(Optional.of(tempo(45)), composition.tempoAt(position));
    }

    @Test
    void endTempoMakesTempoUnspecifiedFromGivenPosition() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.endTempo(ScorePosition.of(4, 1));
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(4, 1)));
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(9, 1)));
    }

    @Test
    void tempoCanBeSetAgainAfterItWasEnded() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.endTempo(ScorePosition.of(4, 1));
        composition.setTempo(ScorePosition.of(8, 1), tempo(60));
        assertEquals(Optional.empty(), composition.tempoAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(tempo(60)), composition.tempoAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removeTempoChangeRestoresPreviousTransition() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.setTempo(ScorePosition.of(4, 1), tempo(60));
        Result<Unit> result = composition.removeTempoChange(ScorePosition.of(4, 1));
        assertInstanceOf(Success.class, result);
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removeTempoChangeOfEndRestoresPreviousTempo() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.endTempo(ScorePosition.of(4, 1));
        assertInstanceOf(Success.class, composition.removeTempoChange(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removeUnknownTempoChangeReturnsFailure() {
        Composition composition = Composition.create();
        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        Result<Unit> result = composition.removeTempoChange(ScorePosition.of(1, 1));
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(CompositionErrors.TEMPO_CHANGE_NOT_FOUND, failure.error());
        assertEquals(Optional.of(tempo(30)), composition.tempoAt(ScorePosition.of(1, 1)));
    }

    @Test
    void tempoOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        ScorePosition position = ScorePosition.of(0, 1);
        assertThrows(NullPointerException.class, () -> composition.setTempo(null, tempo(30)));
        assertThrows(NullPointerException.class, () -> composition.setTempo(position, null));
        assertThrows(NullPointerException.class, () -> composition.endTempo(null));
        assertThrows(NullPointerException.class, () -> composition.removeTempoChange(null));
        assertThrows(NullPointerException.class, () -> composition.tempoAt(null));
    }

    @Test
    void tempoChangesDoNotAffectPartsAndVoices() {
        Composition composition = Composition.create();
        composition.addPart();
        List<Part> partsBefore = List.copyOf(composition.parts());
        List<Integer> voiceCountsBefore = composition.parts().stream().map(part -> part.voices().size()).toList();

        composition.setTempo(ScorePosition.of(0, 1), tempo(30));
        composition.endTempo(ScorePosition.of(4, 1));
        composition.removeTempoChange(ScorePosition.of(4, 1));

        assertEquals(partsBefore, composition.parts());
        assertEquals(
            voiceCountsBefore,
            composition.parts().stream().map(part -> part.voices().size()).toList()
        );
    }
}
