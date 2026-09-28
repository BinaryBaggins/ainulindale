package io.github.binarybaggins.ainulindale.domain.pitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
