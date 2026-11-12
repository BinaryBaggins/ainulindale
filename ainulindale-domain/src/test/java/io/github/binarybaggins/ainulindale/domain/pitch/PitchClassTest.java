package io.github.binarybaggins.ainulindale.domain.pitch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PitchClassTest {

    @Test
    void preservesStructuralComponents() {
        PitchClass pitchClass = PitchClass.of(DiatonicStep.C, PitchAlteration.of(1));

        assertEquals(DiatonicStep.C, pitchClass.diatonicStep());
        assertEquals(PitchAlteration.of(1), pitchClass.pitchAlteration());
    }

    @Test
    void equalPitchClassesHaveEqualValues() {
        PitchClass first = PitchClass.of(DiatonicStep.C, PitchAlteration.of(1));
        PitchClass second = PitchClass.of(DiatonicStep.C, PitchAlteration.of(1));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void enharmonicPitchClassesRemainDistinct() {
        PitchClass cSharp = PitchClass.of(DiatonicStep.C, PitchAlteration.of(1));
        PitchClass dFlat = PitchClass.of(DiatonicStep.D, PitchAlteration.of(-1));

        assertNotEquals(cSharp, dFlat);
    }

    @Test
    void differentAlterationsRemainDistinct() {
        PitchClass natural = PitchClass.of(DiatonicStep.C, PitchAlteration.NATURAL);
        PitchClass sharp = PitchClass.of(DiatonicStep.C, PitchAlteration.of(1));

        assertNotEquals(natural, sharp);
    }

    @Test
    void supportsRationalAlterations() {
        PitchClass pitchClass = PitchClass.of(DiatonicStep.F, PitchAlteration.of(1, 2));

        assertEquals(PitchAlteration.of(1, 2), pitchClass.pitchAlteration());
    }

    @Test
    void nullDiatonicStepThrows() {
        assertThrows(NullPointerException.class, () -> PitchClass.of(null, PitchAlteration.NATURAL));
    }

    @Test
    void nullPitchAlterationThrows() {
        assertThrows(NullPointerException.class, () -> PitchClass.of(DiatonicStep.C, null));
    }
}
