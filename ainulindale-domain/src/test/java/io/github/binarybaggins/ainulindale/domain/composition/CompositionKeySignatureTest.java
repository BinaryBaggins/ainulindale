package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompositionKeySignatureTest {

    private static KeySignature signature(DiatonicStep step, PitchAlteration alteration) {
        return KeySignature.of(Map.of(step, alteration));
    }

    @Test
    void newCompositionHasNoKeySignature() {
        Composition composition = Composition.create();

        assertEquals(Optional.empty(), composition.keySignatureAt(ScorePosition.ZERO));
    }

    @Test
    void keySignatureAppliesFromGivenPosition() {
        Composition composition = Composition.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        composition.setKeySignature(ScorePosition.of(2, 1), signature);

        assertEquals(Optional.empty(), composition.keySignatureAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(signature), composition.keySignatureAt(ScorePosition.of(2, 1)));
        assertEquals(Optional.of(signature), composition.keySignatureAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterKeySignatureChangeReplacesEffectiveSignature() {
        Composition composition = Composition.create();
        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, first);
        composition.setKeySignature(ScorePosition.of(4, 1), second);

        assertEquals(Optional.of(first), composition.keySignatureAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(second), composition.keySignatureAt(ScorePosition.of(4, 1)));
    }

    @Test
    void settingKeySignatureAtSamePositionReplacesTransition() {
        Composition composition = Composition.create();
        ScorePosition position = ScorePosition.of(2, 1);

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(position, first);
        composition.setKeySignature(position, second);

        assertEquals(Optional.of(second), composition.keySignatureAt(position));
    }

    @Test
    void endingKeySignatureMakesItUnspecified() {
        Composition composition = Composition.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        composition.setKeySignature(ScorePosition.ZERO, signature);
        composition.endKeySignature(ScorePosition.of(4, 1));

        assertEquals(Optional.of(signature), composition.keySignatureAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), composition.keySignatureAt(ScorePosition.of(4, 1)));
    }

    @Test
    void keySignatureCanBeSetAfterEnd() {
        Composition composition = Composition.create();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, first);
        composition.endKeySignature(ScorePosition.of(4, 1));
        composition.setKeySignature(ScorePosition.of(8, 1), second);

        assertEquals(Optional.empty(), composition.keySignatureAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(second), composition.keySignatureAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removingKeySignatureChangeRestoresPreviousTransition() {
        Composition composition = Composition.create();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        composition.setKeySignature(ScorePosition.ZERO, first);
        composition.setKeySignature(ScorePosition.of(4, 1), second);

        assertInstanceOf(Success.class, composition.removeKeySignatureChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(first), composition.keySignatureAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingEndRestoresPreviousKeySignature() {
        Composition composition = Composition.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        composition.setKeySignature(ScorePosition.ZERO, signature);
        composition.endKeySignature(ScorePosition.of(4, 1));

        assertInstanceOf(Success.class, composition.removeKeySignatureChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(signature), composition.keySignatureAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingFirstTransitionRestoresUnspecifiedState() {
        Composition composition = Composition.create();

        composition.setKeySignature(ScorePosition.ZERO, signature(DiatonicStep.F, PitchAlteration.of(1)));

        assertInstanceOf(Success.class, composition.removeKeySignatureChange(ScorePosition.ZERO));

        assertEquals(Optional.empty(), composition.keySignatureAt(ScorePosition.ZERO));
    }

    @Test
    void removingUnknownKeySignatureChangeReturnsFailure() {
        Composition composition = Composition.create();

        Failure<?> failure = assertInstanceOf(Failure.class, composition.removeKeySignatureChange(ScorePosition.ZERO));

        assertEquals(CompositionErrors.KEY_SIGNATURE_CHANGE_NOT_FOUND, failure.error());
    }

    @Test
    void explicitNaturalSignatureIsNotUnspecifiedState() {
        Composition composition = Composition.create();
        KeySignature natural = KeySignature.of(Map.of());

        composition.setKeySignature(ScorePosition.ZERO, natural);

        assertEquals(Optional.of(natural), composition.keySignatureAt(ScorePosition.ZERO));

        for (DiatonicStep step : DiatonicStep.values()) {
            assertEquals(
                PitchAlteration.NATURAL,
                composition.keySignatureAt(ScorePosition.ZERO).orElseThrow().alterationFor(step)
            );
        }
    }

    @Test
    void rationalAlterationsArePreserved() {
        Composition composition = Composition.create();

        KeySignature microtonal = KeySignature.of(
            Map.of(DiatonicStep.F, PitchAlteration.of(1, 2), DiatonicStep.B, PitchAlteration.of(-1, 2))
        );

        composition.setKeySignature(ScorePosition.ZERO, microtonal);

        assertEquals(Optional.of(microtonal), composition.keySignatureAt(ScorePosition.ZERO));
        assertEquals(
            PitchAlteration.of(1, 2),
            composition.keySignatureAt(ScorePosition.ZERO).orElseThrow().alterationFor(DiatonicStep.F)
        );
    }

    @Test
    void keySignatureChangesDoNotAffectCompositionStructure() {
        Composition composition = Composition.create();
        composition.addPart();

        int originalPartCount = composition.parts().size();

        composition.setKeySignature(ScorePosition.ZERO, signature(DiatonicStep.F, PitchAlteration.of(1)));
        composition.endKeySignature(ScorePosition.of(4, 1));

        assertEquals(originalPartCount, composition.parts().size());
        assertTrue(composition.parts().getFirst().voices().size() >= 1);
    }

    @Test
    void keySignatureOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        KeySignature signature = KeySignature.of(Map.of());

        assertThrows(NullPointerException.class, () -> composition.setKeySignature(null, signature));
        assertThrows(NullPointerException.class, () -> composition.setKeySignature(ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> composition.endKeySignature(null));
        assertThrows(NullPointerException.class, () -> composition.removeKeySignatureChange(null));
        assertThrows(NullPointerException.class, () -> composition.keySignatureAt(null));
    }
}
