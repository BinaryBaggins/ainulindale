package io.github.binarybaggins.ainulindale.domain.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KeySignatureTest {

    @Test
    void emptyMapProducesNaturalAlterationsForAllSteps() {
        KeySignature signature = KeySignature.of(Map.of());

        for (DiatonicStep step : DiatonicStep.values()) {
            assertEquals(PitchAlteration.NATURAL, signature.alterationFor(step));
        }
    }

    @Test
    void unspecifiedStepsDefaultToNatural() {
        KeySignature signature = KeySignature.of(Map.of(DiatonicStep.F, PitchAlteration.of(1)));

        for (DiatonicStep step : DiatonicStep.values()) {
            PitchAlteration expected = step == DiatonicStep.F ? PitchAlteration.of(1) : PitchAlteration.NATURAL;

            assertEquals(expected, signature.alterationFor(step));
        }
    }

    @Test
    void preservesRationalAndNegativeAlterations() {
        KeySignature signature = KeySignature.of(
            Map.of(DiatonicStep.F, PitchAlteration.of(1, 2), DiatonicStep.B, PitchAlteration.of(-1))
        );

        assertEquals(PitchAlteration.of(1, 2), signature.alterationFor(DiatonicStep.F));
        assertEquals(PitchAlteration.of(-1), signature.alterationFor(DiatonicStep.B));
        assertEquals(PitchAlteration.NATURAL, signature.alterationFor(DiatonicStep.C));
    }

    @Test
    void sparseAndCompleteMappingsAreEqual() {
        KeySignature sparse = KeySignature.of(Map.of(DiatonicStep.F, PitchAlteration.of(1)));

        EnumMap<DiatonicStep, PitchAlteration> complete = new EnumMap<>(DiatonicStep.class);

        for (DiatonicStep step : DiatonicStep.values()) {
            complete.put(step, PitchAlteration.NATURAL);
        }
        complete.put(DiatonicStep.F, PitchAlteration.of(1));

        KeySignature explicit = KeySignature.of(complete);

        assertEquals(sparse, explicit);
        assertEquals(explicit, sparse);
    }

    @Test
    void differentEffectiveAlterationsAreNotEqual() {
        KeySignature first = KeySignature.of(Map.of(DiatonicStep.F, PitchAlteration.of(1)));

        KeySignature second = KeySignature.of(Map.of(DiatonicStep.F, PitchAlteration.of(-1)));

        assertNotEquals(first, second);
    }

    @Test
    void equalSignaturesHaveEqualHashCodes() {
        KeySignature first = KeySignature.of(Map.of(DiatonicStep.F, PitchAlteration.of(1)));

        KeySignature second = KeySignature.of(
            Map.of(DiatonicStep.F, PitchAlteration.of(1), DiatonicStep.C, PitchAlteration.NATURAL)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexiveAndRejectsOtherTypes() {
        KeySignature signature = KeySignature.of(Map.of());

        assertEquals(signature, signature);
        assertNotEquals(null, signature);
        assertNotEquals("KeySignature", signature);
    }

    @Test
    void sourceMapChangesDoNotAffectSignature() {
        EnumMap<DiatonicStep, PitchAlteration> source = new EnumMap<>(DiatonicStep.class);

        source.put(DiatonicStep.F, PitchAlteration.of(1));

        KeySignature signature = KeySignature.of(source);

        source.put(DiatonicStep.F, PitchAlteration.of(-1));
        source.put(DiatonicStep.C, PitchAlteration.of(1));
        source.remove(DiatonicStep.F);

        assertEquals(PitchAlteration.of(1), signature.alterationFor(DiatonicStep.F));
        assertEquals(PitchAlteration.NATURAL, signature.alterationFor(DiatonicStep.C));
    }

    @Test
    void rejectsNullMap() {
        assertThrows(NullPointerException.class, () -> KeySignature.of(null));
    }

    @Test
    void rejectsNullStepInMap() {
        assertThrows(NullPointerException.class, () ->
            KeySignature.of(java.util.Collections.singletonMap(null, PitchAlteration.of(1)))
        );
    }

    @Test
    void rejectsNullAlterationInMap() {
        EnumMap<DiatonicStep, PitchAlteration> source = new EnumMap<>(DiatonicStep.class);

        source.put(DiatonicStep.F, null);

        assertThrows(NullPointerException.class, () -> KeySignature.of(source));
    }

    @Test
    void alterationForRejectsNullStep() {
        KeySignature signature = KeySignature.of(Map.of());

        assertThrows(NullPointerException.class, () -> signature.alterationFor(null));
    }
}
