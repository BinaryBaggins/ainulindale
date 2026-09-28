package io.github.binarybaggins.ainulindale.domain.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScoreRangeTest {

    @Test
    void endIsDerivedFromStartAndDuration() {
        ScoreRange range = new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8));
        assertEquals(ScorePosition.of(3, 8), range.end());
    }

    @Test
    void nullStartIsRejected() {
        assertThrows(NullPointerException.class, () -> new ScoreRange(null, MusicalDuration.ZERO));
    }

    @Test
    void nullDurationIsRejected() {
        assertThrows(NullPointerException.class, () -> new ScoreRange(ScorePosition.ZERO, null));
    }

    @Test
    void zeroDurationIsAccepted() {
        ScoreRange range = new ScoreRange(ScorePosition.ZERO, MusicalDuration.ZERO);
        assertEquals(ScorePosition.ZERO, range.start());
        assertEquals(MusicalDuration.ZERO, range.duration());
        assertEquals(ScorePosition.ZERO, range.end());
    }

    @Test
    void rangesWithSameStartAndDurationAreEqual() {
        assertEquals(
            new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8)),
            new ScoreRange(ScorePosition.of(1, 4), MusicalDuration.of(1, 8))
        );
    }
}
