package io.github.binarybaggins.ainulindale.domain.pitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import org.junit.jupiter.api.Test;

class PitchTest {

    @Test
    void pitchPreservesStructuralComponents() {
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);

        assertEquals(DiatonicStep.C, pitch.step());
        assertEquals(PitchAlteration.of(1), pitch.alteration());
        assertEquals(4, pitch.octave());
    }

    @Test
    void nullStepIsRejected() {
        assertThrows(NullPointerException.class, () -> new Pitch(null, PitchAlteration.NATURAL, 4));
    }

    @Test
    void nullAlterationIsRejected() {
        assertThrows(NullPointerException.class, () -> new Pitch(DiatonicStep.C, null, 4));
    }

    @Test
    void arbitraryIntegerOctavesAreAccepted() {
        assertEquals(-3, new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, -3).octave());

        assertEquals(42, new Pitch(DiatonicStep.C, PitchAlteration.NATURAL, 42).octave());
    }

    @Test
    void enharmonicSpellingsRemainDistinct() {
        Pitch cSharp = new Pitch(DiatonicStep.C, PitchAlteration.of(1), 4);

        Pitch dFlat = new Pitch(DiatonicStep.D, PitchAlteration.of(-1), 4);

        assertNotEquals(cSharp, dFlat);
    }

    @Test
    void microtonalPitchIsValid() {
        Pitch pitch = new Pitch(DiatonicStep.C, PitchAlteration.of(1, 2), 4);

        assertEquals(PitchAlteration.of(1, 2), pitch.alteration());
    }

    @Test
    void zeroIntervalPreservesNaturalPitch() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        assertEquals(source, source.transposedBy(PitchInterval.ZERO));
    }

    @Test
    void zeroIntervalPreservesAlteredPitch() {
        Pitch source = pitch(DiatonicStep.F, 1, 4);

        assertEquals(source, source.transposedBy(PitchInterval.ZERO));
    }

    @Test
    void zeroIntervalPreservesFractionalAlteration() {
        Pitch source = new Pitch(DiatonicStep.F, PitchAlteration.of(1, 2), 4);

        assertEquals(source, source.transposedBy(PitchInterval.ZERO));
    }

    @Test
    void transpositionDerivesFlatAlteration() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(1, Rational.of(1)));

        assertEquals(pitch(DiatonicStep.D, -1, 4), result);
    }

    @Test
    void transpositionAccountsForSourceAlteration() {
        Pitch source = pitch(DiatonicStep.F, 1, 4);

        Pitch result = source.transposedBy(PitchInterval.of(1, Rational.of(2)));

        assertEquals(pitch(DiatonicStep.G, 1, 4), result);
    }

    @Test
    void chromaticUnisonChangesAlterationWithoutChangingStep() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(0, Rational.of(1)));

        assertEquals(pitch(DiatonicStep.C, 1, 4), result);
    }

    @Test
    void enharmonicTranspositionPreservesChromaticHeight() {
        Pitch source = pitch(DiatonicStep.C, 1, 4);

        Pitch result = source.transposedBy(PitchInterval.of(1, Rational.ZERO));

        assertEquals(pitch(DiatonicStep.D, -1, 4), result);
    }

    @Test
    void transpositionCrossesOctaveUpward() {
        Pitch source = pitch(DiatonicStep.B, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(1, Rational.of(1)));

        assertEquals(pitch(DiatonicStep.C, 0, 5), result);
    }

    @Test
    void transpositionCrossesOctaveDownward() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(-1, Rational.of(-1)));

        assertEquals(pitch(DiatonicStep.B, 0, 3), result);
    }

    @Test
    void transpositionCrossesMultipleOctavesDownward() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(-9, Rational.of(-15)));

        assertEquals(pitch(DiatonicStep.A, 0, 2), result);
    }

    @Test
    void transpositionSupportsFractionalChromaticOffsets() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        Pitch result = source.transposedBy(PitchInterval.of(1, Rational.of(1, 2)));

        assertEquals(new Pitch(DiatonicStep.D, PitchAlteration.of(-3, 2), 4), result);
    }

    @Test
    void transpositionAndItsNegationRestoreOriginalPitch() {
        Pitch source = pitch(DiatonicStep.F, 1, 4);
        PitchInterval interval = PitchInterval.of(-9, Rational.of(-29, 2));

        Pitch result = source.transposedBy(interval).transposedBy(interval.negated());

        assertEquals(source, result);
    }

    @Test
    void transpositionRejectsNullInterval() {
        Pitch source = pitch(DiatonicStep.C, 0, 4);

        assertThrows(NullPointerException.class, () -> source.transposedBy(null));
    }

    @Test
    void transpositionRejectsOctaveOverflow() {
        Pitch source = pitch(DiatonicStep.C, 0, Integer.MAX_VALUE);

        assertThrows(ArithmeticException.class, () -> source.transposedBy(PitchInterval.of(7, Rational.of(12))));
    }

    @Test
    void transpositionRejectsOctaveUnderflow() {
        Pitch source = pitch(DiatonicStep.C, 0, Integer.MIN_VALUE);

        assertThrows(ArithmeticException.class, () -> source.transposedBy(PitchInterval.of(-7, Rational.of(-12))));
    }

    @Test
    void zeroIntervalPreservesPitchAtOctaveBoundaries() {
        Pitch lowest = pitch(DiatonicStep.C, 0, Integer.MIN_VALUE);
        Pitch highest = pitch(DiatonicStep.B, 0, Integer.MAX_VALUE);

        assertEquals(lowest, lowest.transposedBy(PitchInterval.ZERO));
        assertEquals(highest, highest.transposedBy(PitchInterval.ZERO));
    }

    private static Pitch pitch(DiatonicStep step, long alteration, int octave) {
        return new Pitch(step, PitchAlteration.of(alteration), octave);
    }
}
