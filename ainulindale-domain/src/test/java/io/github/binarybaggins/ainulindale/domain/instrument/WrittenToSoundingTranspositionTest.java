package io.github.binarybaggins.ainulindale.domain.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchInterval;
import org.junit.jupiter.api.Test;

class WrittenToSoundingTranspositionTest {

    @Test
    void identityUsesZeroInterval() {
        assertEquals(PitchInterval.ZERO, WrittenToSoundingTransposition.IDENTITY.interval());

        assertEquals(WrittenToSoundingTransposition.of(PitchInterval.ZERO), WrittenToSoundingTransposition.IDENTITY);
    }

    @Test
    void preservesWrittenToSoundingDirection() {
        PitchInterval interval = PitchInterval.of(-1, Rational.of(-2));

        WrittenToSoundingTransposition transposition = WrittenToSoundingTransposition.of(interval);

        assertEquals(interval, transposition.interval());
        assertEquals(-1, transposition.interval().diatonicSteps());
        assertEquals(Rational.of(-2), transposition.interval().chromaticOffset());
    }

    @Test
    void distinguishesOppositeDirections() {
        WrittenToSoundingTransposition downward = WrittenToSoundingTransposition.of(
            PitchInterval.of(-1, Rational.of(-2))
        );

        WrittenToSoundingTransposition upward = WrittenToSoundingTransposition.of(PitchInterval.of(1, Rational.of(2)));

        assertNotEquals(downward, upward);
    }

    @Test
    void preservesRationalChromaticOffset() {
        PitchInterval interval = PitchInterval.of(1, Rational.of(3, 2));

        WrittenToSoundingTransposition transposition = WrittenToSoundingTransposition.of(interval);

        assertEquals(interval, transposition.interval());
    }

    @Test
    void structurallyEqualTranspositionsHaveEqualHashCodes() {
        WrittenToSoundingTransposition first = WrittenToSoundingTransposition.of(
            PitchInterval.of(-1, Rational.of(-4, 2))
        );

        WrittenToSoundingTransposition second = WrittenToSoundingTransposition.of(
            PitchInterval.of(-1, Rational.of(-2))
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void constructorAndFactoryProduceEqualValues() {
        PitchInterval interval = PitchInterval.of(-7, Rational.of(-12));

        assertEquals(new WrittenToSoundingTransposition(interval), WrittenToSoundingTransposition.of(interval));
    }

    @Test
    void rejectsNullInterval() {
        assertThrows(NullPointerException.class, () -> new WrittenToSoundingTransposition(null));

        assertThrows(NullPointerException.class, () -> WrittenToSoundingTransposition.of(null));
    }
}
